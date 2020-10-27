package com.theapache64.chequered

import kotlinx.browser.document
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent

fun main() {
    // Add enter listener for input
    val inputNewClg = document.getElementById("input_new_challenge")!! as HTMLInputElement
    inputNewClg.addEventListener("keyup", { event ->
        event as KeyboardEvent

    })
}