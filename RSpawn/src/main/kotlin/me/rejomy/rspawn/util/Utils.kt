package me.rejomy.rspawn.util

import me.rejomy.rspawn.INSTANCE
import org.bukkit.entity.Player

fun getDelay(player: Player): Int {
    for (amount in INSTANCE.delay) {
        val line = amount.split(" ")
        if (player.hasPermission(line[0])) {
            return line[1].toInt()
        }
    }
    return INSTANCE.defaultDelay
}
