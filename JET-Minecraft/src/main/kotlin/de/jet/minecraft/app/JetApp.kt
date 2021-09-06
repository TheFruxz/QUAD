package de.jet.minecraft.app

import de.jet.library.extension.forceCast
import de.jet.minecraft.app.component.chat.JetChatComponent
import de.jet.minecraft.app.component.events.JetEventsComponent
import de.jet.minecraft.app.component.item.JetActionComponent
import de.jet.minecraft.app.component.system.JetKeeperComponent
import de.jet.minecraft.app.component.world.JetBuildModeComponent
import de.jet.minecraft.app.interchange.ComponentInterchange
import de.jet.minecraft.app.interchange.JETInterchange
import de.jet.minecraft.app.interchange.SandboxInterchange
import de.jet.minecraft.app.interchange.ServiceInterchange
import de.jet.minecraft.app.interchange.player.BuildModeInterchange
import de.jet.minecraft.extension.debugLog
import de.jet.minecraft.extension.mainLog
import de.jet.minecraft.extension.obj.buildSandBox
import de.jet.minecraft.structure.app.App
import de.jet.minecraft.structure.app.AppCompanion
import de.jet.minecraft.tool.data.Preference
import de.jet.minecraft.tool.display.world.SimpleLocation
import org.bukkit.configuration.serialization.ConfigurationSerialization
import java.util.logging.Level

class JetApp : App() {

	override val companion = Companion

	override val appIdentity = "JET"
	override val appLabel = "JET"
	override val appCache = JetCache

	override fun register() {
		instance = this

		ConfigurationSerialization.registerClass(SimpleLocation::class.java)

		debugMode = JetData.debugMode.content

		debugLog("DebugMode preference loaded & set from file!")

	}

	override fun hello() {

		JetCache.tmp_initSetupPreferences.forEach {
			fun <T : Any> proceed(default: T) {
				val preference = it.forceCast<Preference<T>>()
				preference.content = default
				mainLog(Level.INFO, "Init-Setup '${preference.identity}' with '$default'(${default::class.simpleName})")
			}
			proceed(it.default)
		}

		JetCache.tmp_initSetupPreferences.clear()

		languageSpeaker.let { languageSpeaker ->
			mainLog(Level.INFO, "Speaking langauge: ${languageSpeaker.baseLang}")
			with(languageSpeaker.languageContainer) {
				"""
					Display-Language detected:
					ID: ${this.languageId};
					JET: ${this.jetVersion};
					Version: ${this.languageVersion};
					Vendor: ${this.languageVendor};
					Website: ${this.languageVendorWebsite};
					Test: ${languageSpeaker.message("system.hello")};
				""".trimIndent().lines().forEach {
					mainLog(Level.INFO, it)
				}
			}
		}

		add(JetEventsComponent())
		add(JetChatComponent())
		add(JetActionComponent())
		add(JetKeeperComponent())
		add(JetBuildModeComponent())

		add(JETInterchange())
		add(ComponentInterchange())
		add(ServiceInterchange())
		add(SandboxInterchange())
		add(BuildModeInterchange())

		buildSandBox(this, "worky") {
			executor.sendMessage("worky is receiving: '${parameters.joinToString(" ")}'! NICE!")
		}

	}

	override fun bye() {

		JetCache.registeredComponents.forEach {
			it.stop()
		}

	}

	companion object : AppCompanion<JetApp> {
		override lateinit var instance: JetApp
		var debugMode: Boolean = false
	}

}