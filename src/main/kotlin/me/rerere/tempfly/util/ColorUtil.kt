package me.rerere.tempfly.util

import java.util.regex.Matcher
import java.util.regex.Pattern

fun String.color(colorCode: Char = '&'): String =
    net.md_5.bungee.api.ChatColor.translateAlternateColorCodes(colorCode, with(HexColorUtil) { this@color.toHex() })

fun List<String>.color(colorCode: Char = '&'): List<String> = this.map { it.color(colorCode) }

object HexColorUtil {
    private val PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})")

    fun String.toHex(): String {
        var text = this
        var matcher: Matcher = PATTERN.matcher(text)
        while (matcher.find()) {
            val color = text.substring(matcher.start(), matcher.end())
            text = text.replace(color, net.md_5.bungee.api.ChatColor.of(color.substring(1)).toString())
            matcher = PATTERN.matcher(text) // 更新matcher
        }
        return text
    }
}
