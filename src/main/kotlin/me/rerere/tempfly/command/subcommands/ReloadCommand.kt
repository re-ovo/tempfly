package me.rerere.tempfly.command.subcommands

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.command.SubCommand
import me.rerere.tempfly.util.async
import me.rerere.tempfly.util.color
import me.rerere.tempfly.util.sync
import org.bukkit.command.CommandSender

class ReloadCommand : SubCommand(
    name = "reload",
    args = "",
    description = "重载插件",
    permission = "tempfly.reload"
) {
    override fun execute(sender: CommandSender, args: Array<String>) {
        async {
            TempFlyPlugin.apply {
                saveDefaultConfig()
                reloadConfig()

                storageManager.close()
                storageManager.init()

                playerDataManager.reload()

                sync {
                    sender.sendMessage("&a重载完成".color())
                }
            }
        }
    }
}