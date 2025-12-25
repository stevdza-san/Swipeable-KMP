package com.stevdza_san.swipeable.domain

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Sealed class representing different background configurations for swipe surfaces.
 */
sealed class SwipeBackground {
    /**
     * Solid color background configuration.
     * @param color The solid color to use for the background
     */
    data class Solid(val color: Color) : SwipeBackground()

    /**
     * Gradient background configuration.
     * @param brush The gradient brush to use for the background
     */
    data class Gradient(val brush: Brush) : SwipeBackground()

    companion object {
        /**
         * Creates a solid color background.
         */
        fun solid(color: Color) = Solid(color)

        /**
         * Creates a linear gradient background.
         */
        fun linearGradient(
            colors: List<Color>,
            startX: Float = 0.0f,
            startY: Float = 0.0f,
            endX: Float = Float.POSITIVE_INFINITY,
            endY: Float = 0.0f,
        ) = Gradient(
            Brush.linearGradient(
                colors,
                start = Offset(x = startX, y = startY),
                end = Offset(x = endX, y = endY)
            )
        )

        /**
         * Creates a radial gradient background.
         */
        fun radialGradient(
            colors: List<Color>,
            centerX: Float = Float.POSITIVE_INFINITY,
            centerY: Float = Float.POSITIVE_INFINITY,
            radius: Float = Float.POSITIVE_INFINITY,
        ) = Gradient(
            Brush.radialGradient(
                colors,
                center = Offset(x = centerX, y = centerY),
                radius = radius
            )
        )
    }
}