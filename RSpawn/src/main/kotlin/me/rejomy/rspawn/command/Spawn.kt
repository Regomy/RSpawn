package me.rejomy.rspawn.command

import me.rejomy.rspawn.INSTANCE
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File

class Spawn : CommandExecutor {

    override fun onCommand(
        sender: CommandSender?,
        command: Command?,
        label: String?,
        args: Array<out String>?
    ): Boolean {
        if (args == null || args.isEmpty()) {

            if(sender is Player) {

                val player: Player = sender

                if( INSTANCE.spawn == null ) {
                    sender.sendMessage("Spawn not found!")
                    return true
                }

                player.teleport(INSTANCE.spawn)
                player.sendTitle(INSTANCE.config.getString("Teleport.title").replace("&", "§"), INSTANCE.config.getString("Teleport.subtitle").replace("&", "§"))

            } else
                sender!!.sendMessage("Error! This command allowed for player!")
            return true

        } else {
            when (args[0]) {
                "set" -> {

                    if(args.size != 2 || sender !is Player) {
                        sender!!.sendMessage("Error! Illegal arguments or sender!")
                        return true
                    }

                    val player: Player = sender

                    val file = File(INSTANCE.dataFolder, File.separator + "location.yml")
                    val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)

                    if(file.exists())
                        file.createNewFile()


                        when (args[1]) {

                            "spawn" -> {
                                config.set("Spawn.world", player.world.name)
                                config.set("Spawn.x", player.location.x)
                                config.set("Spawn.y", player.location.y)
                                config.set("Spawn.z", player.location.z)
                                config.set("Spawn.yaw", player.location.yaw)
                                config.set("Spawn.pitch", player.location.pitch)

                                config.save(file)

                                INSTANCE.spawn = Location(Bukkit.getWorld(config.getString("Spawn.world")),
                                    config.getDouble("Spawn.x"),
                                    config.getDouble("Spawn.y"),
                                    config.getDouble("Spawn.z"),
                                    config.getDouble("Spawn.yaw").toFloat(),
                                    config.getDouble("Spawn.pitch").toFloat())
                                sender.sendMessage("Spawn successful set from location x: " + player.location.x + " y: " + player.location.y +
                                " z: " + player.location.z)
                            }

                            "respawn" -> {

                                config.set("Respawn.world", player.world.name)
                                config.set("Respawn.x", player.location.x)
                                config.set("Respawn.y", player.location.y)
                                config.set("Respawn.z", player.location.z)
                                config.set("Respawn.yaw", player.location.yaw)
                                config.set("Respawn.pitch", player.location.pitch)

                                config.save(file)

                                INSTANCE.respawn = Location(
                                    Bukkit.getWorld(config.getString("Respawn.world")),
                                    config.getDouble("Respawn.x"),
                                    config.getDouble("Respawn.y"),
                                    config.getDouble("Respawn.z"),
                                    config.getDouble("Respawn.yaw").toFloat(),
                                    config.getDouble("Respawn.pitch").toFloat())

                                sender.sendMessage("Respawn successful set from location x: " + player.location.x + " y: " + player.location.y +
                                        " z: " + player.location.z)
                            }

                        }

                }

                "info" -> {
                    sender!!.sendMessage("RSpawn by Rejomy v1.0")
                }

            }
        }
        return true
    }
}