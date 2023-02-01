package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerRespawnEvent

class DeathListener : Listener {

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val player = event.entity.player
        if(INSTANCE.config.getBoolean("Death.auto respawn")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, { player.spigot().respawn(); player.teleport(INSTANCE.respawn)}, 4)
        }

    }

    @EventHandler
    fun onRespawn(event: PlayerRespawnEvent) {
        val player = event.player
        if(INSTANCE.config.getBoolean("Teleport.death")) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, { player.teleport(INSTANCE.respawn)}, 4)
        }
    }

}