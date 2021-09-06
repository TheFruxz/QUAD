package de.jet.minecraft.extension

import de.jet.library.tool.smart.Identifiable
import de.jet.library.tool.smart.Identity
import de.jet.minecraft.JET
import de.jet.minecraft.app.JetApp
import de.jet.minecraft.app.JetCache
import de.jet.minecraft.runtime.app.LanguageSpeaker
import de.jet.minecraft.structure.app.App
import java.util.logging.Level

fun <T : Any?> T.debugLog(message: String) = this.also {
	if (JetApp.debugMode) {
		mainLog(Level.WARNING, "DEBUG /!/ $message")
	}
}

fun debugLog(message: String) = "".debugLog(message)

internal fun mainLog(level: Level = Level.INFO, message: String) = JetApp.instance.log.log(level, message)

internal val lang: LanguageSpeaker
	get() = JET.languageSpeaker

internal fun lang(id: String, smartColor: Boolean = true) = lang.message(id, smartColor)

internal val system: JetApp
	get() = JET.appInstance

@Throws(NoSuchElementException::class)
fun app(id: String) = JetCache.registeredApplications.first { it.appIdentity == id }

@Throws(NoSuchElementException::class)
fun app(vendor: Identifiable<App>) = JetCache.registeredApplications.first { it.appIdentity == vendor.identity }

@Throws(NoSuchElementException::class)
fun app(vendorIdentity: Identity<App>) = JetCache.registeredApplications.first { it.appIdentity == vendorIdentity.identity }

@Throws(NoSuchElementException::class)
fun Identifiable<App>.getApp() = app(identity)