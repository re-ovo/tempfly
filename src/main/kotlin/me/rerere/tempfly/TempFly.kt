package me.rerere.tempfly

import me.rerere.tempfly.command.TempFlyCommand
import me.rerere.tempfly.core.PlayerDataManager
import me.rerere.tempfly.listener.PlayerListener
import me.rerere.tempfly.placeholder.TempFlyPlaceholder
import me.rerere.tempfly.storage.StorageManager
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.time.Duration
import kotlin.time.Duration.Companion.hours

class TempFly : JavaPlugin() {
    lateinit var storageManager: StorageManager
    lateinit var playerDataManager: PlayerDataManager

    init {
        instance = this
    }

    override fun onEnable() {
        logger.info("Loading Config")
        saveDefaultConfig()
        reloadConfig()

        checkMiss()

        logger.info("Loading StorageManager")
        storageManager = StorageManager()
        storageManager.init()

        logger.info("Loading PlayerManagers")
        playerDataManager = PlayerDataManager()

        logger.info("Registering command")
        Bukkit.getPluginCommand("tempfly")?.apply {
            val tempFlyCommand = TempFlyCommand()
            setExecutor(tempFlyCommand)
            tabCompleter = tempFlyCommand
        }

        logger.info("Registering listenr")
        Bukkit.getPluginManager().registerEvents(PlayerListener(), this)

        logger.info("Hook with PlaceholderAPI")
        TempFlyPlaceholder.register()
    }

    override fun onDisable() {
        TempFlyPlaceholder.unregister()
        storageManager.close()
    }

    private fun checkMiss(){
        val systemConfig = YamlConfiguration.loadConfiguration(InputStreamReader(getResource("config.yml")!!,StandardCharsets.UTF_8))
        systemConfig.getConfigurationSection("messages")?.apply {
            getKeys(false).forEach {
                if(!config.contains("messages.$it", true)){
                    config["messages.$it"] = get(it)
                    saveConfig()
                    logger.info("Add missing message: $it")
                }
            }
        }
    }

    companion object {
        lateinit var instance: TempFly
    }
}

val TempFlyPlugin = TempFly.instance

fun locale(key: String) = TempFlyPlugin.config.getString("messages.$key", "未知消息: $key")!!