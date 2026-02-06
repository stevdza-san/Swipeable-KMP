package com.stevdza_san.swipeable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity
import platform.UIKit.UIImpactFeedbackGenerator
import platform.UIKit.UIImpactFeedbackStyle

/**
 * iOS implementation of haptic feedback using UIImpactFeedbackGenerator.
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    return remember {
        IosHapticFeedback()
    }
}

private class IosHapticFeedback : HapticFeedback {
    // Cache generators for each intensity level for better performance
    private val lightGenerator = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleLight
    )
    private val mediumGenerator = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleMedium
    )
    private val heavyGenerator = UIImpactFeedbackGenerator(
        style = UIImpactFeedbackStyle.UIImpactFeedbackStyleHeavy
    )

    init {
        // Prepare all generators for better performance
        lightGenerator.prepare()
        mediumGenerator.prepare()
        heavyGenerator.prepare()
    }

    override fun performHapticFeedback(intensity: HapticFeedbackIntensity) {
        val generator = when (intensity) {
            HapticFeedbackIntensity.LIGHT -> lightGenerator
            HapticFeedbackIntensity.MEDIUM -> mediumGenerator
            HapticFeedbackIntensity.HEAVY -> heavyGenerator
        }
        
        generator.impactOccurred()
        // Re-prepare for the next haptic feedback
        generator.prepare()
    }
}
