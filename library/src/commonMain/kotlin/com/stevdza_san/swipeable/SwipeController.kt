package com.stevdza_san.swipeable

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.stevdza_san.swipeable.domain.SwipeAction
import com.stevdza_san.swipeable.domain.SwipeBehavior
import com.stevdza_san.swipeable.domain.SwipeDirection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * State holder that encapsulates swipe offset, reveal state, and animation/progress helpers
 * for the [Swipeable] composable.
 *
 * Created once via `remember` inside [Swipeable] and kept alive across recompositions.
 * Mutable properties ([animationSpec], [onSwipeProgress], [maxDragDistancePx],
 * [finalLeftActions], [finalRightActions]) are updated each composition so the controller
 * always reflects the latest caller-provided values without needing to be recreated.
 *
 * @param coroutineScope Scope used to launch fire-and-forget close animations.
 */
internal class SwipeController(
    private val coroutineScope: CoroutineScope,
) {
    val offsetX = Animatable(0f)
    var isRevealed by mutableStateOf(false)
    var swipeDirection by mutableStateOf<SwipeDirection?>(null)

    // --- Updated each recomposition by Swipeable ---
    var animationSpec: AnimationSpec<Float> = tween(300)
    var onSwipeProgress: ((progress: Float, direction: SwipeDirection?) -> Unit)? = null
    var maxDragDistancePx: Float = 0f

    /**
     * Resolved action list for the left (start) side.
     * Either the single [SwipeBehavior.DISMISS] action or the full [SwipeBehavior.REVEAL] list.
     */
    var finalLeftActions: List<SwipeAction> = emptyList()

    /**
     * Resolved action list for the right (end) side.
     * Either the single [SwipeBehavior.DISMISS] action or the full [SwipeBehavior.REVEAL] list.
     */
    var finalRightActions: List<SwipeAction> = emptyList()

    // --- Internal haptic-tracking state (no recomposition needed) ---
    var thresholdHapticTriggered = false
    var lastHapticProgress = 0f
    /** Progress milestones (25 / 50 / 75 / 100) already triggered in this gesture. */
    val progressMilestones = mutableSetOf<Int>()

    /** Resets all per-gesture haptic tracking; call at the start of every `onDragEnd`. */
    fun resetHapticState() {
        thresholdHapticTriggered = false
        lastHapticProgress = 0f
        progressMilestones.clear()
    }

    // --- Helpers ---
    /**
     * Reports the current swipe progress (0–1) and direction to [onSwipeProgress].
     * Direction is inferred from the sign of [offsetX]; null means centered.
     */
    fun notifySwipeProgress() {
        onSwipeProgress?.let { callback ->
            val progress = (abs(offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
            val direction = when {
                offsetX.value > 0 -> SwipeDirection.RIGHT
                offsetX.value < 0 -> SwipeDirection.LEFT
                else -> null
            }
            callback(progress, direction)
        }
    }

    /**
     * Animates [offsetX] to [targetValue] using [animationSpec], notifying
     * [onSwipeProgress] on every animation frame.
     */
    suspend fun animateToWithProgress(targetValue: Float) {
        offsetX.animateTo(
            targetValue = targetValue,
            animationSpec = animationSpec,
        ) {
            notifySwipeProgress()
        }
    }

    /**
     * Collapses the revealed panel back to the closed (0) position.
     * Fires-and-forgets via [coroutineScope] so it can be called from
     * non-suspend click handlers.
     */
    fun closeReveal() {
        coroutineScope.launch {
            isRevealed = false
            swipeDirection = null
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = animationSpec,
            ) {
                onSwipeProgress?.let { callback ->
                    val progress = (abs(offsetX.value) / maxDragDistancePx).coerceIn(0f, 1f)
                    callback(progress, null)
                }
            }
        }
    }
}
