package de.jet.paper.app.component.point

import de.jet.paper.extension.system
import de.jet.paper.structure.component.Component.RunType.AUTOSTART_MUTABLE
import de.jet.paper.structure.component.SmartComponent

internal class PointComponent : SmartComponent(system, AUTOSTART_MUTABLE) {

	override val thisIdentity = "World-Points"

	override fun component() {

		interchange(PointInterchange())

	}
}