package com.stevdza_san.swipeable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.stevdza_san.swipeable.domain.ActionAnimationConfig
import com.stevdza_san.swipeable.domain.ActionCustomization
import com.stevdza_san.swipeable.domain.SwipeAction
import com.stevdza_san.swipeable.domain.SwipeBehavior
import com.stevdza_san.swipeable.domain.SwipeDirection
import org.jetbrains.compose.ui.tooling.preview.Preview
import swipeablekmp.composeapp.generated.resources.Res
import swipeablekmp.composeapp.generated.resources.delete

@Composable
@Preview
fun App() {
    MaterialTheme {
        // State to track swipe progress for custom animations
        var swipeProgress by remember { mutableStateOf(0f) }
        var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) }
        
        Column(
            modifier = Modifier.systemBarsPadding()
        ) {
            Swipeable(
                modifier = Modifier
                    .padding(all = 12.dp)
                    .height(120.dp),
                behavior = SwipeBehavior.REVEAL,
                direction = SwipeDirection.RIGHT,
                threshold = 0.3f,
                actionAnimation = ActionAnimationConfig.Pendulum,
                shape = RoundedCornerShape(16.dp),
                // Custom spring animation for bouncy swipe animations
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
//                rightDismissAction = SwipeAction(
//                    customization = ActionCustomization(
//                        icon = Res.drawable.delete,
//                        iconColor = MaterialTheme.colorScheme.onErrorContainer,
//                        containerColor = MaterialTheme.colorScheme.errorContainer
//                    ),
//                    onAction = {
//                        println("Triggered!")
//                    }
//                )
                rightRevealActions = listOf(
                    SwipeAction(
                        customization = ActionCustomization(
                            icon = Res.drawable.delete,
                            iconColor = MaterialTheme.colorScheme.onErrorContainer,
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        onAction = {
                            println("Triggered!")
                        }
                    )
                ),
                onSwipeProgress = { progress, direction ->
                    swipeProgress = progress
                    swipeDirection = direction
                }
            ) {
                // Calculate dynamic visual effects based on swipe progress
                val backgroundColor = when (swipeDirection) {
                    SwipeDirection.RIGHT -> lerp(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.errorContainer,
                        swipeProgress
                    )
                    else -> MaterialTheme.colorScheme.secondaryContainer
                }
                
                val scale = 1f - (swipeProgress * 0.1f) // Slightly shrink as user swipes
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(scale)
                        .clip(shape = RoundedCornerShape(16.dp))
                        .background(backgroundColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (swipeDirection) {
                            SwipeDirection.RIGHT -> "Swipe progress: ${(swipeProgress * 100).toInt()}%"
                            SwipeDirection.LEFT -> "← Swiping left: ${(swipeProgress * 100).toInt()}%"
                            else -> "Swipe to see custom animation"
                        },
                        color = when (swipeDirection) {
                            SwipeDirection.RIGHT -> lerp(
                                MaterialTheme.colorScheme.onSecondaryContainer,
                                MaterialTheme.colorScheme.onErrorContainer,
                                swipeProgress
                            )
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        }
                    )
                }
            }
        }
    }
}