package me.rerere.tempfly.command

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.command.subcommands.BalCommand
import me.rerere.tempfly.command.subcommands.PayCommand
import me.rerere.tempfly.command.subcommands.ReloadCommand
import me.rerere.tempfly.command.subcommands.SetCommand
import me.rerere.tempfly.util.color
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter

class TempFlyCommand : CommandExecutor, TabCompleter {
    private val subCommands = listOf(
        BalCommand(),
        PayCommand(),
        SetCommand(),
        ReloadCommand()
    )

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (args.isEmpty()) {
            sender.sendMessage("&bTempFly &7| &f${TempFlyPlugin.description.version} &7| &fBy: &bRE".color())
            subCommands.filter {
                it.permission.isEmpty() || sender.hasPermission(it.permission)
            }.forEach {
                sender.sendMessage("&b/tempfly &a${it.name} &b${it.args} &7- &f${it.description}".color())
            }
        } else {
            subCommands.firstOrNull { it.name.equals(args[0], true) }?.let {
                if(it.permission.isEmpty() || sender.hasPermission(it.permission)) {
                    it.execute(sender, args.copyOfRange(1, args.size))
                } else {
                    sender.sendMessage("&c没有权限!".color())
                }
            } ?: kotlin.run {
                sender.sendMessage("&c不存在这个命令: ${args[0]}".color())
            }
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        if (args.isEmpty()) return emptyList()

        // completion for subcommands
        if (args.size == 1) {
            return subCommands
                .filter {
                    (it.permission.isEmpty() || sender.hasPermission(it.permission)) && it.name.startsWith(args[0], true)
                }
                .map { it.name }
                .sorted()
                .toList()
        }

        return subCommands.firstOrNull {
            it.name.equals(args[0], true) && (it.permission.isEmpty() || sender.hasPermission(it.permission))
        }?.let {
            val slot = args.size - 2
            val arg = args.last()
            it.giveSuggestion(slot, arg)
                .filter { suggestion -> suggestion.startsWith(arg, true) }
                .sorted()
        } ?: emptyList()
    }
}