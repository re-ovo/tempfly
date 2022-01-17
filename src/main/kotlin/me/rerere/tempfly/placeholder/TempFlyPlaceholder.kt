package me.rerere.tempfly.placeholder

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.util.formatToString
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

object TempFlyPlaceholder : PlaceholderExpansion() {
    override fun getIdentifier(): String = "tempfly"

    override fun getAuthor(): String = TempFlyPlugin.description.authors.joinToString(", ")

    override fun getVersion(): String = TempFlyPlugin.description.version

    override fun persist(): Boolean = true

    override fun onPlaceholderRequest(player: Player?, params: String): String {
        return when(params){
            "time" -> player?.let {
                TempFlyPlugin.playerDataManager.getData(it)?.time?.toString()
            } ?: "null"
            "time_format" -> player?.let {
                TempFlyPlugin.playerDataManager.getData(it)?.time?.seconds?.formatToString()
            } ?: "null"
            else -> ""
        }
    }
}