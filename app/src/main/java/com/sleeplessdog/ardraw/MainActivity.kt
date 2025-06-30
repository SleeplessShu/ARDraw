package com.sleeplessdog.ardraw

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import com.sleeplessdog.ardraw.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
/*        window.statusBarColor = ContextCompat.getColor(this, R.color.On_Tertiary_Fixed)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.On_Tertiary_Fixed)*/
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }*/

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val navController = navHostFragment.navController

        val bottomNavigationView = binding.bottomNavigationMenu
        bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            when (item.itemId) {
                R.id.drawFragment -> {
                    if (currentDestination != R.id.drawFragment) {
                        if (currentDestination == R.id.settingsFragment) {
                            navController.navigate(
                                R.id.drawFragment, null, getNavOptions(false)
                            )
                        } else {
                            navController.navigate(
                                R.id.drawFragment, null, getNavOptions(true)
                            )
                        }
                    }
                    true
                }

                R.id.historyFragment -> {
                    if (currentDestination != R.id.historyFragment) {
                        navController.navigate(R.id.historyFragment, null, getNavOptions(false))
                    }
                    true
                }

                R.id.settingsFragment -> {
                    if (currentDestination != R.id.settingsFragment) {
                        navController.navigate(R.id.settingsFragment, null, getNavOptions(true))
                    }
                    true
                }

                else -> false
            }
        }
    }

    private fun bottomNavigationVisible(isVisible: Boolean) {
        findViewById<View>(R.id.bottomNavigationMenu)?.isVisible = isVisible
        //findViewById<View>(R.id.view_line)?.isVisible = isVisible
    }

    private fun getNavOptions(slideRight: Boolean): androidx.navigation.NavOptions {
        when (slideRight) {
            true -> {
                return androidx.navigation.NavOptions.Builder().setEnterAnim(R.anim.slide_in_right)
                    .setExitAnim(R.anim.slide_out_left).setPopEnterAnim(R.anim.slide_in_left)
                    .setPopExitAnim(R.anim.slide_out_right).build()
            }

            false -> {
                return androidx.navigation.NavOptions.Builder().setEnterAnim(R.anim.slide_in_left)
                    .setExitAnim(R.anim.slide_out_right).setPopEnterAnim(R.anim.slide_in_right)
                    .setPopExitAnim(R.anim.slide_out_left).build()
            }
        }
    }
}