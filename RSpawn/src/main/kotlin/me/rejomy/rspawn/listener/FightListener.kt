package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.util.getDelay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent

object FightListener : Listener {
    private val damager = mutableMapOf<String, String>()

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if(event.entity !is Player || !INSTANCE.config.getBoolean("Prevent death.enable")) return

        // Links
        val player: Player = event.entity as Player
        val loc = player.location

        if(player.gameMode == GameMode.SPECTATOR) {
            event.isCancelled = true
            return;
        }

        // check if player health < damage
        if(player.health > event.finalDamage) return

        // Links 2
        val dname: String = if(event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) damager[player.name]!! else event.cause.name

        // drop items
        for(item in player.inventory.contents) {
            player.inventory.remove(item)
            loc.world.dropItemNaturally(loc, item)
        }
        for(item in player.inventory.armorContents) {
            loc.world.dropItemNaturally(loc, item)
        }
        for(i in 1..4) {
            player.inventory.armorContents[i] = null
        }

        // call death event
        Bukkit.getPluginManager().callEvent(PlayerDeathEvent(player,
            player.inventory.contents.toMutableList(), player.expToLevel, "Player has been killed $dname"))

        //damage effect
        player.damage(0.0)

        if(INSTANCE.config.getBoolean("Prevent death.Rebirth.enable")) {
            if(event.cause == EntityDamageEvent.DamageCause.VOID) player.teleport(INSTANCE.spawn)
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.pre-gamemode") + " " + player.name)
            val taskID: Int
            var delay = getDelay(player)

            player.sendTitle(INSTANCE.config.getString("Death.title"),
                INSTANCE.config.getString("Death.subtitle").replace("\$killer", dname))

            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, {
                delay -= 1

                if(delay < 0) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban Regomy Опять фигню сделал!")

                player.sendTitle(INSTANCE.config.getString("Prevent death.Rebirth.Delay.title"),
                    INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle").replace("\$delay", "$delay"))

            }, 20, 20)

            Bukkit.getScheduler().scheduleSyncDelayedTask(INSTANCE, {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.post-gamemode") + " " + player.name)
                Bukkit.getScheduler().cancelTask(taskID)
            }, (delay * 20).toLong())
        } else {
            player.teleport(INSTANCE.spawn)
        }

        // set statistic
        for(effect in player.activePotionEffects) player.removePotionEffect(effect.type)
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1)
        if(Bukkit.getPlayer(damager[player.name]!!) != null) Bukkit.getPlayer(damager[player.name]!!).setStatistic(Statistic.PLAYER_KILLS,
            Bukkit.getPlayer(damager[player.name]!!).getStatistic(Statistic.PLAYER_KILLS) + 1)
        player.foodLevel = 20
        player.health = player.maxHealth
        player.exp = 0F


    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if(event.entity !is Player) return
        damager.put(event.entity.name, event.damager.name)!!
    }

}