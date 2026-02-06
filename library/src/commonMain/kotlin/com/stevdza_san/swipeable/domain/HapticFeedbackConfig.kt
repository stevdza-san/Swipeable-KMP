package com.stevdza_san.swipeable.domain

/**
 * Configuration for haptic feedback during swipe gestures.
 *
 * @param enabled Whether haptic feedback is enabled
 * @param mode The haptic feedback mode determining when feedback is triggered
 * @param intensity The intensity level of haptic feedback (platform-specific interpretation)
 */
data class HapticFeedbackConfig(
    val enabled: Boolean = true,
    val mode: HapticFeedbackMode = HapticFeedbackMode.THRESHOLD_ONCE,
    val intensity: HapticFeedbackIntensity = HapticFeedbackIntensity.MEDIUM
) {
    companion object {
        /**
         * Default configuration: enabled, triggers once at threshold, medium intensity
         */
        val Default = HapticFeedbackConfig()
        
        /**
         * Disabled haptic feedback
         */
        val Disabled = HapticFeedbackConfig(enabled = false)
        
        /**
         * Continuous haptic feedback while swiping
         */
        val Continuous = HapticFeedbackConfig(
            mode = HapticFeedbackMode.CONTINUOUS
        )
        
        /**
         * Haptic feedback at progress milestones (25%, 50%, 75%, 100%)
         */
        val ProgressSteps = HapticFeedbackConfig(
            mode = HapticFeedbackMode.PROGRESS_STEPS
        )
    }
}

/**
 * Defines when haptic feedback is triggered during swipe gestures.
 */
enum class HapticFeedbackMode {
    /**
     * Trigger haptic feedback once when the swipe threshold is reached.
     * This is the default mode and provides clear feedback when the action will be triggered.
     */
    THRESHOLD_ONCE,
    
    /**
     * Trigger haptic feedback continuously while swiping.
     * Creates a tactile sensation throughout the swipe gesture.
     * Feedback is throttled to avoid overwhelming the user (triggers every ~50-100ms of significant movement).
     */
    CONTINUOUS,
    
    /**
     * Trigger haptic feedback at specific progress milestones (25%, 50%, 75%, 100% of threshold).
     * Provides incremental feedback as the user approaches the threshold.
     */
    PROGRESS_STEPS
}

/**
 * Intensity level for haptic feedback.
 * The actual implementation is platform-specific:
 * - Android: Maps to HapticFeedbackConstants (LIGHT_TICK, CLOCK_TICK, CONTEXT_CLICK)
 * - iOS: Maps to UIImpactFeedbackStyle (Light, Medium, Heavy)
 * - Desktop/WasmJS: No-op (these platforms don't support haptic feedback)
 */
enum class HapticFeedbackIntensity {
    LIGHT,
    MEDIUM,
    HEAVY
}
