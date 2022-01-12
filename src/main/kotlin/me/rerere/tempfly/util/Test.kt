package me.rerere.tempfly.util

import kotlin.time.Duration

fun main() {
    println(Duration.parse("1h").inWholeSeconds)
    println(Duration.parse("1m").inWholeSeconds)
}