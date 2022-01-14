package me.rerere.tempfly.command.subcommands

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.command.SubCommand
import me.rerere.tempfly.util.color
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import kotlin.time.Duration.Companion.seconds

class BalCommand : SubCommand(
    name = "bal",
    args = "[玩家]",
    description = "查询玩家飞行余额"
){
    override fun execute(sender: CommandSender, args: Array<String>) {
        val target = if(args.isEmpty()) {
            sender as Player
        } else {
            Bukkit.getPlayerExact(args[0]) ?: return
        }
        var balance = TempFlyPlugin.playerDataManager.getData(target)?.time?.seconds?.toString()
        if(target.hasPermission("tempfly.infinite")){
            balance += "&7(无限时长)"
        }
        sender.sendMessage("&a玩家飞行时间余额: &f${TempFlyPlugin.playerDataManager.getData(target)?.time?.seconds}".color())
    }

    override fun giveSuggestion(slot: Int, arg: String): List<String> = when(slot){
        0 -> onlinePlayerNames
        else -> emptyList()
    }
}