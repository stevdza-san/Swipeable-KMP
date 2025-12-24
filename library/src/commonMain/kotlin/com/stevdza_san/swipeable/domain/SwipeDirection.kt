package com.stevdza_san.swipeable.domain

import androidx.compose.runtime.Stable

/**
 * Defines the permitted swipe directions for the Swipeable component.
 * 
 * This enum controls which horizontal swipe gestures are allowed, enabling
 * developers to restrict swipe functionality to specific directions based on
 * their UI requirements.
 */
@Stable
enum class SwipeDirection {
    /**
     * Allow swiping right only (positive X direction).
     * 
     * When set, users can only swipe from left to right, which reveals
     * left-side actions (leftRevealActions or leftDismissAction).
     * Left swipes are ignored.
     */
    LEFT,

    /**
     * Allow swiping left only (negative X direction).
     * 
     * When set, users can only swipe from right to left, which reveals
     * right-side actions (rightRevealActions or rightDismissAction).
     * Right swipes are ignored.
     */
    RIGHT,

    /**
     * Allow swiping in both directions (bidirectional).
     * 
     * Users can swipe both left and right, revealing actions on both sides.
     * This provides maximum flexibility but requires actions to be defined
     * for both sides to be useful.
     */
    BOTH
}