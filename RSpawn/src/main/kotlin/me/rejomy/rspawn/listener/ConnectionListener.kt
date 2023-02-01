package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ConnectionListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        val player: Player = event.player

        if(INSTANCE.config.getBoolean("Teleport.join"))
            player.teleport(INSTANCE.spawn)

    }

    @EventHandler
    fun onQuit(event: PlayerJoinEvent) {
        val player: Player = event.player
        val name = player.name
        if(cooldown.containsKey(name)) {

            val taskID: Int = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, {
                if(player.isOnline) {

                    cooldown[player.name] = cooldown[player.name]!! - 1

                    if (cooldown[player.name]!! < 0) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban Regomy Опять фигню сделал!")

                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle").replace("\$delay", "${cooldown[player.name]}")
                            .replace("&", "§")
                    )
                }
            }, 20, 20)

            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                if(player.isOnline) {
                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.subtitle").replace("&", "§")
                    )
                    player.teleport(INSTANCE.respawn)
                    Bukkit.dispatchCommand(
                        Bukkit.getConsoleSender(),
                        "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.post-gamemode") + " " + player.name
                    )
                    cooldown.remove(player.name)
                }
                Bukkit.getScheduler().cancelTask(taskID)
            }, (cooldown[player.name]!! * 20).toLong())
        }
    }



}