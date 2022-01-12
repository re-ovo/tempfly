package me.rerere.tempfly.util

import me.rerere.tempfly.TempFlyPlugin
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import kotlin.math.abs

private const val SameLocationDiff = 0.1

val Player.nearGround: Boolean get() = location.block.getRelative(BlockFace.DOWN).collisionShape.boundingBoxes.isNotEmpty()

fun Location.sameLocation(other: Location): Boolean {
    return abs( x - other.x) <= SameLocationDiff && abs(y - other.y) <= SameLocationDiff && abs(z - other.z) <= SameLocationDiff
}

val GameMode.flyable: Boolean get() = when(this){
    GameMode.SURVIVAL -> false
    GameMode.ADVENTURE -> false
    else -> true
}

val Player.allowUseTempFly: Boolean get() {
    if(otherSourceFlyAbility) return true

    val data = TempFlyPlugin.playerDataManager.getData(this)
    return ((data?.time ?: 0L) > 0 || hasPermission("tempfly.infinite")) // has time to fly
            // not in disabled worlds
            && !TempFlyPlugin.config.getStringList("setting.disabled_worlds").contains(world.name)
            // not in pvp
            && System.currentTimeMillis() - (data?.lastFight ?: 0L) >= TempFlyPlugin.config.getLong("setting.pvp.pvp_cooldown", 0) * 1000
}

/**
 * 玩家是否有其他来源的飞行能力，比如 ess fly, gamemode fly
 */
val Player.otherSourceFlyAbility: Boolean get() {
    if(TempFlyPlugin.config.getStringList("setting.compatibility").any { hasPermission(it) }){
        return true
    }
    if(gameMode.flyable){
        return true
    }
    return false
}