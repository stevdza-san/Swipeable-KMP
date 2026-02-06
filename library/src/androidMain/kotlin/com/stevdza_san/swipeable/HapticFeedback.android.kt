package com.stevdza_san.swipeable

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity

/**
 * Android implementation of haptic feedback using both View.performHapticFeedback 
 * and Vibrator service as fallback.
 * 
 * Note: Requires VIBRATE permission in AndroidManifest.xml:
 * <uses-permission android:name="android.permission.VIBRATE" />
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    val view = LocalView.current
    val context = LocalContext.current
    return remember(view, context) {
        AndroidHapticFeedback(view, context)
    }
}

private class AndroidHapticFeedback(
    private val view: View,
    private val context: Context
) : HapticFeedback {
    
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }
    
    override fun performHapticFeedback(intensity: HapticFeedbackIntensity) {
        // Try View haptic feedback first (more efficient, no permission needed)
        val success = tryViewHapticFeedback(intensity)
        
        // If View haptic feedback failed, use Vibrator as fallback (requires VIBRATE permission)
        if (!success) {
            useVibratorFallback(intensity)
        }
    }
    
    private fun tryViewHapticFeedback(intensity: HapticFeedbackIntensity): Boolean {
        val feedbackConstant = when (intensity) {
            HapticFeedbackIntensity.LIGHT -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    HapticFeedbackConstants.TEXT_HANDLE_MOVE
                } else {
                    HapticFeedbackConstants.CLOCK_TICK
                }
            }
            HapticFeedbackIntensity.MEDIUM -> HapticFeedbackConstants.CLOCK_TICK
            HapticFeedbackIntensity.HEAVY -> HapticFeedbackConstants.CONTEXT_CLICK
        }
        
        return view.performHapticFeedback(
            feedbackConstant,
            HapticFeedbackConstants.FLAG_IGNORE_VIEW_SETTING
        )
    }
    
    private fun useVibratorFallback(intensity: HapticFeedbackIntensity) {
        val vib = vibrator ?: return
        
        if (!vib.hasVibrator()) return
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Use VibrationEffect for Android 8.0+ (API 26+)
            val effect = when (intensity) {
                HapticFeedbackIntensity.LIGHT -> VibrationEffect.createOneShot(10, 50)
                HapticFeedbackIntensity.MEDIUM -> VibrationEffect.createOneShot(20, 100)
                HapticFeedbackIntensity.HEAVY -> VibrationEffect.createOneShot(30, 150)
            }
            vib.vibrate(effect)
        } else {
            // Fallback for older Android versions
            @Suppress("DEPRECATION")
            val duration = when (intensity) {
                HapticFeedbackIntensity.LIGHT -> 10L
                HapticFeedbackIntensity.MEDIUM -> 20L
                HapticFeedbackIntensity.HEAVY -> 30L
            }
            @Suppress("DEPRECATION")
            vib.vibrate(duration)
        }
    }
}
