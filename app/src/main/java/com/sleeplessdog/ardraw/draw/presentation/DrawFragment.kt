package com.sleeplessdog.ardraw.draw.presentation

import android.Manifest
import android.R.attr.visibility
import android.content.pm.PackageManager
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat.animate
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.common.util.concurrent.ListenableFuture
import com.sleeplessdog.ardraw.R
import com.sleeplessdog.ardraw.databinding.FragmentDrawBinding
import com.sleeplessdog.ardraw.draw.domain.DrawMessageState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue
import pub.devrel.easypermissions.EasyPermissions

class DrawFragment : Fragment() {

    var currentContrast = 1f
    var currentBrightness = 0f
    private var isPanelVisible = true
    private var currentRotation = 0f

    private val drawViewModel: DrawViewModel by viewModel()
    private var _binding: FragmentDrawBinding? = null
    private val binding: FragmentDrawBinding get() = _binding!!
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required", Toast.LENGTH_LONG).show()
        }
    }
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            drawViewModel.saveImageToPrivateStorage(uri)
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrawBinding.inflate(inflater, container, false)
        previewView = binding.previewView
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCameraPermission()
        setupUI()
        setupObservers()

    }
    private fun checkCameraPermission(){
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }
    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().apply {
                setSurfaceProvider(binding.previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(requireContext(), "Camera init failed: ${exc.message}", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setupUI() {
        binding.contrastSlider.setLabelFormatter { value ->
            when {
                value == 0f -> "Normal"
                value < 0f -> "${value.toInt()}"
                else -> "+${value.toInt()}"
            }
        }
        binding.reference.setOnClickListener {
            val permission =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    Manifest.permission.READ_MEDIA_IMAGES
                } else {
                    Manifest.permission.READ_EXTERNAL_STORAGE
                }

            if (EasyPermissions.hasPermissions(requireContext(), permission)) {
                pickImageLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            } else {
                EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.appNeedsPermission),
                    REQUEST_CODE_STORAGE,
                    permission
                )
            }
        }
    }

    private fun setupObservers() {
        drawViewModel.playlistImage.observe(viewLifecycleOwner) { uri ->
            binding.reference.setImageURI(uri)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                drawViewModel.toastMessage.collect { message ->
                    showToast(message)
                }
            }
        }
        binding.contrastSlider.addOnChangeListener { _, value, _ ->
            currentContrast = 1f + (value / 100f)
            applyImageAdjustments(currentContrast, currentBrightness)
        }

        binding.brightnessSlider.addOnChangeListener { _, value, _ ->
            currentBrightness = value // -100..100
            applyImageAdjustments(currentContrast, currentBrightness)
        }

        binding.alphaSlider.addOnChangeListener { _, value, _ ->
            val alpha = value / 100f // 0..1
            binding.reference.imageAlpha = (alpha * 255).toInt()
        }

        binding.collapseSliders.setOnClickListener {
            activateToggleAnimation()
        }
    }

    private fun activateToggleAnimation() {
        currentRotation += 180f
        val targetTranslationY = if (isPanelVisible) binding.sliders.height.toFloat() else 0f
        binding.collapseSliders.animate()
            .rotation(currentRotation)
            .translationY(targetTranslationY)
            .setDuration(500)
            .start()
        binding.sliders.animate()
            .translationY(targetTranslationY)
            .alpha(if (isPanelVisible) 0f else 1f)
            .setDuration(500)
            .withStartAction {
                if (!isPanelVisible) binding.sliders.visibility = View.VISIBLE
            }
            .withEndAction {
                if (isPanelVisible) binding.sliders.visibility = View.INVISIBLE
                isPanelVisible = !isPanelVisible
            }
            .start()
    }


    private fun applySaturationToImageView(saturation: Float) {
        val matrix = ColorMatrix()
        matrix.setSaturation(saturation)
        val filter = ColorMatrixColorFilter(matrix)
        binding.reference.colorFilter = filter
    }

    private fun applyContrastToImageView(contrast: Float) {
        // Контраст в пределах 0..2, где 1f — нейтрально
        val matrix = ColorMatrix()

        // Установка контраста
        val scale = contrast
        val translate = (-0.5f * scale + 0.5f) * 255f

        matrix.set(floatArrayOf(
            scale, 0f, 0f, 0f, translate,
            0f, scale, 0f, 0f, translate,
            0f, 0f, scale, 0f, translate,
            0f, 0f, 0f, 1f, 0f
        ))

        binding.reference.colorFilter = ColorMatrixColorFilter(matrix)
    }

    private fun applyImageAdjustments(contrast: Float, brightness: Float) {
        val contrastMatrix = ColorMatrix().apply {
            val scale = contrast
            val translate = brightness * 2.55f // Преобразуем -100..100 в -255..255
            set(floatArrayOf(
                scale, 0f, 0f, 0f, translate,
                0f, scale, 0f, 0f, translate,
                0f, 0f, scale, 0f, translate,
                0f, 0f, 0f, 1f, 0f
            ))
        }

        binding.reference.colorFilter = ColorMatrixColorFilter(contrastMatrix)
    }


    private fun showToast(state: DrawMessageState) {
        val message =
            when (state) {
                DrawMessageState.PERMISSION_DECLINED -> getString(R.string.permissionDenied)
                DrawMessageState.ACCESS_GRANTED -> getString(R.string.accessToStorageGranted)
                DrawMessageState.IMAGE_NOT_SELECTED -> getString(R.string.imageNotSelected)
                DrawMessageState.EDITS_SAVED -> getString(R.string.imageEditsSaved)
                DrawMessageState.CHECK_SETTINGS_FOR_ACCESS -> getString(R.string.checkSettingsForAccess)
            }
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

    private companion object {
        const val REQUEST_CODE_STORAGE = 1001
    }
}
