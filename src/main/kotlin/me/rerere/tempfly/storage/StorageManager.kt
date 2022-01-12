package me.rerere.tempfly.storage

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.storage.engine.FileEngine
import me.rerere.tempfly.storage.engine.MysqlEngine

class StorageManager {
    lateinit var storageEngine: IStorageEngine

    fun init() {
        val engineName = TempFlyPlugin.config.getString("database.type") ?: "file"
        storageEngine = when(engineName.lowercase()){
            "file" -> {
                FileEngine()
            }
            "mysql" -> {
                MysqlEngine()
            }
            else -> error("Not support storage engine: $engineName")
        }.also {
            it.init()
        }
        TempFlyPlugin.logger.info("Loaded storage engine: $engineName")
    }

    fun close(){
        storageEngine.close()
    }
}