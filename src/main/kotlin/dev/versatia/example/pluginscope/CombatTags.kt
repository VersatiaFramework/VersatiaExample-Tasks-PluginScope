package dev.versatia.example.pluginscope

import dev.versatia.config.Value
import dev.versatia.di.LocalBean
import dev.versatia.task.PluginScope
import dev.versatia.task.keyedJobs
import dev.versatia.text.Text
import dev.versatia.text.arg
import java.util.UUID
import java.util.logging.Logger
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.delay
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerQuitEvent

/**
 * One coroutine per tagged player, launched in the plugin's scope: a new hit replaces it, so the
 * timer starts over. Only hits that really happen count: the listener runs last (`MONITOR`) and
 * skips cancelled damage. The plugin's stop cancels them all; a reload leaves them running.
 */
@LocalBean
class CombatTags(private val logger: Logger, scope: PluginScope, private val text: Text) : Listener {
    @Value("combat-tag.duration")
    var duration = 10.seconds

    private val tags = scope.keyedJobs<UUID>()

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val player = event.entity as? Player ?: return
        if (event.damager !is LivingEntity) return
        val tagged = player.uniqueId in tags
        logger.info(if (tagged) "${player.name} hit again, the tag starts over" else "${player.name} tagged for $duration")
        if (!tagged) player.sendMessage(text.parse("<warning>You are in combat for {duration}; leaving now kills you", arg("duration", duration)))
        tags.launch(player.uniqueId) {
            delay(duration)
            player.sendMessage(text.parse("<positive>You are out of combat"))
            logger.info("${player.name} is safe")
        }
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        val player = event.player
        if (tags.cancel(player.uniqueId)) {
            player.health = 0.0
            logger.info("${player.name} left in combat and was killed")
        }
    }
}
