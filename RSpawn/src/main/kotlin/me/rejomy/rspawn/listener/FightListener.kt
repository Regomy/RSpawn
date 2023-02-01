package me.rejomy.rspawn.listener

import me.rejomy.rspawn.INSTANCE
import me.rejomy.rspawn.util.getDelay
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack

val cooldown: MutableMap<String, Int> = mutableMapOf()

class FightListener : Listener {
    private val damager: MutableMap<String, String> = mutableMapOf()

    @EventHandler
    fun onDamage(event: EntityDamageEvent) {
        if(event.isCancelled || event.entity !is Player || !INSTANCE.config.getBoolean("Prevent death.enable")) return

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
        val dpre: String = if(damager.containsKey(player.name)) damager[player.name]!! else "Player"

        val dname: String = if(event.cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK) dpre else event.cause.name

        // drop items
        for(item in player.inventory.contents) {
            if(item != null && item.type != null && item.type != Material.AIR) {
                player.inventory.remove(item)
                loc.world.dropItemNaturally(loc, item)
            }
        }
        for(item in player.inventory.armorContents) {
            if(item != null && item.type != null && item.type!! != Material.AIR) {
                loc.world.dropItemNaturally(loc, item)
            }
        }
        player.inventory.armorContents = arrayOf(ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR), ItemStack(Material.AIR))

        //damage effect
        player.damage(0.0)

        if(INSTANCE.config.getBoolean("Prevent death.Rebirth.enable")) {

            if(event.cause == EntityDamageEvent.DamageCause.VOID) player.teleport(INSTANCE.respawn!!.clone().subtract(0.0, 3.0, 0.0))
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "gamemode " + INSTANCE.config.getString("Prevent death.Rebirth.pre-gamemode") + " " + player.name)
            val taskID: Int
            var delay = getDelay(player)

            cooldown[player.name] = delay

            player.sendTitle(INSTANCE.config.getString("Death.title").replace("&", "§"),
                INSTANCE.config.getString("Death.subtitle").replace("\$killer", dname).replace("&", "§"))

            taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(INSTANCE, {
                if(player.isOnline) {
                    delay -= 1

                    cooldown[player.name] = delay

                    if (delay < 0) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ban Regomy Опять фигню сделал!")

                    player.sendTitle(
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.title").replace("&", "§"),
                        INSTANCE.config.getString("Prevent death.Rebirth.Delay.subtitle").replace("\$delay", "$delay")
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
            }, (delay * 20).toLong())
        } else {
            player.teleport(INSTANCE.respawn)
        }

        // call death event
        Bukkit.getPluginManager().callEvent(PlayerDeathEvent(player,
            player.inventory.contents.toMutableList(), player.expToLevel, "Player has been killed $dname"))

        // set statistic
        for(effect in player.activePotionEffects) player.removePotionEffect(effect.type)
        player.setStatistic(Statistic.DEATHS, player.getStatistic(Statistic.DEATHS) + 1)
        if(damager[player.name] != null && Bukkit.getPlayer(damager[player.name]) != null) Bukkit.getPlayer(damager[player.name]!!).setStatistic(Statistic.PLAYER_KILLS,
            Bukkit.getPlayer(damager[player.name]!!).getStatistic(Statistic.PLAYER_KILLS) + 1)
        player.foodLevel = 20
        player.health = player.maxHealth
        player.exp = 0F

        event.isCancelled = true


    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageByEntityEvent) {
        if(event.entity !is Player) return
        damager[event.entity.name] = event.damager.name
    }

}