package me.rerere.tempfly.core

import org.bukkit.Location
import java.util.*

data class PlayerData(
   val uuid: UUID,
   var name: String,
   var time: Long,
   var lastBonusDay: String,

   var prevLocation: Location? = null,
   var lastFight: Long = 0L
)