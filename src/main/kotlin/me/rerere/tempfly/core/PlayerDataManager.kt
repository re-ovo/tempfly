package me.rerere.tempfly.core

import me.clip.placeholderapi.PlaceholderAPI
import me.rerere.tempfly.TempFlyPlugin
import me.rerere.tempfly.locale
import me.rerere.tempfly.util.*
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import java.util.*
import kotlin.time.Duration

class PlayerDataManager {
    private val data = hashMapOf<UUID, PlayerData>().also {
        // Plugman Compatibility
        Bukkit.getOnlinePlayers().forEach(this::load)
    }

    init {
        Bukkit.getScheduler().runTaskTimer(TempFlyPlugin, Runnable {
            check()
        }, 20, 20)
    }

    fun reload(){
        Bukkit.getOnlinePlayers().forEach {
            quit(it)
            load(it)
        }
    }

    fun load(player: Player) {
        async {
            val playerData = TempFlyPlugin.storageManager.storageEngine.load(
                uuid = player.uniqueId,
                defaultData = PlayerData(
                    uuid = player.uniqueId,
                    name = player.name,
                    time = Duration.parse(TempFlyPlugin.config.getString("setting.bonus.first") ?: "0s").inWholeSeconds,
                    lastBonusDay = ""
                )
            ).apply {
                // update name
                name = player.name

                // daily bonus
                if(!today().equals(lastBonusDay.trim(), true)) {
                    TempFlyPlugin.config.getConfigurationSection("setting.bonus.daily")?.let { section ->
                        section.getKeys(false).forEach { group ->
                            if (player.hasPermission("tempfly.bonus.daily.$group")) {
                                val duration = Duration.parse(section.getString(group) ?: "0s")
                                time += duration.inWholeSeconds
                                player.sendMessage(locale("bonus").replace("%length%", duration.toString()).color())
                            }
                        }
                    }
                    lastBonusDay = today()
                }
            }

            sync {
                data[player.uniqueId] = playerData
            }
        }
    }

    fun quit(player: Player) {
        val playerData = data[player.uniqueId]
        data -= player.uniqueId
        async {
            playerData?.let {
                TempFlyPlugin.storageManager.storageEngine.save(it)
            }
        }
    }

    private fun check() {
        val groundTimer = TempFlyPlugin.config.getBoolean("setting.timer.ground", false)
        val idleTimer = TempFlyPlugin.config.getBoolean("setting.timer.idle", false)
        data.values.forEach { playerData ->
            val player = Bukkit.getPlayer(playerData.uuid)
            player?.let {
                if(!player.allowFlight && !player.otherSourceFlyAbility && player.allowUseTempFly){
                    player.allowFlight = true
                }
                if(player.allowFlight && !player.otherSourceFlyAbility && !player.allowUseTempFly){
                    player.isFlying = false
                    player.allowFlight = false
                }

                if (it.isFlying && !player.gameMode.flyable && !player.otherSourceFlyAbility  && !it.hasPermission("tempfly.infinite") && !it.isInsideVehicle) {
                    if (!groundTimer && player.nearGround) {
                        return@let
                    }
                    if (!idleTimer && playerData.prevLocation?.sameLocation(player.location) == true) {
                        return@let
                    }
                    playerData.prevLocation = player.location.clone()

                    if(playerData.time > 0) {
                        playerData.time--

                        // action bar
                        if(TempFlyPlugin.config.getBoolean("setting.actionbar.enable", true)){
                            it.spigot().sendMessage(
                                ChatMessageType.ACTION_BAR,
                                TextComponent(
                                    PlaceholderAPI.setPlaceholders(it, TempFlyPlugin.config.getString("setting.actionbar.format")?.color() ?: "")
                                )
                            )
                        }

                        if(playerData.time <= 5){
                            player.playSound(player.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1f,1f)
                        }

                        if(playerData.time == 0L){
                            it.sendMessage(locale("end").color())
                        }
                    }
                }
            }
        }
    }

    fun getData(player: Player) = data[player.uniqueId]

    fun getData(uuid: UUID) = data[uuid]
}