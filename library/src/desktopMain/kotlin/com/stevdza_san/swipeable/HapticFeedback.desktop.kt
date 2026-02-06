package com.stevdza_san.swipeable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity

/**
 * Desktop implementation of haptic feedback.
 * Since desktop platforms don't support haptic feedback, this is a no-op implementation.
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    return remember {
        DesktopHapticFeedback()
    }
}

private class DesktopHapticFeedback : HapticFeedback {
    override fun performHapticFeedback(intensity: HapticFeedbackIntensity) {
        // No-op: Desktop platforms don't support haptic feedback
    }
}
