package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player

        if(INSTANCE.config.getBoolean("Teleport.join"))
            player.teleport(INSTANCE.spawn)

    }

/*    @EventHandler
    fun onQuit(event: PlayerJoinEvent) {
        val player: Player = event.player


    }*/



}