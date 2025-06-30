package com.sleeplessdog.ardraw.draw.presentation

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View

class CameraOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    var overlayBitmap: Bitmap? = null
    var transparency: Float = 0.4f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        overlayBitmap?.let {
            val paint = Paint().apply {
                alpha = (255 * transparency).toInt()
            }
            canvas.drawBitmap(it, null, Rect(0, 0, width, height), paint)
        }
    }
}