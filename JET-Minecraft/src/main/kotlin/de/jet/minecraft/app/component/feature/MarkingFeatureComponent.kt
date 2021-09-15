package de.jet.minecraft.app.component.feature

import de.jet.library.extension.collection.replaceVariables
import de.jet.library.extension.data.shorter
import de.jet.minecraft.app.JetCache.playerMarkerBoxes
import de.jet.minecraft.extension.display.BOLD
import de.jet.minecraft.extension.display.YELLOW
import de.jet.minecraft.extension.display.message
import de.jet.minecraft.extension.display.notification
import de.jet.minecraft.extension.display.ui.item
import de.jet.minecraft.extension.get
import de.jet.minecraft.extension.lang
import de.jet.minecraft.extension.paper.identityObject
import de.jet.minecraft.extension.system
import de.jet.minecraft.structure.app.App
import de.jet.minecraft.structure.command.Interchange
import de.jet.minecraft.structure.command.InterchangeExecutorType.PLAYER
import de.jet.minecraft.structure.command.InterchangeResult
import de.jet.minecraft.structure.command.InterchangeResult.SUCCESS
import de.jet.minecraft.structure.command.live.InterchangeAccess
import de.jet.minecraft.structure.component.Component.RunType.AUTOSTART_MUTABLE
import de.jet.minecraft.structure.component.SmartComponent
import de.jet.minecraft.tool.display.item.Item
import de.jet.minecraft.tool.display.message.Transmission.Level.*
import de.jet.minecraft.tool.position.LocationBox
import org.bukkit.FluidCollisionMode.ALWAYS
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player

class MarkingFeatureComponent(vendor: App = system) : SmartComponent(vendor, AUTOSTART_MUTABLE) {

	override val thisIdentity = "feature_marking"

	override fun component() {
		interchange(MarkingToolInterchange(vendor))
	}

	private class MarkingToolInterchange(vendor: App) : Interchange(vendor, "markingtool", requiredExecutorType = PLAYER, requiresAuthorization = true) {

		fun positionData(location: Location) = buildString {
			append('(')
			append("x=${location.x}, y=${location.y}, z=${location.z}")
			append(')')
		}

		val test = Material.STONE.item.apply {
			lore = """
				${de.jet.minecraft.extension.display.DARK_BLUE}TEST
			""".trimIndent()
		}

		val markingTool = Material.GOLDEN_HOE.item.apply {
			label = "${YELLOW}${BOLD}Marking-Tool"
			identity = "jet:marking_tool"
			lore = """
				 
				LEFT-CLICK -> Position-1
				RIGHT-CLICK -> Position-2
				SHIFT-CLICK -> VIEW DATA
				
			""".trimIndent()
			putInteractAction {
				val targetBlock = whoInteract.rayTraceBlocks(10.0, ALWAYS)?.hitBlock

				if (targetBlock != null) {
					val actualBox = playerMarkerBoxes[player.identityObject]
					val currentBox = actualBox ?: LocationBox(targetBlock.location)
					val targetLocation = targetBlock.location
					val targetPrint = positionData(targetBlock.location)
					if (!whoInteract.isSneaking) {
						when {
							action.isLeftClick -> {
								denyInteraction()

								if (actualBox?.first != targetLocation) {

									playerMarkerBoxes[player.identityObject] = currentBox.apply {
										first = targetLocation
									}
									lang["component.markingTool.action.set"].replaceVariables(
										"n" to 1,
										"pos" to targetPrint
									).notification(APPLIED, whoInteract).display()
									lang["component.markingTool.action.view.distance.other"].replaceVariables(
										"distance" to targetLocation.distance(currentBox.last).shorter
									).message(whoInteract).display()
								} else
									lang["component.markingTool.action.duplicate"].replaceVariables(
										"pos" to targetPrint
									).notification(FAIL, whoInteract).display()
							}
							action.isRightClick -> {
								denyInteraction()

								if (actualBox?.last != targetLocation) {

									playerMarkerBoxes[player.identityObject] = currentBox.apply {
										last = targetLocation
									}
									lang["component.markingTool.action.set"].replaceVariables(
										"n" to 2,
										"pos" to targetPrint
									).notification(APPLIED, whoInteract).display()
									lang["component.markingTool.action.view.distance.other"].replaceVariables(
										"distance" to targetLocation.distance(currentBox.first).shorter
									).message(whoInteract).display()
								} else
									lang["component.markingTool.action.duplicate"].replaceVariables(
										"pos" to targetPrint
									).notification(FAIL, whoInteract).display()
							}
						}
					} else {

						if (playerMarkerBoxes[player.identityObject] != null) {
							lang["component.markingTool.action.view.detail"].replaceVariables(
								"1" to positionData(currentBox.first),
								"2" to positionData(currentBox.last),
							).notification(INFO, whoInteract).display()
							lang["component.markingTool.action.view.distance.both"].replaceVariables(
								"distance" to currentBox.first.distance(currentBox.last).shorter
							).message(whoInteract).display()

						} else
							lang["component.markingTool.action.view.notSet"]
								.notification(FAIL, whoInteract).display()
					}

				} else
					lang["component.markingTool.action.wrongLook"]
						.notification(FAIL, whoInteract).display()

			}
		}

		override val execution: InterchangeAccess.() -> InterchangeResult = {

			(executor as Player).inventory.addItem(markingTool.produce())

			lang["component.markingTool.interchange.success"]
				.notification(APPLIED, executor).display()

			SUCCESS
		}

		val Item.isMarkingTool: Boolean
			get() = identity == "jet:marking_tool"

	}

}