package de.jet.minecraft.extension.paper

import de.jet.library.tool.smart.Identity
import de.jet.minecraft.app.JetCache
import de.jet.minecraft.tool.permission.Approval
import org.bukkit.OfflinePlayer
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.permissions.Permissible

fun Permissible.hasApproval(approval: Approval) =
	approval.hasApproval(this)

@Suppress("DEPRECATION")
var LivingEntity.quickMaxHealth: Double
	get() = getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue ?: maxHealth
	set(value) {
		getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = value
	}

fun LivingEntity.maxOutHealth() {
	health = quickMaxHealth
}

val Player.identityObject: Identity<Player>
	get() = Identity("$uniqueId")

val OfflinePlayer.identityObject: Identity<OfflinePlayer>
	get() = Identity("$uniqueId")

var OfflinePlayer.buildMode: Boolean
	get() = JetCache.buildModePlayers.contains(identityObject)
	set(value) {
		if (value) {
			JetCache.buildModePlayers.add(identityObject)
		} else
			JetCache.buildModePlayers.remove(identityObject)
	}

// TODO: 22.08.2021 Cooldowns here