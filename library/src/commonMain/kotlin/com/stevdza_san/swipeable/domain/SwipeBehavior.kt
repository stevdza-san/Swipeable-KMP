package com.stevdza_san.swipeable.domain

import androidx.compose.runtime.Stable

/**
 * Defines the interaction behavior of the Swipeable component after a swipe gesture.
 * 
 * This enum determines how the component responds when users swipe and reach the
 * threshold, controlling whether actions are immediately triggered or if the
 * interface remains open for further interaction.
 */
@Stable
enum class SwipeBehavior {
    /**
     * Traditional swipe-to-dismiss behavior.
     * 
     * When the swipe threshold is reached:
     * - The associated action (leftDismissAction or rightDismissAction) is immediately triggered
     * - The component automatically animates back to the center position
     * - Only one action per side is supported
     * - The swipe gesture acts as a quick action trigger
     * 
     * This is ideal for destructive actions like delete, archive, or mark as read,
     * where immediate execution is desired.
     */
    DISMISS,

    /**
     * Reveal and persist behavior for multiple actions.
     * 
     * When the swipe threshold is reached:
     * - The component stays open, revealing action buttons (leftRevealActions or rightRevealActions)
     * - Multiple interactive buttons can be displayed per side
     * - Users can tap individual action buttons to trigger specific actions
     * - Tapping the main content or swiping back closes the revealed actions
     * - The interface remains accessible for multiple interactions
     * 
     * This is ideal for offering multiple related actions like reply, forward, archive,
     * or when users need time to choose between different options.
     */
    REVEAL
}