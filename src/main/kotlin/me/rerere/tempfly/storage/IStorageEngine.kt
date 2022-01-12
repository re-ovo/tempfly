package me.rerere.tempfly.storage

import me.rerere.tempfly.core.PlayerData
import java.util.*

interface IStorageEngine {
    fun init() {}

    fun close() {}

    fun load(
        uuid: UUID,
        defaultData: PlayerData
    ): PlayerData

    fun save(
        playerData: PlayerData
    )
}