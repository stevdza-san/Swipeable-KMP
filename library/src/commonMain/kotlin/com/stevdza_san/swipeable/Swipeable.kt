package com.stevdza_san.swipeable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.stevdza_san.swipeable.domain.ActionAnimationConfig
import com.stevdza_san.swipeable.domain.SwipeAction
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
 * @param threshold Progress threshold (0.0 to 1.0) required to trigger actions or reveal state.
 *   - For DISMISS behavior: Percentage of screen width that must be swiped
 *   - For REVEAL behavior: Percentage of maximum drag distance that must be swiped
 * @param maxDragDistance Maximum distance the content can be dragged in either direction.
 *   Controls how far content moves and the space available for action buttons in REVEAL mode.
 *   Examples: 100.dp (compact), 200.dp (default), 300.dp (spacious)
 * @param leftRevealActions List of action buttons for left side when behavior = REVEAL and swiping right
 * @param rightRevealActions List of action buttons for right side when behavior = REVEAL and swiping left
 * @param revealActionSpacing Custom spacing between action buttons in REVEAL mode. If null, spacing is 
 *   auto-calculated based on button sizes (16% of largest button, minimum 6dp)
 * @param leftDismissAction Single action for left side when behavior = DISMISS and swiping right
 * @param rightDismissAction Single action for right side when behavior = DISMISS and swiping left
 * @param shape Shape applied to both the content and background surfaces
 * @param leftContainerColor Background color for the left swipe surface (behind action buttons)
 * @param rightContainerColor Background color for the right swipe surface (behind action buttons)
 * @param actionAnimation Animation configuration for action button appearance during swipe (scale, fade, etc.)
 * @param animationSpec Animation specification for swipe transitions (snap back, reveal, dismiss animations).
 *   Use tween() for linear animations, spring() for bouncy effects, or custom AnimationSpec implementations
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
    maxDragDistance: Dp = 200.dp,
    leftRevealActions: List<SwipeAction> = emptyList(),
    rightRevealActions: List<SwipeAction> = emptyList(),
    revealActionSpacing: Dp? = null,
    leftDismissAction: SwipeAction? = null,
    rightDismissAction: SwipeAction? = null,
    shape: Shape = RoundedCornerShape(0.dp),
    leftContainerColor: Color = Color.Gray,
    rightContainerColor: Color = Color.Red,
    actionAnimation: ActionAnimationConfig = ActionAnimationConfig.Default,
    animationSpec: AnimationSpec<Float> = tween(300),
    onSwipeProgress: ((progress: Float, direction: SwipeDirection?) -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Animation state for the main content offset
    val offsetX = remember { Animatable(0f) }

    // Track if an action was triggered
    var actionTriggered by remember { mutableStateOf(false) }

    // Convert maxDragDistance parameter to pixels
    val maxDragDistancePx = with(density) { maxDragDistance.toPx() }

    // Helper function to notify about swipe progress
    fun notifySwipeProgress() {
        onSwipeProgress?.let { callback ->
            val progress = (abs(offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
            val swipeDirection = when {
                offsetX.value > 0 -> SwipeDirection.LEFT // Swiping right reveals left actions
                offsetX.value < 0 -> SwipeDirection.RIGHT // Swiping left reveals right actions
                else -> null // No swipe
            }
            callback(progress, swipeDirection)
        }
    }

    // Enhanced animate function that notifies progress during animations
    suspend fun animateToWithProgress(
        targetValue: Float,
        animationSpec: AnimationSpec<Float>,
    ) {
        offsetX.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec,
        ) {
            // Call progress callback during animation
            notifySwipeProgress()
        }
    }

    // Determine which actions to use based on behavior mode
    val finalLeftActions = when (behavior) {
        SwipeBehavior.DISMISS -> listOfNotNull(leftDismissAction)
        SwipeBehavior.REVEAL -> leftRevealActions
    }
    val finalRightActions = when (behavior) {
        SwipeBehavior.DISMISS -> listOfNotNull(rightDismissAction)
        SwipeBehavior.REVEAL -> rightRevealActions
    }

    // Track if swipe is currently revealed (for REVEAL behavior)
    var isRevealed by remember { mutableStateOf(false) }
    var revealedSide by remember { mutableStateOf<SwipeDirection?>(null) }

    Box(modifier = modifier) {
        // Background layer - Always visible behind the content
        Box(modifier = Modifier.matchParentSize()) {
            // Left actions background (shows when swiping RIGHT, revealing left actions)
            if (finalLeftActions.isNotEmpty() && (direction == SwipeDirection.LEFT || direction == SwipeDirection.BOTH) && (offsetX.value > 0 || (isRevealed && revealedSide == SwipeDirection.LEFT))) {
                val progress = (abs(offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
                when (behavior) {
                    SwipeBehavior.DISMISS -> {
                        // Single action for dismiss behavior
                        finalLeftActions.firstOrNull()?.let { action ->
                            DismissActionContent(
                                action = action,
                                progress = progress,
                                alignment = Alignment.CenterStart,
                                shape = shape,
                                containerColor = leftContainerColor,
                                animationConfig = actionAnimation
                            )
                        }
                    }

                    SwipeBehavior.REVEAL -> {
                        // Multiple actions for reveal behavior
                        RevealActionsContent(
                            actions = finalLeftActions,
                            progress = progress,
                            alignment = Alignment.CenterStart,
                            shape = shape,
                            isRevealed = isRevealed && revealedSide == SwipeDirection.LEFT,
                            customSpacing = revealActionSpacing,
                            animationConfig = actionAnimation,
                            containerColor = leftContainerColor
                        )
                    }
                }
            }

            // Right actions background (shows when swiping LEFT, revealing right actions)
            if (finalRightActions.isNotEmpty() && (direction == SwipeDirection.RIGHT || direction == SwipeDirection.BOTH) && (offsetX.value < 0 || (isRevealed && revealedSide == SwipeDirection.RIGHT))) {
                val progress = (abs(offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
                when (behavior) {
                    SwipeBehavior.DISMISS -> {
                        // Single action for dismiss behavior
                        finalRightActions.firstOrNull()?.let { action ->
                            DismissActionContent(
                                action = action,
                                progress = progress,
                                alignment = Alignment.CenterEnd,
                                shape = shape,
                                containerColor = rightContainerColor,
                                animationConfig = actionAnimation
                            )
                        }
                    }

                    SwipeBehavior.REVEAL -> {
                        // Multiple actions for reveal behavior
                        RevealActionsContent(
                            actions = finalRightActions,
                            progress = progress,
                            alignment = Alignment.CenterEnd,
                            shape = shape,
                            isRevealed = isRevealed && revealedSide == SwipeDirection.RIGHT,
                            customSpacing = revealActionSpacing,
                            animationConfig = actionAnimation,
                            containerColor = rightContainerColor
                        )
                    }
                }
            }
        }

        // Foreground content layer - Swipeable
        Box(
            modifier = Modifier
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                val currentOffset = offsetX.value
                                
                                // Smart threshold calculation based on behavior
                                val triggerThreshold = when (behavior) {
                                    SwipeBehavior.DISMISS -> {
                                        // For DISMISS: use screen width percentage (traditional)
                                        // Example: 30% of 1000px screen = 300px threshold
                                        size.width * threshold
                                    }
                                    SwipeBehavior.REVEAL -> {
                                        // For REVEAL: use percentage of max drag distance (achievable)
                                        // Example: 70% of 200dp = 140dp threshold (always achievable)
                                        maxDragDistancePx * threshold
                                    }
                                }

                                when (behavior) {
                                    SwipeBehavior.DISMISS -> {
                                        // Traditional dismiss behavior - trigger action and animate back
                                        when {
                                            // Left swipe (negative offset, revealing right action)
                                            currentOffset < -triggerThreshold && finalRightActions.isNotEmpty() -> {
                                                finalRightActions.firstOrNull()?.onAction()
                                                actionTriggered = true
                                            }

                                            // Right swipe (positive offset, revealing left action)
                                            currentOffset > triggerThreshold && finalLeftActions.isNotEmpty() -> {
                                                finalLeftActions.firstOrNull()?.onAction()
                                                actionTriggered = true
                                            }
                                        }

                                        // Always animate back to center in dismiss mode
                                        animateToWithProgress(
                                            targetValue = 0f,
                                            animationSpec = animationSpec
                                        )
                                        actionTriggered = false
                                    }

                                    SwipeBehavior.REVEAL -> {
                                        // Reveal behavior - stay open if threshold reached, otherwise snap back
                                        when {
                                            // Left swipe (negative offset, revealing right actions)
                                            currentOffset < -triggerThreshold && finalRightActions.isNotEmpty() -> {

                                                isRevealed = true
                                                revealedSide = SwipeDirection.RIGHT
                                                animateToWithProgress(
                                                    targetValue = -maxDragDistancePx,
                                                    animationSpec = animationSpec
                                                )
                                            }

                                            // Right swipe (positive offset, revealing left actions)
                                            currentOffset > triggerThreshold && finalLeftActions.isNotEmpty() -> {

                                                isRevealed = true
                                                revealedSide = SwipeDirection.LEFT
                                                animateToWithProgress(
                                                    targetValue = maxDragDistancePx,
                                                    animationSpec = animationSpec
                                                )
                                            }

                                            else -> {
                                                // Snap back to center if threshold not reached
                                                isRevealed = false
                                                revealedSide = null
                                                animateToWithProgress(
                                                    targetValue = 0f,
                                                    animationSpec = animationSpec
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) { _, dragAmount ->
                        coroutineScope.launch {
                            val newOffset = (offsetX.value + dragAmount).coerceIn(
                                -maxDragDistancePx,
                                maxDragDistancePx
                            )

                            // Only allow drag in permitted directions
                            when (direction) {
                                SwipeDirection.LEFT -> {
                                    if (newOffset >= 0) {
                                        offsetX.snapTo(newOffset)
                                    }
                                }

                                SwipeDirection.RIGHT -> {
                                    if (newOffset <= 0) {
                                        offsetX.snapTo(newOffset)
                                    }
                                }

                                SwipeDirection.BOTH -> {
                                    offsetX.snapTo(newOffset)
                                }
                            }

                            // Notify about swipe progress for custom animations
                            notifySwipeProgress()
                        }
                    }
                }
                .then(
                    if (behavior == SwipeBehavior.REVEAL && isRevealed) {
                        Modifier.clickable {
                            // Close revealed actions when tapping the content
                            coroutineScope.launch {
                                isRevealed = false
                                revealedSide = null
                                animateToWithProgress(
                                    targetValue = 0f,
                                    animationSpec = animationSpec
                                )
                            }
                        }
                    } else Modifier
                )
        ) {
            content()
        }
    }
}

@Composable
private fun BoxScope.DismissActionContent(
    action: SwipeAction,
    progress: Float,
    alignment: Alignment,
    shape: Shape,
    containerColor: Color,
    animationConfig: ActionAnimationConfig = ActionAnimationConfig.Default,
) {
    val containerAlpha = if (animationConfig.enableBackgroundFade) {
        (progress * 1.0f).coerceIn(0f, 1.0f)
    } else 1f

    // Full background color that fills the entire parent component
    Box(
        modifier = Modifier
            .matchParentSize() // Match the exact size of the parent
            .clip(shape) // Apply the shape (rounded corners) to match the content
            .background(containerColor.copy(alpha = containerAlpha))
    ) {
        // Reuse ActionButton for consistent icon rendering and animation
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(horizontal = 24.dp), // Add padding so icon isn't at the very edge
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
private fun BoxScope.RevealActionsContent(
    actions: List<SwipeAction>,
    progress: Float,
    alignment: Alignment,
    shape: Shape,
    isRevealed: Boolean,
    customSpacing: Dp? = null,
    containerColor: Color,
    animationConfig: ActionAnimationConfig = ActionAnimationConfig.Default,
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
                .background(containerColor.copy(alpha = containerAlpha))
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
                    .padding(horizontal = 16.dp),
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
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
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
        Icon(
            modifier = Modifier.size(action.customization.iconSize),
            painter = painterResource(action.customization.icon),
            contentDescription = action.label,
            tint = action.customization.iconColor
        )
    }
}