@file:Suppress("LeakingThis")

package de.jet.minecraft.structure.app

import com.destroystokyo.paper.utils.PaperPluginLogger
import de.jet.library.extension.catchException
import de.jet.library.extension.collection.mutableReplaceWith
import de.jet.library.extension.jetTry
import de.jet.library.tool.smart.Identifiable
import de.jet.library.tool.smart.Identity
import de.jet.minecraft.app.JetCache
import de.jet.minecraft.app.JetData
import de.jet.minecraft.extension.mainLog
import de.jet.minecraft.extension.tasky.task
import de.jet.minecraft.extension.tasky.wait
import de.jet.minecraft.runtime.app.LanguageSpeaker
import de.jet.minecraft.runtime.app.RunStatus
import de.jet.minecraft.runtime.app.RunStatus.*
import de.jet.minecraft.runtime.exception.IllegalActionException
import de.jet.minecraft.structure.app.event.EventListener
import de.jet.minecraft.structure.app.interchange.IssuedInterchange
import de.jet.minecraft.structure.command.Interchange
import de.jet.minecraft.structure.component.Component
import de.jet.minecraft.structure.service.Service
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.configuration.serialization.ConfigurationSerializable
import org.bukkit.configuration.serialization.ConfigurationSerialization
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.InputStreamReader
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

/**
 * # `App (abstract)`
 * ## Info
 * This class structures the main class of every app; It defines
 * variables, management-containers, language and the functions
 * of the whole app.
 *
 * ## Use
 * You should use this Class as the base-plate of your app/plugin
 * instead of the [JavaPlugin], because this class extends out of
 * the [JavaPlugin] class. This class is way better, because it has
 * more extending functionality over the classic [JavaPlugin]!
 *
 * ## Base
 * The [App]-Class is abstract, so you can use it for your own class,
 * to be the base of your own app.
 *
 * This class uses these base-plates:
 * - [JavaPlugin]: Every app is a whole real Minecraft-Server-Plugin,
 * but it is heavely extended. But you can touch the original Bukkit/
 * Paper API, if you want to do so.
 *
 * - [Identifiable]<[App]>: Every app is identifiable thru is unique
 * custom set [appIdentity] property. ([appIdentity] = [identity])
 *
 * @author Fruxz (@TheFruxz)
 * @since 1.0-BETA-2 (preview)
 * @see de.jet.minecraft.app.JetApp
 * @constructor abstract
 */
abstract class App : JavaPlugin(), Identifiable<App> {

	// parameters

	/**
	 * # `App.companion (abstract-value)`
	 * ## Info
	 * This value defines the Companion-Object of this class, which
	 * holds the instance variable *(lateinit recommended)* and other
	 * app-related special variables & functions.
	 *
	 * ## Use
	 * Use this value/companion to save the app-instance, and other
	 * important things, but not too much, it is only a place for very
	 * important every-time reachable stuff! Use your App-Class(-Type)
	 * as the input for the <T> at [AppCompanion]<*>, so that the
	 * app-instance is from your class, but also use `instance = this`
	 * at the [register]`()` function
	 *
	 * ## Relations
	 * Because the [AppCompanion]<T> class provides the same identity
	 * ([Identifiable]<[App]>) as the app itself, you can use the
	 * [companion] instead of the app, to check equalities and more!
	 *
	 * @author Fruxz (@TheFruxz)
	 * @since 1.0-BETA-2 (preview)
	 * @see de.jet.minecraft.app.JetApp.Companion
	 * @sample de.jet.minecraft.app.JetApp.companion
	 * @constructor abstract
	 */
	abstract val companion: AppCompanion<*>

	/**
	 * The unique application identity
	 */
	abstract val appIdentity: String

	/**
	 * The displayName
	 */
	abstract val appLabel: String

	/**
	 * The cache of the application
	 */
	abstract val appCache: AppCache

	override val identity: String
		get() = appIdentity

	// api

	/**
	 * Add Interchange
	 */
	fun add(interchange: Interchange) {
		val failFreeLabel = interchange::class.simpleName

		mainLog(Level.INFO, "Starting register of interchange '$failFreeLabel'!")

		fun failed() {
			val label = interchange.label
			val aliases = try { interchange.aliases } catch (e: Exception) { emptySet() }
			val command = getCommand(interchange.label)

			mainLog(Level.WARNING, "FAILED! try to register fail-interchange '$label' instead...")

			if (command != null) {
				val replace = IssuedInterchange(this, label, aliases)

				command.setExecutor(replace)
				command.tabCompleter = replace.completionEngine
				command.usage = replace.completionDisplay

			} else
				mainLog(Level.WARNING, "FAILED! failed to register fail-interchange for '$label'")

		}

		if (isEnabled) {

			try {
				val label = interchange.label
				val aliases = interchange.aliases
				val command = getCommand(interchange.label)

				if (command != null) {

					command.setExecutor(interchange)
					command.tabCompleter = interchange.completionEngine
					command.usage = interchange.completionDisplay
					command.aliases.mutableReplaceWith(aliases)

					JetCache.registeredInterchanges.add(interchange)

					mainLog(Level.INFO, "Register of interchange '$label' succeed!")

				} else
					throw IllegalArgumentException("Cannot find interchange (command) with name '$label' in plugin.yml!")

			} catch (exception: Exception) {
				catchException(exception)
				failed()
			}

		} else
			mainLog(Level.WARNING, "skipped registering '$failFreeLabel' interchange, app disabled!")

		// TODO: 11.07.2021 Add Interchanges
	}

	fun add(eventListener: EventListener) {
		if (isEnabled) {

			try {

				pluginManager.registerEvents(eventListener as Listener, this)
				mainLog(Level.INFO, "registered '${eventListener.listenerIdentity}' listener!")

			} catch (e: Exception) {

				mainLog(Level.WARNING, "Error during adding handler")
				catchException(e)

			}

		} else
			mainLog(Level.WARNING, "skipped registering '${eventListener.listenerIdentity}' listener, app disabled!")
	}

	fun register(service: Service) {
		if (JetCache.registeredServices.none { it.identity == service.identity }) {
			jetTry {
				JetCache.registeredServices.add(service)
				mainLog(Level.INFO, "Register of service '${service.identity}' succeed!")
			}
		} else
			throw IllegalStateException("The service '${service.identity}' is already registered!")
	}

	fun unregister(service: Service) {
		if (JetCache.registeredServices.any { it.identity == service.identity }) {
			jetTry {
				stop(service)
				JetCache.registeredServices.remove(service)
				mainLog(Level.INFO, "Unregister of service '${service.identity}' succeed!")
			}
		} else
			throw IllegalStateException("The service '${service.identity}' is not registered!")
	}

	fun reset(service: Service) {
		if (JetCache.registeredServices.any { it.identity == service.identity }) {
			jetTry {
				service.controller?.attempt = 0
				mainLog(Level.INFO, "Reset of service '${service.identity}' succeed!")
			}
		} else
			throw IllegalStateException("The service '${service.identity}' is not registered!")
	}

	fun start(service: Service) {
		if (JetCache.registeredServices.any { it.identity == service.identity }) {
			if (!service.isRunning) {
				jetTry {
					task(
						service.temporalAdvice,
						process = service.process,
						vendor = this,
						onStart = service.onStart,
						onStop = service.onStop,
						onCrash = service.onCrash,
						serviceVendor = service.identityObject
					)
					mainLog(Level.INFO, "Starting of service '${service.identity}' succeed!")
				}
			} else
				throw IllegalStateException("The service '${service.identity}' is already running!")
		} else
			throw IllegalStateException("The service '${service.identity}' is not registered!")
	}

	fun stop(service: Service) {
		if (service.isRunning) {
			jetTry {
				service.shutdown()
				mainLog(Level.INFO, "Stopping of service '${service.identity}' succeed!")
			}
		} else
			throw IllegalStateException("The service '${service.identity}' is not running!")
	}

	fun restart(service: Service) {
		mainLog(Level.INFO, "--- --- --- --- --- --- --- --- --- --- --- ---")
		mainLog(Level.INFO, "Attempting restart of service '${service.identity}'...")
		try {
			stop(service)
		} catch (exception: IllegalStateException) {
			mainLog(Level.WARNING, "skipped stop of service '${service.identity}', was already offline!")
		}
		mainLog(Level.INFO, "Waiting one second, let the service stop...")
		wait(20L*1) {
			start(service)
		}
		mainLog(Level.INFO, "Restart of service '${service.identity}' succeed!")
		mainLog(Level.INFO, "--- --- --- --- --- --- --- --- --- --- --- ---")
	}

	fun remove(eventListener: EventListener) {
		if (isEnabled) {

			try {

				HandlerList.unregisterAll(eventListener)
				mainLog(Level.INFO, "unregistered '${eventListener.listenerIdentity}' listener!")

			} catch (e: Exception) {

				mainLog(Level.WARNING, "Error during removing handler")
				catchException(e)

			}

		} else
			mainLog(Level.WARNING, "skipped unregistering '${eventListener.listenerIdentity}' listener, app disabled!")
	}

	fun add(component: Component) {
		jetTry {

			if (JetCache.registeredComponents.any { it.identity == component.identity })
				throw IllegalStateException("Component '${component.identity}' (${component::class.simpleName}) cannot be saved, because the component id '${component.identity}' is already in use!")

			component.firstContactHandshake()

			JetCache.registeredComponents.add(component)

			mainLog(Level.INFO, "registered '${component.identity}' component!")

			if (component.isAutoStarting) {

				mainLog(Level.INFO, "### [ AUTO-START ] ### '${component.identity}' is auto-starting ### ")

				start(component.identityObject)

			}

		}
	}

	fun start(componentIdentity: Identity<Component>) = jetTry {
		val component = JetCache.registeredComponents.firstOrNull { it.identityObject == componentIdentity }

		if (component != null) {

			if (!JetCache.runningComponents.contains(componentIdentity)) {

				component.start()

				JetCache.runningComponents.add(componentIdentity)

				mainLog(Level.INFO, "started '${componentIdentity.identity}' component!")

			} else
				throw IllegalStateException("The component '$componentIdentity' is already running!")

		} else
			throw NoSuchElementException("The component '$componentIdentity' is currently not registered! ADD IT!")

	}

	fun stop(componentIdentity: Identity<Component>, unregisterComponent: Boolean = false) = jetTry {
		val component = JetCache.registeredComponents.firstOrNull { it.identityObject == componentIdentity }

		if (component != null) {

			if (component.canBeStopped) {

				if (JetCache.runningComponents.contains(componentIdentity)) {

					component.stop()

					JetCache.runningComponents.remove(componentIdentity)

					if (unregisterComponent)
						unregister(componentIdentity)

					mainLog(Level.INFO, "stopped '${component.identity}' component!")

				} else
					throw IllegalStateException("The component '$componentIdentity' is already not running!")

			} else
				throw IllegalActionException("The component '$componentIdentity' can't be stopped, due to its behavior '${component.behaviour}'!")

		} else
			throw NoSuchElementException("The component '$componentIdentity' is currently not registered! ADD IT!")

	}

	fun unregister(componentIdentity: Identity<Component>) {
		jetTry {
			val component = JetCache.registeredComponents.firstOrNull { it.identityObject == componentIdentity }

			if (component != null) {

				JetCache.registeredComponents.remove(component)

			} else
				throw NoSuchElementException("The component '$componentIdentity' is already not registered!")

		}
	}

	fun register(serializable: Class<out ConfigurationSerializable>) = jetTry {
		ConfigurationSerialization.registerClass(serializable)
		mainLog(Level.INFO, "successfully registered '${serializable.simpleName}' as serializable!")
	}

	fun register(serializable: KClass<out ConfigurationSerializable>) =
		register(serializable.java)

	fun unregister(serializable: Class<out ConfigurationSerializable>) = jetTry {
		ConfigurationSerialization.unregisterClass(serializable)
	}

	fun unregister(serializable: KClass<out ConfigurationSerializable>) =
		unregister(serializable.java)

	// runtime

	/**
	 * The current status of app-runtime
	 */
	var runStatus: RunStatus = OFFLINE
		private set

	var appRegistrationFile = YamlConfiguration()

	val log by lazy { createLog(appIdentity) }

	internal fun getResourceFile(path: String) =
		classLoader.getResourceAsStream(path)?.reader()?.readText()

	val languageSpeaker by lazy { LanguageSpeaker(JetData.systemLanguage.content) }

	private val pluginManager = server.pluginManager

	// override base-mechanics

	abstract fun register()
	abstract fun hello()
	abstract fun bye()

	/**
	 * # **DO NOT OVERRIDE IF YOU DON'T EXPLICITLY WANT TO!**
	 * #### ___Expect heavy code changes during the BETA!___
	 */
	override fun onLoad() {
		jetTry {
			JetCache.registeredApplications.add(this)

			runStatus = PRE_LOAD
			classLoader.getResourceAsStream("plugin.yml")?.let { resource ->
				appRegistrationFile.load(InputStreamReader(resource))
			}
			register()
			runStatus = LOAD

		}
	}

	/**
	 * # **DO NOT OVERRIDE IF YOU DON'T EXPLICITLY WANT TO!**
	 * #### ___Expect heavy code changes during the BETA!___
	 */
	override fun onEnable() {
		jetTry {

			runStatus = PRE_ENABLE
			hello()
			runStatus = ENABLE

		}
	}

	/**
	 * # **DO NOT OVERRIDE IF YOU DON'T EXPLICITLY WANT TO!**
	 * #### ___Expect heavy code changes during the BETA!___
	 */
	override fun onDisable() {
		jetTry {

			runStatus = SHUTDOWN
			bye()
			runStatus = OFFLINE

		}
	}

	companion object {

		fun createLog(app: String, section: String = "main"): Logger =
			(PaperPluginLogger.getLogger("JET/$app // $section") ?: Logger.getLogger("JET/$app // $section"))

	}

}