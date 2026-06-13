package com.stevdza_san.swipeable

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.stevdza_san.swipeable.domain.ActionAnimationConfig
import com.stevdza_san.swipeable.domain.HapticFeedbackConfig
import com.stevdza_san.swipeable.domain.HapticFeedbackMode
import com.stevdza_san.swipeable.domain.IconSource
import com.stevdza_san.swipeable.domain.SwipeAction
import com.stevdza_san.swipeable.domain.SwipeBackground
import com.stevdza_san.swipeable.domain.SwipeBehavior
import com.stevdza_san.swipeable.domain.SwipeDirection
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.abs
import kotlin.math.roundToInt


/**
 * A highly customizable swipeable component that supports both dismiss and reveal behaviors.
 *
 * The component allows users to swipe horizontally to reveal action buttons or trigger dismiss actions.
 * It provides extensive customization options for animations, colors, spacing, and real-time progress tracking.
 *
 * @param modifier Modifier to be applied to the swipeable component
 * @param behavior Controls the swipe interaction mode:
 *   - DISMISS: Traditional swipe-to-dismiss with single action per side that triggers and snaps back
 *   - REVEAL: Swipe stays open, revealing multiple interactive action buttons per side
 * @param direction Permitted swipe directions (LEFT, RIGHT, or BOTH)
 * @param threshold Progress threshold (0.0 to 1.0) that controls both the trigger point and maximum drag distance.
 *   The maximum drag distance is automatically calculated as a percentage of screen width:
 *   - Formula: maxDragDistance = screenWidth * threshold
 *   - Example: threshold of 0.3 (30%) means the content can be dragged up to 30% of screen width,
 *     and the action triggers when you reach 100% of that drag distance (i.e., the full 30%)
 *   - Lower thresholds (e.g., 0.2) = shorter drag distances, easier to trigger
 *   - Higher thresholds (e.g., 0.5) = longer drag distances, requires more effort
 *   This unified approach works consistently for both DISMISS and REVEAL behaviors.
 * @param leftRevealActions List of action buttons for left side when behavior = REVEAL and swiping right
 * @param rightRevealActions List of action buttons for right side when behavior = REVEAL and swiping left
 * @param revealActionsSpacing Custom spacing between action buttons in REVEAL mode. If null, spacing is
 *   auto-calculated based on button sizes (16% of largest button, minimum 6dp)
 * @param revelActionsHorizontalPadding Horizontal padding around action buttons
 * @param leftDismissAction Single action for left side when behavior = DISMISS and swiping right
 * @param rightDismissAction Single action for right side when behavior = DISMISS and swiping left
 * @param dismissActionHorizontalPadding Horizontal padding around the action button
 * @param shape Shape applied to both the content and background surfaces
 * @param leftBackground Background configuration for the left swipe surface (behind action buttons).
 *   Can be solid color or gradient. Use SwipeBackground.solid() or SwipeBackground.linearGradient()/radialGradient()
 * @param rightBackground Background configuration for the right swipe surface (behind action buttons).
 *   Can be solid color or gradient. Use SwipeBackground.solid() or SwipeBackground.linearGradient()/radialGradient()
 * @param actionAnimation Animation configuration for action button appearance during swipe (scale, fade, etc.)
 * @param animationSpec Animation specification for swipe transitions (snap back, reveal, dismiss animations).
 *   Use tween() for linear animations, spring() for bouncy effects, or custom AnimationSpec implementations
 * @param hapticFeedbackConfig Default configuration for haptic feedback during swipe gestures (applies to both directions).
 *   Can be overridden by leftHapticFeedbackConfig or rightHapticFeedbackConfig for directional control.
 * @param leftHapticFeedbackConfig Haptic feedback configuration specifically for swiping RIGHT (revealing left actions).
 *   If null, uses hapticFeedbackConfig. Allows different haptic feedback for left vs right swipes.
 * @param rightHapticFeedbackConfig Haptic feedback configuration specifically for swiping LEFT (revealing right actions).
 *   If null, uses hapticFeedbackConfig. Allows different haptic feedback for left vs right swipes.
 * @param onSwipeProgress Callback that provides real-time swipe progress (0.0 to 1.0) and direction
 *   for implementing custom animations and visual effects
 * @param content The main content to be displayed, which can be swiped to reveal actions
 */
@Composable
fun Swipeable(
    modifier: Modifier = Modifier,
    behavior: SwipeBehavior = SwipeBehavior.DISMISS,
    direction: SwipeDirection = SwipeDirection.BOTH,
    threshold: Float = 0.3f,
    leftRevealActions: List<SwipeAction> = emptyList(),
    rightRevealActions: List<SwipeAction> = emptyList(),
    revealActionsSpacing: Dp? = null,
    revelActionsHorizontalPadding: Dp = 24.dp,
    leftDismissAction: SwipeAction? = null,
    rightDismissAction: SwipeAction? = null,
    dismissActionHorizontalPadding: Dp = 24.dp,
    shape: Shape = RoundedCornerShape(0.dp),
    leftBackground: SwipeBackground = SwipeBackground.solid(Color.Gray),
    rightBackground: SwipeBackground = SwipeBackground.solid(Color.Red),
    actionAnimation: ActionAnimationConfig = ActionAnimationConfig.Default,
    animationSpec: AnimationSpec<Float> = tween(300),
    hapticFeedbackConfig: HapticFeedbackConfig = HapticFeedbackConfig.Default,
    leftHapticFeedbackConfig: HapticFeedbackConfig? = null,
    rightHapticFeedbackConfig: HapticFeedbackConfig? = null,
    onSwipeProgress: ((progress: Float, direction: SwipeDirection?) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val hapticFeedback = rememberHapticFeedback()
    val controller = remember(key1 = coroutineScope) { SwipeController(coroutineScope) }

    // Sync caller-provided params and derived values that may change between recompositions
    controller.animationSpec = animationSpec
    controller.onSwipeProgress = onSwipeProgress
    controller.finalLeftActions = when (behavior) {
        SwipeBehavior.DISMISS -> listOfNotNull(leftDismissAction)
        SwipeBehavior.REVEAL -> leftRevealActions
    }
    controller.finalRightActions = when (behavior) {
        SwipeBehavior.DISMISS -> listOfNotNull(rightDismissAction)
        SwipeBehavior.REVEAL -> rightRevealActions
    }

    BoxWithConstraints(modifier = modifier) {
        // Calculate maxDragDistance as a percentage of screen width based on threshold.
        // For example: if threshold is 0.3 (30%), max drag distance is 30% of screen width.
        // This creates an intuitive 1:1 relationship where the threshold directly determines
        // both the trigger point and the maximum drag distance.
        controller.maxDragDistancePx = constraints.maxWidth * threshold
        val maxDragDistancePx = controller.maxDragDistancePx

        Box(modifier = Modifier) {
            // Background layer - Always visible behind the content
            Box(modifier = Modifier.matchParentSize()) {
                // Left actions background (shows when swiping RIGHT, revealing left actions)
                if (controller.finalLeftActions.isNotEmpty() && (direction == SwipeDirection.LEFT || direction == SwipeDirection.BOTH) && (controller.offsetX.value > 0 || (controller.isRevealed && controller.swipeDirection == SwipeDirection.LEFT))) {
                    val progress = (abs(controller.offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
                    when (behavior) {
                        SwipeBehavior.DISMISS -> {
                            // Single action for dismiss behavior
                            controller.finalLeftActions.firstOrNull()?.let { action ->
                                DismissActionContent(
                                    action = action,
                                    progress = progress,
                                    alignment = Alignment.CenterStart,
                                    shape = shape,
                                    background = leftBackground,
                                    horizontalPadding = dismissActionHorizontalPadding,
                                    animationConfig = actionAnimation
                                )
                            }
                        }

                        SwipeBehavior.REVEAL -> {
                            // Multiple actions for reveal behavior
                            RevealActionsContent(
                                actions = controller.finalLeftActions,
                                progress = progress,
                                alignment = Alignment.CenterStart,
                                shape = shape,
                                isRevealed = controller.isRevealed && controller.swipeDirection == SwipeDirection.LEFT,
                                customSpacing = revealActionsSpacing,
                                animationConfig = actionAnimation,
                                horizontalPadding = revelActionsHorizontalPadding,
                                background = leftBackground,
                                onClose = controller::closeReveal,
                            )
                        }
                    }
                }

                // Right actions background (shows when swiping LEFT, revealing right actions)
                if (controller.finalRightActions.isNotEmpty() && (direction == SwipeDirection.RIGHT || direction == SwipeDirection.BOTH) && (controller.offsetX.value < 0 || (controller.isRevealed && controller.swipeDirection == SwipeDirection.RIGHT))) {
                    val progress = (abs(controller.offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
                    when (behavior) {
                        SwipeBehavior.DISMISS -> {
                            // Single action for dismiss behavior
                            controller.finalRightActions.firstOrNull()?.let { action ->
                                DismissActionContent(
                                    action = action,
                                    progress = progress,
                                    alignment = Alignment.CenterEnd,
                                    shape = shape,
                                    background = rightBackground,
                                    horizontalPadding = dismissActionHorizontalPadding,
                                    animationConfig = actionAnimation
                                )
                            }
                        }

                        SwipeBehavior.REVEAL -> {
                            // Multiple actions for reveal behavior
                            RevealActionsContent(
                                actions = controller.finalRightActions,
                                progress = progress,
                                alignment = Alignment.CenterEnd,
                                shape = shape,
                                isRevealed = controller.isRevealed && controller.swipeDirection == SwipeDirection.RIGHT,
                                customSpacing = revealActionsSpacing,
                                animationConfig = actionAnimation,
                                horizontalPadding = revelActionsHorizontalPadding,
                                background = rightBackground,
                                onClose = controller::closeReveal,
                            )
                        }
                    }
                }
            }

            // Foreground content layer - Swipeable
            Box(
                modifier = Modifier
                    .offset { IntOffset(controller.offsetX.value.roundToInt(), 0) }
                    .pointerInput(controller.isRevealed) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                // Reset haptic feedback state when drag ends
                                controller.resetHapticState()

                                coroutineScope.launch {
                                    val currentOffset = controller.offsetX.value

                                    // Calculate trigger threshold as percentage of max drag distance
                                    // This works for both DISMISS and REVEAL behaviors
                                    val triggerThreshold = maxDragDistancePx * threshold

                                    when (behavior) {
                                        SwipeBehavior.DISMISS -> {
                                            // Traditional dismiss behavior - trigger action and animate back
                                            when {
                                                // Left swipe (negative offset, revealing right action)
                                                currentOffset < -triggerThreshold && controller.finalRightActions.isNotEmpty() -> {
                                                    controller.finalRightActions.firstOrNull()?.onAction()
                                                }

                                                // Right swipe (positive offset, revealing left action)
                                                currentOffset > triggerThreshold && controller.finalLeftActions.isNotEmpty() -> {
                                                    controller.finalLeftActions.firstOrNull()?.onAction()
                                                }
                                            }

                                            // Always animate back to center in dismiss mode
                                            controller.animateToWithProgress(targetValue = 0f)
                                        }

                                        SwipeBehavior.REVEAL -> {
                                            // Reveal behavior - stay open if threshold reached, otherwise snap back
                                            when {
                                                // Left swipe (negative offset, revealing right actions)
                                                currentOffset < -triggerThreshold && controller.finalRightActions.isNotEmpty() -> {
                                                    controller.isRevealed = true
                                                    controller.swipeDirection = SwipeDirection.RIGHT
                                                    controller.animateToWithProgress(targetValue = -maxDragDistancePx)
                                                }

                                                // Right swipe (positive offset, revealing left actions)
                                                currentOffset > triggerThreshold && controller.finalLeftActions.isNotEmpty() -> {
                                                    controller.isRevealed = true
                                                    controller.swipeDirection = SwipeDirection.LEFT
                                                    controller.animateToWithProgress(targetValue = maxDragDistancePx)
                                                }

                                                else -> {
                                                    // Snap back to center if threshold not reached
                                                    controller.isRevealed = false
                                                    controller.swipeDirection = null
                                                    controller.animateToWithProgress(targetValue = 0f)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        ) { _, dragAmount ->
                            coroutineScope.launch {
                                val newOffset = (controller.offsetX.value + dragAmount).coerceIn(
                                    -maxDragDistancePx,
                                    maxDragDistancePx
                                )

                                // Only allow drag in permitted directions
                                when (direction) {
                                    SwipeDirection.LEFT -> {
                                        if (newOffset >= 0) {
                                            controller.offsetX.snapTo(newOffset)
                                        }
                                    }

                                    SwipeDirection.RIGHT -> {
                                        if (newOffset <= 0) {
                                            controller.offsetX.snapTo(newOffset)
                                        }
                                    }

                                    SwipeDirection.BOTH -> {
                                        controller.offsetX.snapTo(newOffset)
                                    }
                                }

                                // Trigger haptic feedback based on configuration
                                // Determine which haptic config to use based on swipe direction
                                val activeHapticConfig = when {
                                    controller.offsetX.value > 0 -> leftHapticFeedbackConfig ?: hapticFeedbackConfig
                                    controller.offsetX.value < 0 -> rightHapticFeedbackConfig ?: hapticFeedbackConfig
                                    else -> hapticFeedbackConfig
                                }

                                if (activeHapticConfig.enabled) {
                                    // Verify there are actions available for the swipe direction
                                    val hasActions = when {
                                        controller.offsetX.value > 0 -> controller.finalLeftActions.isNotEmpty()
                                        controller.offsetX.value < 0 -> controller.finalRightActions.isNotEmpty()
                                        else -> false
                                    }

                                    if (hasActions) {
                                        val currentProgress = (abs(controller.offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)

                                        when (activeHapticConfig.mode) {
                                            HapticFeedbackMode.THRESHOLD_ONCE -> {
                                                // Trigger once when threshold is reached
                                                if (!controller.thresholdHapticTriggered && currentProgress >= threshold) {
                                                    hapticFeedback.performHapticFeedback(activeHapticConfig.intensity)
                                                    controller.thresholdHapticTriggered = true
                                                }
                                            }

                                            HapticFeedbackMode.CONTINUOUS -> {
                                                // Trigger continuously while swiping (throttled by progress change)
                                                // Only trigger if progress changed by at least 5% to avoid overwhelming
                                                if (abs(currentProgress - controller.lastHapticProgress) >= 0.05f && currentProgress > 0f) {
                                                    hapticFeedback.performHapticFeedback(activeHapticConfig.intensity)
                                                    controller.lastHapticProgress = currentProgress
                                                }
                                            }

                                            HapticFeedbackMode.PROGRESS_STEPS -> {
                                                // Trigger at 25%, 50%, 75%, and 100% of threshold
                                                val milestone = when {
                                                    currentProgress >= threshold -> 100
                                                    currentProgress >= threshold * 0.75f -> 75
                                                    currentProgress >= threshold * 0.50f -> 50
                                                    currentProgress >= threshold * 0.25f -> 25
                                                    else -> 0
                                                }

                                                if (milestone > 0 && !controller.progressMilestones.contains(milestone)) {
                                                    hapticFeedback.performHapticFeedback(activeHapticConfig.intensity)
                                                    controller.progressMilestones.add(milestone)
                                                }
                                            }
                                        }
                                    }
                                }

                                // Notify about swipe progress for custom animations
                                controller.notifySwipeProgress()
                            }
                        }
                    }
            ) {
                content()

                // Transparent overlay that intercepts all clicks when reveal is open,
                // preventing content onClick from firing and closing the panel instead.
                if (behavior == SwipeBehavior.REVEAL && controller.isRevealed) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { controller.closeReveal() }
                    )
                }
            }
        }
    }
}

@Composable
internal fun BoxScope.DismissActionContent(
    action: SwipeAction,
    progress: Float,
    alignment: Alignment,
    shape: Shape,
    background: SwipeBackground,
    horizontalPadding: Dp,
    animationConfig: ActionAnimationConfig = ActionAnimationConfig.Default,
) {
    val containerAlpha = if (animationConfig.enableBackgroundFade) {
        (progress * 1.0f).coerceIn(0f, 1.0f)
    } else 1f

    // Full background that fills the entire parent component
    Box(
        modifier = Modifier
            .matchParentSize() // Match the exact size of the parent
            .clip(shape) // Apply the shape (rounded corners) to match the content
            .then(
                when (background) {
                    is SwipeBackground.Solid -> Modifier.background(background.color.copy(alpha = containerAlpha))
                    is SwipeBackground.Gradient -> {
                        Modifier.background(background.brush)
                    }
                }
            )
    ) {
        // Reuse ActionButton for consistent icon rendering and animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = horizontalPadding), // Add padding so icon isn't at the very edge
            contentAlignment = when (alignment) {
                Alignment.CenterStart -> Alignment.CenterStart
                Alignment.CenterEnd -> Alignment.CenterEnd
                else -> Alignment.Center
            }
        ) {
            ActionButton(
                action = action,
                progress = progress,
                isInteractive = false, // No click handling in background mode
                animationConfig = animationConfig,
                onClick = { /* No-op since isInteractive = false */ }
            )
        }
    }
}

@Composable
internal fun BoxScope.RevealActionsContent(
    actions: List<SwipeAction>,
    progress: Float,
    alignment: Alignment,
    shape: Shape,
    isRevealed: Boolean,
    customSpacing: Dp? = null,
    background: SwipeBackground,
    horizontalPadding: Dp,
    animationConfig: ActionAnimationConfig = ActionAnimationConfig.Default,
    onClose: () -> Unit = {},
) {
    val containerAlpha = if (isRevealed) {
        1.0f
    } else if (animationConfig.enableBackgroundFade) {
        (progress * 1.0f).coerceIn(0f, 1.0f) // Fade from 0% to 100%
    } else 1.0f

    if (actions.isNotEmpty()) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(shape)
                .then(
                    when (background) {
                        is SwipeBackground.Solid -> Modifier.background(background.color.copy(alpha = containerAlpha))
                        is SwipeBackground.Gradient -> {
                            Modifier.background(background.brush)
                        }
                    }
                )
        ) {
            // Always show multiple action buttons for reveal behavior
            // The buttons will be fully interactive when isRevealed=true

            // Use custom spacing or calculate dynamic spacing based on button sizes
            val spacing = customSpacing ?: run {
                val maxButtonSize = actions.maxOfOrNull { it.customization.padding } ?: 48.dp
                (maxButtonSize * 0.16f).coerceAtLeast(6.dp) // 16% of largest button, minimum 6dp
            }

            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .align(if (alignment == Alignment.CenterStart) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(horizontal = horizontalPadding),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                actions.forEach { action ->
                    ActionButton(
                        action = action,
                        progress = progress, // Always use actual progress for smooth animations
                        isInteractive = isRevealed && progress > 0.9f, // Clickable when revealed AND mostly swiped
                        animationConfig = animationConfig,
                        onClick = {
                            if (isRevealed) {
                                action.onAction()
                                if (action.autoClose) { onClose() }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun ActionButton(
    action: SwipeAction,
    progress: Float = 1f,
    isInteractive: Boolean = true,
    animationConfig: ActionAnimationConfig = ActionAnimationConfig.Default,
    onClick: () -> Unit,
) {
    // Calculate animation values based on config
    val scale = if (animationConfig.enableScale) {
        val minScale = animationConfig.scaleRange.start
        val maxScale = animationConfig.scaleRange.endInclusive
        (minScale + (maxScale - minScale) * progress).coerceIn(minScale, maxScale)
    } else {
        1f
    }

    val alpha = if (animationConfig.enableFade) {
        val minAlpha = animationConfig.alphaRange.start
        val maxAlpha = animationConfig.alphaRange.endInclusive
        (minAlpha + (maxAlpha - minAlpha) * progress).coerceIn(minAlpha, maxAlpha)
    } else {
        1f
    }

    Box(
        modifier = Modifier
            .then(
                animationConfig.customModifier?.invoke(progress) ?: Modifier
            ) // Apply custom animation
            .size(action.customization.padding)
            .scale(scale)
            .alpha(alpha)
            .clip(action.customization.shape)
            .background(action.customization.containerColor)
            .then(
                if (isInteractive) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        when (val icon = action.customization.icon) {
            is IconSource.Vector -> Icon(
                imageVector = icon.imageVector,
                modifier = Modifier.size(action.customization.iconSize),
                contentDescription = action.label,
                tint = action.customization.iconColor,
            )
            is IconSource.Bitmap -> Icon(
                bitmap = icon.imageBitmap,
                modifier = Modifier.size(action.customization.iconSize),
                contentDescription = action.label,
                tint = action.customization.iconColor,
            )
            is IconSource.Resource -> Icon(
                painter = painterResource(icon.resource),
                modifier = Modifier.size(action.customization.iconSize),
                contentDescription = action.label,
                tint = action.customization.iconColor,
            )
        }
    }
}