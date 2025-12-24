# 🚀 Swipeable KMP

<div align="center">

![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose Multiplatform](https://img.shields.io/badge/Compose%20Multiplatform-4285F4?style=for-the-badge&logo=jetpack-compose&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![iOS](https://img.shields.io/badge/iOS-000000?style=for-the-badge&logo=ios&logoColor=white)
![Desktop](https://img.shields.io/badge/Desktop-FF6B6B?style=for-the-badge&logo=windows&logoColor=white)
![Web](https://img.shields.io/badge/Web%20(WASM)-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

[![Maven Central](https://img.shields.io/maven-central/v/com.stevdza-san/swipeable-kmp?style=flat-square)](https://central.sonatype.com/artifact/com.stevdza-san/swipeable-kmp)
[![License](https://img.shields.io/github/license/stevdza-san/Swipeable-KMP?style=flat-square)](LICENSE)
[![GitHub stars](https://img.shields.io/github/stars/stevdza-san/Swipeable-KMP?style=flat-square)](https://github.com/stevdza-san/Swipeable-KMP/stargazers)

**A highly customizable, modern swipeable component for Compose Multiplatform**

*Supporting Android, iOS, Desktop, and Web (WASM) with beautiful animations and intuitive gestures*

</div>

## 🎥 Demo

<p align="center">
<img src="ASSETS/demo.gif" width="256"/>
</p>

<p align="center">
<em>Experience the full range of swipe behaviors and animations</em>
</p>

---

## ✨ Features

### 🎯 **Dual Behavior Modes**
- **REVEAL**: Persistent action buttons that stay open for multiple interactions
- **DISMISS**: Quick swipe-to-action that automatically snaps back

### 🌍 **True Multiplatform**
- **Android** - Native touch gestures
- **iOS** - Smooth iOS-style interactions
- **Desktop** - Mouse and keyboard support
- **Web (WASM)** - Pure Kotlin, no JavaScript

### 🎨 **Rich Customization**
- **15+ Built-in Animations** - From subtle fades to quantum effects
- **Flexible Theming** - Colors, shapes, spacing, and more
- **Smart Thresholds** - Adaptive trigger distances
- **Real-time Progress** - Custom animation callbacks
- **Directional Control** - Left, right, or bidirectional swipes

---

## Quick Start

### Installation

Add to your `commonMain` dependencies:

```kotlin
implementation("com.stevdza-san:swipeable-kmp:1.0.0")
```

### Basic Usage

```kotlin
Swipeable(
    behavior = SwipeBehavior.REVEAL,
    rightRevealActions = listOf(
        SwipeAction(
            customization = ActionCustomization(
                icon = Icons.Default.Delete,
                iconColor = Color.White,
                containerColor = Color.Red
            ),
            onAction = { /* Delete item */ }
        )
    )
) {
    // Your content here
    Card { 
        Text("Swipe me!") 
    }
}
```

---

## 📖 Documentation

### Behavior Modes

#### REVEAL Mode
Perfect for multiple actions - swipe to reveal persistent action buttons.

```kotlin
Swipeable(
    behavior = SwipeBehavior.REVEAL,
    rightRevealActions = listOf(
        SwipeAction(/* Edit action */),
        SwipeAction(/* Share action */),
        SwipeAction(/* Delete action */)
    )
) { /* content */ }
```

#### DISMISS Mode  
Great for quick actions - swipe to trigger and automatically return.

```kotlin
Swipeable(
    behavior = SwipeBehavior.DISMISS,
    rightDismissAction = SwipeAction(
        customization = ActionCustomization(
            icon = Icons.Default.Archive,
            containerColor = Color.Blue
        ),
        onAction = { /* Archive item */ }
    )
) { /* content */ }
```

### Advanced Customization

#### Custom Animations

```kotlin
Swipeable(
    // Choose from 15+ built-in animations
    actionAnimation = ActionAnimationConfig.Quantum,
    
    // Or create custom animations
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )
) { /* content */ }
```

#### Real-time Progress Tracking

```kotlin
var swipeProgress by remember { mutableStateOf(0f) }

Swipeable(
    onSwipeProgress = { progress, direction ->
        swipeProgress = progress
        // Create custom animations based on swipe progress
    }
) { 
    Box(
        modifier = Modifier
            .scale(1f - swipeProgress * 0.1f) // Shrink as user swipes
            .background(
                lerp(Color.Blue, Color.Red, swipeProgress)
            )
    ) {
        Text("Progress: ${(swipeProgress * 100).toInt()}%")
    }
}
```

#### Fine-tuned Control

```kotlin
Swipeable(
    direction = SwipeDirection.RIGHT,      // Only allow right swipes
    threshold = 0.3f,                      // Trigger at 30% swipe
    maxDragDistance = 200.dp,              // Limit drag distance
    shape = RoundedCornerShape(16.dp),     // Custom shape
    leftContainerColor = Color.Green,      // Background colors
    rightContainerColor = Color.Red
) { /* content */ }
```

---

## Animation Showcase

### Built-in Animation Styles

| Animation | Description | Best For |
|-----------|-------------|----------|
| `Default` | Smooth scale and fade | General use |
| `Dramatic` | Strong scaling effects | Eye-catching actions |
| `Quantum` | Glitch-like teleport | Futuristic themes |
| `Pendulum` | Swinging motion | Playful interactions |
| `Bounce` | Elastic entrance | Dynamic feedback |
| `Morph` | Shape transformation | Creative interfaces |
| `Wave` | Liquid-like flow | Organic designs |

```kotlin
// Try different animations
Swipeable(actionAnimation = ActionAnimationConfig.Quantum) { /* content */ }
Swipeable(actionAnimation = ActionAnimationConfig.Pendulum) { /* content */ }  
Swipeable(actionAnimation = ActionAnimationConfig.Wave) { /* content */ }
```

### Custom Animation Creation

```kotlin
val customAnimation = ActionAnimationConfig.custom(
    enableScale = true,
    enableFade = true,
    animationModifier = { progress ->
        Modifier
            .rotate(progress * 360f)
            .offset(y = (50.dp * (1f - progress)))
    }
)
```

---

## API Reference

### Core Components

#### `Swipeable`
The main composable component.

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `behavior` | `SwipeBehavior` | `DISMISS` | Interaction mode (DISMISS/REVEAL) |
| `direction` | `SwipeDirection` | `BOTH` | Allowed swipe directions |
| `threshold` | `Float` | `0.3f` | Trigger threshold (0.0-1.0) |
| `maxDragDistance` | `Dp` | `200.dp` | Maximum drag distance |
| `actionAnimation` | `ActionAnimationConfig` | `Default` | Action button animations |
| `animationSpec` | `AnimationSpec<Float>` | `tween(300)` | Swipe transition animations |
| `onSwipeProgress` | `((Float, SwipeDirection?) -> Unit)?` | `null` | Real-time progress callback |

#### `SwipeAction`
Defines an individual action button.

```kotlin
SwipeAction(
    customization = ActionCustomization(
        icon = Res.drawable.delete,        // Action icon
        iconSize = 24.dp,                  // Icon size
        iconColor = Color.White,           // Icon tint
        containerColor = Color.Red,        // Button background
        shape = CircleShape,               // Button shape
        padding = 48.dp                    // Button size
    ),
    onAction = { /* Handle action */ },    // Action callback
    label = "Delete"                       // Accessibility label
)
```

---

## Theming & Styling

### Material Design Integration

```kotlin
Swipeable(
    rightRevealActions = listOf(
        SwipeAction(
            customization = ActionCustomization(
                icon = Icons.Default.Delete,
                iconColor = MaterialTheme.colorScheme.onError,
                containerColor = MaterialTheme.colorScheme.error,
                shape = MaterialTheme.shapes.medium
            )
        )
    ),
    shape = MaterialTheme.shapes.large,
    rightContainerColor = MaterialTheme.colorScheme.errorContainer
) { /* content */ }
```

### Custom Design Systems

```kotlin
// Define your design tokens
object SwipeTheme {
    val dangerAction = ActionCustomization(
        containerColor = Color(0xFFFF3B30),
        iconColor = Color.White,
        shape = RoundedCornerShape(12.dp)
    )
    
    val primaryAction = ActionCustomization(
        containerColor = Color(0xFF007AFF),
        iconColor = Color.White,
        shape = CircleShape
    )
}
```

---

## License

```
MIT License

Copyright (c) 2024 Stefan Jovanovic

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

<div align="center">

### 🌟 Star this repo if you find it useful!

**Made with ❤️ by [Stevdza-San](https://github.com/stevdza-san)**

[📖 Documentation](https://github.com/stevdza-san/Swipeable-KMP/wiki) • 
[🐛 Report Bug](https://github.com/stevdza-san/Swipeable-KMP/issues) • 
[💡 Request Feature](https://github.com/stevdza-san/Swipeable-KMP/issues) • 
[💬 Discussions](https://github.com/stevdza-san/Swipeable-KMP/discussions)

</div>