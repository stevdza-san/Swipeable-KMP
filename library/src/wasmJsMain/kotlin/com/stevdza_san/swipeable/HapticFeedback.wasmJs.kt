package com.stevdza_san.swipeable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity

/**
 * WasmJS implementation of haptic feedback.
 * Since web platforms don't support haptic feedback in the standard way, this is a no-op implementation.
 * Note: The Vibration API exists in browsers but is not widely supported and requires different handling.
 */
@Composable
actual fun rememberHapticFeedback(): HapticFeedback {
    return remember {
        WasmJsHapticFeedback()
    }
}

private class WasmJsHapticFeedback : HapticFeedback {
    override fun performHapticFeedback(intensity: HapticFeedbackIntensity) {
        // No-op: Web platforms don't support standard haptic feedback
        // The Vibration API could be used but requires JS interop and isn't widely supported
    }
}
