package de.moltenKt.paper.app.component.ui.actionbar

import de.moltenKt.core.tool.timing.calendar.TimeState
import de.moltenKt.paper.app.component.ui.actionbar.AdaptiveActionBarComponent.LayerPosition
import net.kyori.adventure.text.Component
import org.bukkit.entity.Player

class ActionBarLayer : ActionBarLayerSchematic {

	override val expiration: TimeState
	override val content: (Player) -> Component
	override val level: LayerPosition

	constructor(content: (Player) -> Component, level: LayerPosition, expiration: TimeState) {
		this.expiration = expiration
		this.content = content
		this.level = level
	}

	constructor(staticContent: Component, level: LayerPosition, expiration: TimeState) {
		this.expiration = expiration
		this.content = { staticContent }
		this.level = level
	}

}