package me.rejomy.rspawn

import me.rejomy.rspawn.command.Spawn
import me.rejomy.rspawn.listener.ConnectionListener
import me.rejomy.rspawn.listener.DeathListener
import me.rejomy.rspawn.listener.FightListener
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

lateinit var INSTANCE: Main

class Main : JavaPlugin() {

    var delay = config.getStringList("Prevent death.Rebirth.Delay.permissions")
    var defaultDelay = config.getInt("Prevent death.Rebirth.Delay.default")
    var spawn: Location? = null
    var respawn: Location? = null

    override fun onEnable() {
        INSTANCE = this

        saveDefaultConfig()

        val file = File(dataFolder, "location.yml")

        if(file.exists()) {
            val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
            spawn = Location(Bukkit.getWorld(config.getString("Spawn.world")),
                config.getDouble("Spawn.x"),
                config.getDouble("Spawn.y"),
                config.getDouble("Spawn.z"),
                config.getDouble("Spawn.yaw").toFloat(),
                config.getDouble("Spawn.pitch").toFloat())

            respawn = Location(Bukkit.getWorld(config.getString("Respawn.world")),
                config.getDouble("Respawn.x"),
                config.getDouble("Respawn.y"),
                config.getDouble("Respawn.z"),
                config.getDouble("Respawn.yaw").toFloat(),
                config.getDouble("Respawn.pitch").toFloat())
        }

        Bukkit.getPluginManager().registerEvents(ConnectionListener(), this)
        Bukkit.getPluginManager().registerEvents(DeathListener(), this)
        Bukkit.getPluginManager().registerEvents(FightListener(), this)
        getCommand("spawn").executor = Spawn()

    }


    override fun onDisable() {

    }

}
