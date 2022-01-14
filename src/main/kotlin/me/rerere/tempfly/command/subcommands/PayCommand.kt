package me.rerere.tempfly.command.subcommands

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.command.SubCommand
import me.rerere.tempfly.locale
import me.rerere.tempfly.util.color
import me.rerere.tempfly.util.sync
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.lang.Exception
import kotlin.time.Duration

class PayCommand : SubCommand(
    name = "pay",
    args = "<玩家> <时长>",
    description = "向其他玩家赠送时长"
) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        if(!Bukkit.isPrimaryThread()){
            sync {
                execute(sender, args)
            }
            return
        }
        if(args.size < 2) {
            sender.sendMessage("&c参数长度错误！".color())
            return
        }
        val target = Bukkit.getPlayer(args[0])
        val duration = try {
            Duration.parse(args[1]).also {
                require(it.isPositive())
            }
        }catch (e: Exception){
            sender.sendMessage("&c时长格式错误! 正确范例: &f1m, 1s, 1h".color())
            return
        }
        if(sender is Player){
            target?.let {
                val senderData = TempFlyPlugin.playerDataManager.getData(sender)!!
                val targetData = TempFlyPlugin.playerDataManager.getData(target)!!
                if(senderData.time < duration.inWholeSeconds){
                    sender.sendMessage("&c你没有那么多飞行时间！".color())
                    return
                }
                senderData.time -= duration.inWholeSeconds
                targetData.time += duration.inWholeSeconds
                sender.sendMessage(
                    locale("pay_send").replace("%player%", target.name).replace("%duration%", duration.toString()).color()
                )
                it.sendMessage(
                    locale("pay_receive").replace("%from%", sender.name).replace("%duration%", duration.toString()).color()
                )
            } ?: kotlin.run {
                sender.sendMessage(locale("not_online").color())
            }
        }
        if(sender is ConsoleCommandSender){
            target?.let {
                val targetData = TempFlyPlugin.playerDataManager.getData(target)!!
                targetData.time += duration.inWholeSeconds
                sender.sendMessage("&a你赠送了玩家 ${target.name} 飞行时间: &f$duration".color())
                it.sendMessage("&a系统赠送了你飞行时间: &f$duration".color())
            } ?: kotlin.run {
                sender.sendMessage(locale("not_online").color())
            }
        }
    }

    override fun giveSuggestion(slot: Int, arg: String): List<String> = when(slot){
        0 -> onlinePlayerNames
        else -> emptyList()
    }
}