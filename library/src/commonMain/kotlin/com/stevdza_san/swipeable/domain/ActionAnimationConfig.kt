package com.stevdza_san.swipeable.domain

import androidx.compose.foundation.layout.offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * Animation configuration for action buttons during swipe interactions.
 * Supports both built-in animations and completely custom animation functions.
 *
 * @param scaleRange Range for scaling animation (min scale to max scale)
 * @param alphaRange Range for alpha/opacity animation (min alpha to max alpha)
 * @param enableScale Whether to apply scaling animation
 * @param enableFade Whether to apply fade in/out animation to individual buttons
 * @param enableBackgroundFade Whether to apply fade in/out animation to the background layer
 * @param customModifier Optional custom modifier function that receives progress (0f-1f) and returns a Modifier
 */
data class ActionAnimationConfig(
    val scaleRange: ClosedFloatingPointRange<Float> = 0.6f..1.0f,
    val alphaRange: ClosedFloatingPointRange<Float> = 0.0f..1.0f,
    val enableScale: Boolean = true,
    val enableFade: Boolean = true,
    val enableBackgroundFade: Boolean = true,
    val customModifier: ((progress: Float) -> Modifier)? = null
) {
    companion object {
        /**
         * Default animation with moderate effects
         */
        val Default = ActionAnimationConfig(
            scaleRange = 0.6f..1.0f,
            alphaRange = 0.0f..1.0f,
            enableScale = true,
            enableFade = true
        )

        /**
         * Dramatic animation with strong scaling effects
         */
        val Dramatic = ActionAnimationConfig(
            scaleRange = 0.3f..1.0f,
            alphaRange = 0.0f..1.0f,
            enableScale = true,
            enableFade = true
        )

        /**
         * Slide in animation from the bottom
         */
        val SlideUp = ActionAnimationConfig(
            enableScale = false,
            enableFade = true,
            customModifier = { progress ->
                Modifier.offset(y = (50.dp * (1f - progress)))
            }
        )

        /**
         * Rotation animation with scaling
         */
        val Rotate = ActionAnimationConfig(
            scaleRange = 0.5f..1.0f,
            enableScale = true,
            enableFade = true,
            customModifier = { progress ->
                Modifier.graphicsLayer {
                    rotationZ = (1f - progress) * 180f
                }
            }
        )

        /**
         * Bounce effect animation
         */
        val Bounce = ActionAnimationConfig(
            enableScale = false,
            enableFade = true,
            customModifier = { progress ->
                val bounceScale = if (progress < 0.7f) {
                    0.3f + (progress / 0.7f) * 1.2f // Scale up to 120%
                } else {
                    1.2f - ((progress - 0.7f) / 0.3f) * 0.2f // Scale back down to 100%
                }
                Modifier.scale(bounceScale.coerceIn(0.3f, 1.2f))
            }
        )

        /**
         * Flip animation with 3D effect
         */
        val Flip = ActionAnimationConfig(
            enableScale = false,
            enableFade = true,
            customModifier = { progress ->
                Modifier.graphicsLayer {
                    rotationY = (1f - progress) * 90f
                    cameraDistance = 12f * density
                }
            }
        )

        /**
         * Elastic scale animation with overshoot effect
         */
        val Elastic = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val elasticScale = if (progress < 0.8f) {
                    val t = progress / 0.8f
                    0.2f + t * 1.3f // Scale to 130%
                } else {
                    val t = (progress - 0.8f) / 0.2f
                    1.3f - t * 0.3f // Scale back to 100%
                }
                Modifier.scale(elasticScale.coerceIn(0.2f, 1.3f))
            }
        )

        /**
         * Morphing animation - transforms from circle to final shape
         */
        val Morph = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                Modifier.graphicsLayer {
                    val morphScale = 0.3f + (progress * 0.7f)
                    scaleX = morphScale
                    scaleY = morphScale
                    rotationZ = (1f - progress) * 720f // Two full rotations
                }
            }
        )

        /**
         * Spring-loaded entrance with multiple bounces
         */
        val Spring = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val springEffect = when {
                    progress < 0.6f -> {
                        val t = progress / 0.6f
                        0.1f + t * 1.4f // Scale up to 140%
                    }
                    progress < 0.8f -> {
                        val t = (progress - 0.6f) / 0.2f
                        1.4f - t * 0.5f // Scale down to 90%
                    }
                    else -> {
                        val t = (progress - 0.8f) / 0.2f
                        0.9f + t * 0.1f // Scale up to final 100%
                    }
                }
                Modifier
                    .scale(springEffect.coerceIn(0.1f, 1.4f))
                    .offset(y = (30.dp * (1f - progress)))
            }
        )

        /**
         * Pendulum swing animation
         */
        val Pendulum = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val swingAngle = sin(progress * PI * 2) * 30f * (1f - progress)
                Modifier
                    .graphicsLayer {
                        rotationZ = swingAngle.toFloat()
                        transformOrigin = TransformOrigin(0.5f, 0.0f)
                    }
                    .scale(0.4f + (progress * 0.6f))
            }
        )

        /**
         * Liquid/wave entrance effect
         */
        val Wave = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val waveOffset = sin(progress * PI * 4) * 10f * (1f - progress)
                Modifier
                    .offset(
                        x = waveOffset.dp,
                        y = (40.dp * (1f - progress))
                    )
                    .graphicsLayer {
                        scaleX = 0.3f + (progress * 0.7f)
                        scaleY = 0.8f + (progress * 0.2f) + abs(sin(progress * PI * 6).toFloat()) * 0.1f
                    }
            }
        )

        /**
         * Magnetic attraction effect
         */
        val Magnetic = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val magneticPull = if (progress < 0.7f) {
                    // Slow approach
                    val t = progress / 0.7f
                    80.dp * (1f - (t * t)) // Quadratic ease-in
                } else {
                    // Sudden snap
                    val t = (progress - 0.7f) / 0.3f
                    80.dp * (1f - t) * (1f - t) * (1f - t) // Cubic ease-out
                }
                Modifier
                    .offset(x = magneticPull)
                    .scale(0.5f + (progress * 0.5f))
            }
        )

        /**
         * Origami-style folding animation
         */
        val Origami = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                Modifier.graphicsLayer {
                    val foldProgress = progress.coerceIn(0f, 1f)
                    rotationY = (1f - foldProgress) * 180f
                    rotationX = (1f - foldProgress) * 45f
                    scaleX = 0.2f + (foldProgress * 0.8f)
                    scaleY = 0.2f + (foldProgress * 0.8f)
                    cameraDistance = 8f * density
                }
            }
        )

        /**
         * Particle-style materialization
         */
        val Materialize = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                val particleEffect = sin(progress * PI * 8) * 5f * (1f - progress)
                Modifier
                    .offset(
                        x = (cos(progress * PI * 3) * particleEffect).dp,
                        y = (sin(progress * PI * 3) * particleEffect).dp
                    )
                    .graphicsLayer {
                        val materializeScale = if (progress < 0.3f) {
                            0f
                        } else {
                            ((progress - 0.3f) / 0.7f).let { t ->
                                t * t * (3f - 2f * t) // Smooth step function
                            }
                        }
                        scaleX = materializeScale
                        scaleY = materializeScale
                        rotationZ = (1f - progress) * 360f
                    }
            }
        )

        /**
         * Quantum teleport effect
         */
        val Quantum = ActionAnimationConfig(
            enableScale = false,
            enableFade = false,
            enableBackgroundFade = false,
            customModifier = { progress ->
                Modifier.graphicsLayer {
                    val quantumPhase = progress * PI * 2
                    val glitchEffect = sin(quantumPhase * 10) * 0.05f * (1f - progress)

                    scaleX = (0.1f + (progress * 0.9f)) * (1f + glitchEffect.toFloat())
                    scaleY = (0.1f + (progress * 0.9f)) * (1f - glitchEffect.toFloat())
                    rotationZ = sin(quantumPhase * 3).toFloat() * 180f * (1f - progress)

                    translationX = cos(quantumPhase * 5).toFloat() * 20f * (1f - progress)
                    translationY = sin(quantumPhase * 7).toFloat() * 15f * (1f - progress)
                }
            }
        )

        /**
         * Custom animation creator - allows users to define their own animations
         */
        fun custom(
            scaleRange: ClosedFloatingPointRange<Float> = 1.0f..1.0f,
            alphaRange: ClosedFloatingPointRange<Float> = 1.0f..1.0f,
            enableScale: Boolean = false,
            enableFade: Boolean = false,
            enableBackgroundFade: Boolean = false,
            animationModifier: (progress: Float) -> Modifier
        ) = ActionAnimationConfig(
            scaleRange = scaleRange,
            alphaRange = alphaRange,
            enableScale = enableScale,
            enableFade = enableFade,
            enableBackgroundFade = enableBackgroundFade,
            customModifier = animationModifier
        )
    }
}