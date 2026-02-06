package com.stevdza_san.swipeable

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stevdza_san.swipeable.domain.ActionAnimationConfig
import com.stevdza_san.swipeable.domain.ActionCustomization
import com.stevdza_san.swipeable.domain.HapticFeedbackConfig
import com.stevdza_san.swipeable.domain.HapticFeedbackIntensity
import com.stevdza_san.swipeable.domain.HapticFeedbackMode
import com.stevdza_san.swipeable.domain.SwipeAction
import com.stevdza_san.swipeable.domain.SwipeBackground
import com.stevdza_san.swipeable.domain.SwipeBehavior
import com.stevdza_san.swipeable.domain.SwipeDirection
import swipeablekmp.composeapp.generated.resources.Res
import swipeablekmp.composeapp.generated.resources.archive
import swipeablekmp.composeapp.generated.resources.check
import swipeablekmp.composeapp.generated.resources.delete
import swipeablekmp.composeapp.generated.resources.edit
import swipeablekmp.composeapp.generated.resources.heart
import swipeablekmp.composeapp.generated.resources.share


@Composable
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .systemBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Column {
                Text(
                    text = "Swipeable Component Showcase",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Light,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "Explore different swipe behaviors, animations, gradient backgrounds, and haptic feedback modes",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            
            // Progress Tracking with Dynamic Scaling
            ShowcaseItem(
                title = "PROGRESS TRACKING • Quantum Animation",
                description = "External progress tracking with dynamic scaling and continuous light haptic feedback while swiping"
            ) {
                ProgressTrackingExample()
            }
            
            // REVEAL Behavior with Bounce Animation
            ShowcaseItem(
                title = "REVEAL • Bounce Animation",
                description = "Swipe right to reveal actions with bounce effect and medium haptic feedback at threshold"
            ) {
                RevealBounceExample()
            }
            
            // DISMISS Behavior with Wave Animation
            ShowcaseItem(
                title = "DISMISS • Wave Animation", 
                description = "Swipe right for light continuous haptic (archive) or left for heavy milestone haptic (delete)"
            ) {
                DismissWaveExample()
            }
            
            // REVEAL Behavior with Elastic Animation
            ShowcaseItem(
                title = "REVEAL • Elastic Animation",
                description = "Swipe left to reveal actions with elastic overshoot and continuous medium haptic feedback"
            ) {
                RevealElasticExample()
            }
            
            // DISMISS Behavior with Pendulum Animation
            ShowcaseItem(
                title = "DISMISS • Pendulum Animation",
                description = "Swipe to trigger actions with pendulum swing and strong haptic feedback at threshold"
            ) {
                DismissPendulumExample()
            }
            
            // REVEAL Behavior with Custom Animation
            ShowcaseItem(
                title = "REVEAL • Custom Rotation",
                description = "Swipe right for single medium haptic (share) or left for light progressive haptic (archive)"
            ) {
                RevealCustomExample()
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ShowcaseItem(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
        content()
    }
}

@Composable
private fun StandardCard(
    modifier: Modifier = Modifier,
    title: String = "Sample Item",
    subtitle: String = "Demonstrate swipe behavior",
    trailingText: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        )
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (trailingText != null) {
                Text(
                    text = trailingText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
fun RevealBounceExample() {
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.REVEAL,
        direction = SwipeDirection.RIGHT,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        actionAnimation = ActionAnimationConfig.Bounce,
        hapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.THRESHOLD_ONCE,
            intensity = HapticFeedbackIntensity.MEDIUM
        ),
        rightRevealActions = listOf(
            SwipeAction(
                label = "Edit",
                customization = ActionCustomization(
                    icon = Res.drawable.edit,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Edit action triggered") }
            ),
            SwipeAction(
                label = "Delete",
                customization = ActionCustomization(
                    icon = Res.drawable.delete,
                    iconColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Delete action triggered") }
            )
        ),
        rightBackground = SwipeBackground.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                MaterialTheme.colorScheme.primaryContainer
            )
        )
    ) {
        StandardCard(
            title = "Bounce Animation",
            subtitle = "Actions bounce into view with elastic effect"
        )
    }
}

@Composable
fun DismissWaveExample() {
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.DISMISS,
        direction = SwipeDirection.BOTH,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        actionAnimation = ActionAnimationConfig.Wave,
        // Light continuous haptic for archive (left/non-destructive)
        leftHapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.CONTINUOUS,
            intensity = HapticFeedbackIntensity.LIGHT
        ),
        // Heavy haptic at milestones for delete (right/destructive)
        rightHapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.PROGRESS_STEPS,
            intensity = HapticFeedbackIntensity.HEAVY
        ),
        leftDismissAction = SwipeAction(
            label = "Archive",
            customization = ActionCustomization(
                icon = Res.drawable.archive,
                iconColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            onAction = { println("Archive action triggered") }
        ),
        rightDismissAction = SwipeAction(
            label = "Delete",
            customization = ActionCustomization(
                icon = Res.drawable.delete,
                iconColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onAction = { println("Delete action triggered") }
        ),
        leftBackground = SwipeBackground.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.tertiary,
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.6f)
            )
        ),
        rightBackground = SwipeBackground.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.errorContainer
            )
        )
    ) {
        StandardCard(
            title = "Wave Animation",
            subtitle = "Swipe triggers action with liquid wave effect"
        )
    }
}

@Composable
fun RevealElasticExample() {
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.REVEAL,
        direction = SwipeDirection.LEFT,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        actionAnimation = ActionAnimationConfig.Elastic,
        hapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.CONTINUOUS,
            intensity = HapticFeedbackIntensity.MEDIUM
        ),
        leftRevealActions = listOf(
            SwipeAction(
                label = "Heart",
                customization = ActionCustomization(
                    icon = Res.drawable.heart,
                    iconColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Heart action triggered") }
            ),
            SwipeAction(
                label = "Share",
                customization = ActionCustomization(
                    icon = Res.drawable.share,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Share action triggered") }
            )
        ),
        leftBackground = SwipeBackground.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            )
        )
    ) {
        StandardCard(
            title = "Elastic Animation",
            subtitle = "Actions appear with overshoot elastic effect"
        )
    }
}

@Composable
fun DismissPendulumExample() {
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.DISMISS,
        direction = SwipeDirection.BOTH,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        actionAnimation = ActionAnimationConfig.Pendulum,
        hapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.THRESHOLD_ONCE,
            intensity = HapticFeedbackIntensity.HEAVY
        ),
        leftDismissAction = SwipeAction(
            label = "Accept",
            customization = ActionCustomization(
                icon = Res.drawable.check,
                iconColor = MaterialTheme.colorScheme.onTertiary,
                containerColor = MaterialTheme.colorScheme.tertiary
            ),
            onAction = { println("Accept action triggered") }
        ),
        rightDismissAction = SwipeAction(
            label = "Reject",
            customization = ActionCustomization(
                icon = Res.drawable.delete,
                iconColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error
            ),
            onAction = { println("Reject action triggered") }
        ),
        leftBackground = SwipeBackground.solid(MaterialTheme.colorScheme.tertiary),
        rightBackground = SwipeBackground.radialGradient(
            colors = listOf(
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.onError.copy(alpha = 0.1f)
            )
        )
    ) {
        StandardCard(
            title = "Pendulum Animation",
            subtitle = "Icons swing like a pendulum during swipe"
        )
    }
}

@Composable 
fun RevealCustomExample() {
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.REVEAL,
        direction = SwipeDirection.BOTH,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        actionAnimation = ActionAnimationConfig(
            enableScale = true,
            enableFade = true,
            scaleRange = 0.3f..1.0f,
            customModifier = { progress ->
                Modifier.rotate(progress * 360f)
            }
        ),
        // Medium haptic at threshold for share (left)
        leftHapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.THRESHOLD_ONCE,
            intensity = HapticFeedbackIntensity.MEDIUM
        ),
        // Light progressive haptic for archive (right)
        rightHapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.PROGRESS_STEPS,
            intensity = HapticFeedbackIntensity.LIGHT
        ),
        leftRevealActions = listOf(
            SwipeAction(
                label = "Share",
                customization = ActionCustomization(
                    icon = Res.drawable.share,
                    iconColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Share action triggered") }
            )
        ),
        rightRevealActions = listOf(
            SwipeAction(
                label = "Archive",
                customization = ActionCustomization(
                    icon = Res.drawable.archive,
                    iconColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    padding = 48.dp,
                    shape = RoundedCornerShape(8.dp)
                ),
                onAction = { println("Archive action triggered") }
            )
        ),
        leftBackground = SwipeBackground.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.tertiaryContainer
            )
        ),
        rightBackground = SwipeBackground.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primaryContainer,
                MaterialTheme.colorScheme.surfaceVariant
            )
        )
    ) {
        StandardCard(
            title = "Custom Rotation",
            subtitle = "Icons rotate while scaling and fading in"
        )
    }
}

@Composable
fun ProgressTrackingExample() {
    var swipeProgress by remember { mutableStateOf(0f) }
    var swipeDirection by remember { mutableStateOf<SwipeDirection?>(null) }
    
    Swipeable(
        modifier = Modifier.fillMaxWidth(),
        behavior = SwipeBehavior.REVEAL,
        direction = SwipeDirection.BOTH,
        threshold = 0.7f,
        shape = RoundedCornerShape(12.dp),
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessHigh
        ),
        actionAnimation = ActionAnimationConfig.Quantum,
        hapticFeedbackConfig = HapticFeedbackConfig(
            enabled = true,
            mode = HapticFeedbackMode.CONTINUOUS,
            intensity = HapticFeedbackIntensity.LIGHT
        ),
        leftRevealActions = listOf(
            SwipeAction(
                label = "Favorite",
                customization = ActionCustomization(
                    icon = Res.drawable.heart,
                    iconColor = MaterialTheme.colorScheme.onSecondary,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    padding = 52.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
                onAction = { println("Favorite action triggered") }
            ),
            SwipeAction(
                label = "Share",
                customization = ActionCustomization(
                    icon = Res.drawable.share,
                    iconColor = MaterialTheme.colorScheme.onPrimary,
                    containerColor = MaterialTheme.colorScheme.primary,
                    padding = 52.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
                onAction = { println("Share action triggered") }
            )
        ),
        rightRevealActions = listOf(
            SwipeAction(
                label = "Archive",
                customization = ActionCustomization(
                    icon = Res.drawable.archive,
                    iconColor = MaterialTheme.colorScheme.onTertiary,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    padding = 52.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
                onAction = { println("Archive action triggered") }
            ),
            SwipeAction(
                label = "Delete",
                customization = ActionCustomization(
                    icon = Res.drawable.delete,
                    iconColor = MaterialTheme.colorScheme.onError,
                    containerColor = MaterialTheme.colorScheme.error,
                    padding = 52.dp,
                    shape = RoundedCornerShape(12.dp)
                ),
                onAction = { println("Delete action triggered") }
            )
        ),
        leftBackground = SwipeBackground.linearGradient(
            colors = listOf(
                Color(0xFF6A5ACD), // SlateBlue
                Color(0xFF9370DB), // MediumPurple  
                Color(0xFFBA55D3)  // MediumOrchid
            )
        ),
        rightBackground = SwipeBackground.linearGradient(
            colors = listOf(
                Color(0xFFFF6B6B), // Coral Red
                Color(0xFFFF8E53), // Orange
                Color(0xFFFF6B9D)  // Pink
            )
        ),
        onSwipeProgress = { progress, direction ->
            swipeProgress = progress
            swipeDirection = direction
        }
    ) {
        // Calculate dynamic scaling based on swipe progress
        val contentScale = 1f - (swipeProgress * 0.1f) // Scale from 1.0 to 0.9
        
        // Create subtle color transition based on swipe direction and progress
        val backgroundColor = when (swipeDirection) {
            SwipeDirection.LEFT -> lerp(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.secondaryContainer,
                swipeProgress * 0.3f
            )
            SwipeDirection.RIGHT -> lerp(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.tertiaryContainer,
                swipeProgress * 0.3f
            )
            else -> MaterialTheme.colorScheme.surface
        }
        
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .scale(contentScale), // Dynamic scaling based on swipe progress
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(
                defaultElevation = (1 + swipeProgress * 3).dp // Elevation increases with swipe
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                CircleShape
                            )
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Progress Tracking",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    AnimatedContent(
                        targetState = when (swipeDirection) {
                            SwipeDirection.LEFT -> "Swiping right • Progress: ${(swipeProgress * 100).toInt()}%"
                            SwipeDirection.RIGHT -> "Swiping left • Progress: ${(swipeProgress * 100).toInt()}%"
                            else -> "External progress tracking with dynamic scaling"
                        },
                        transitionSpec = {
                            (slideInVertically(
                                animationSpec = tween(300),
                                initialOffsetY = { it / 3 }
                            ) + fadeIn(
                                animationSpec = tween(300)
                            )).togetherWith(
                                slideOutVertically(
                                    animationSpec = tween(300),
                                    targetOffsetY = { -it / 3 }
                                ) + fadeOut(
                                    animationSpec = tween(300)
                                )
                            )
                        },
                        label = "progress_text_animation"
                    ) { text ->
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                // Progress indicator
                if (swipeProgress > 0f) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                when (swipeDirection) {
                                    SwipeDirection.LEFT -> MaterialTheme.colorScheme.secondary
                                    SwipeDirection.RIGHT -> MaterialTheme.colorScheme.tertiary
                                    else -> MaterialTheme.colorScheme.outline
                                },
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}