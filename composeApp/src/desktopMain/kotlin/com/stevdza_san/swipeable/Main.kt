package com.stevdza_san.swipeable

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Swipeable KMP Demo",
    ) {
        App()
    }
}