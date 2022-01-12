package me.rerere.tempfly.command.subcommands

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.command.SubCommand
import me.rerere.tempfly.util.color
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.lang.Exception
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SetCommand : SubCommand(
    name = "set",
    args = "<player> <time>",
    description = "设置玩家飞行时长",
    permission = "tempfly.set"
) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        val player = Bukkit.getPlayerExact(args[0])
        player?.let {
            val duration = try {
                Duration.parse(args[1])
            } catch (e: Exception) {
                sender.sendMessage("&c错误的时间格式".color())
                0.seconds
            }

            TempFlyPlugin.playerDataManager.getData(player)?.time = duration.inWholeSeconds
            sender.sendMessage("&a已设置玩家 ${player.name} 的飞行时长为 $duration".color())
        } ?: kotlin.run {
            sender.sendMessage("&c该玩家不在线！".color())
        }
    }

    override fun giveSuggestion(slot: Int, arg: String): List<String> = when (slot) {
        0 -> onlinePlayerNames
        else -> emptyList()
    }
}