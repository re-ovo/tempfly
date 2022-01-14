package me.rerere.tempfly.listener

import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.util.allowUseTempFly
import me.rerere.tempfly.util.otherSourceFlyAbility
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerToggleFlightEvent

class PlayerListener : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        TempFlyPlugin.playerDataManager.load(event.player)
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        TempFlyPlugin.playerDataManager.quit(event.player)
    }

    @EventHandler
    fun onToggleFlight(event: PlayerToggleFlightEvent) {
        if (event.isFlying && !event.player.allowUseTempFly) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onFight(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            TempFlyPlugin.playerDataManager.getData(event.damager as Player)?.lastFight = System.currentTimeMillis()
            TempFlyPlugin.playerDataManager.getData(event.entity as Player)?.lastFight = System.currentTimeMillis()
        }
    }

    @EventHandler
    fun onMove(event: PlayerMoveEvent){
        if(event.player.allowFlight && event.player.isOnGround && !event.player.isFlying && !event.player.otherSourceFlyAbility){
            if(event.player.fallDistance > 3){
                event.player.allowFlight = false
            }
        }
    }
}