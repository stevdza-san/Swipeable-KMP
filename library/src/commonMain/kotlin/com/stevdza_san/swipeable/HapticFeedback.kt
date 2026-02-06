package com.stevdza_san.swipeable

import androidx.compose.runtime.Composable
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity

/**
 * Platform-specific haptic feedback provider.
 * Provides tactile feedback during swipe gestures.
 */
@Composable
expect fun rememberHapticFeedback(): HapticFeedback

/**
 * Interface for platform-specific haptic feedback implementations.
 */
interface HapticFeedback {
    /**
     * Performs a haptic feedback effect with the specified intensity.
     * 
     * @param intensity The intensity level of the haptic feedback
     * 
     * Platform-specific behavior:
     * - Android: Maps to HapticFeedbackConstants (LIGHT_TICK, CLOCK_TICK, CONTEXT_CLICK)
     * - iOS: Maps to UIImpactFeedbackStyle (Light, Medium, Heavy)
     * - Desktop/WasmJS: No-op (these platforms don't support haptic feedback)
     */
    fun performHapticFeedback(intensity: HapticFeedbackIntensity = HapticFeedbackIntensity.MEDIUM)
}
