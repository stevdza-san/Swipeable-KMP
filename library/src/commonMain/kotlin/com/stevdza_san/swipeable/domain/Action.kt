package com.stevdza_san.swipeable.domain

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource

/**
 * Comprehensive customization options for action button appearance.
 *
 * @param icon The drawable resource for the action icon
 * @param iconSize Size of the icon within the button
 * @param padding Size of the button (determines touch target and visual size)
 * @param shape Shape of the button (CircleShape, RoundedCornerShape, etc.)
 * @param containerColor Background color of the button
 */
data class ActionCustomization(
    val icon: DrawableResource,
    val iconSize: Dp = 24.dp,
    val iconColor: Color,
    val shape: Shape = CircleShape,
    val padding: Dp = 48.dp,
    val containerColor: Color,
)

/**
 * Represents a single swipe action with its behavior and customization.
 *
 * @param customization Visual appearance and styling options
 * @param onAction Callback triggered when the action is performed
 * @param label Optional accessibility label for the action
 */
data class SwipeAction(
    val customization: ActionCustomization,
    val onAction: () -> Unit,
    val label: String? = null
)