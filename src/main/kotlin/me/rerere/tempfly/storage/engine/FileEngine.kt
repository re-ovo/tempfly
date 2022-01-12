package me.rerere.tempfly.storage.engine

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.core.PlayerData
import me.rerere.tempfly.storage.IStorageEngine
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.util.*

class FileEngine : IStorageEngine {
    private val folder = File(TempFlyPlugin.dataFolder, "data").apply {
        if (!exists()) {
            mkdirs()
        }
    }

    override fun load(uuid: UUID, defaultData: PlayerData): PlayerData {
        val file = File(folder, "$uuid.yml")
        return if (!file.exists()) {
            defaultData.also {
                save(defaultData)
            }
        } else {
            val configuration = YamlConfiguration.loadConfiguration(file)
            PlayerData(
                uuid = uuid,
                name = configuration.getString("name") ?: error("cant find name data"),
                time = configuration.getLong("time", 0),
                lastBonusDay = configuration.getString("bonus") ?: error("cant find bonus day")
            )
        }
    }

    override fun save(playerData: PlayerData) {
        val file = File(folder, "${playerData.uuid}.yml")
        val configuration = YamlConfiguration.loadConfiguration(file)

        configuration.set("name", playerData.name)
        configuration.set("time", playerData.time)
        configuration.set("bonus", playerData.lastBonusDay)

        configuration.save(file)
    }
}