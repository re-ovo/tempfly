package me.rerere.tempfly.command

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

abstract class SubCommand(
    val name: String,
    val args: String,
    val description: String,
    val permission: String = ""
) {
    abstract fun execute(sender: CommandSender, args: Array<String>)

    open fun giveSuggestion(slot: Int, arg: String): List<String> = emptyList()

    val onlinePlayerNames: List<String> get() = Bukkit.getOnlinePlayers().map { it.name }
}
