package de.jet.paper.tool.data

import de.jet.jvm.extension.container.flipped
import de.jet.jvm.extension.container.replace
import de.jet.jvm.extension.container.toArrayList
import de.jet.jvm.extension.data.fromJson
import de.jet.jvm.extension.data.toJson
import de.jet.paper.runtime.app.LanguageSpeaker
import de.jet.paper.tool.display.item.Item
import de.jet.paper.tool.display.world.SimpleLocation
import org.bukkit.Location

data class DataTransformer<SHELL: Any, CORE: Any>(
	val toCore: SHELL.() -> CORE,
	val toShell: CORE.() -> SHELL,
) {

	companion object {

		@JvmStatic
		fun <BOTH : Any> empty() =
			DataTransformer<BOTH, BOTH>({ this }, { this })

		// JSON
		@JvmStatic
		inline fun <reified T : Any> json() =
			DataTransformer<T, String>(
				{ this.toJson() },
				{ this.fromJson() },
			)

		@JvmStatic
		fun jsonItem() =
			DataTransformer<Item, String>({ produceJson() }, { Item.produceByJson(this)!! })

		// collections

		@JvmStatic
		inline fun <reified SET> setCollection() =
			DataTransformer<Set<SET>, ArrayList<SET>>(
				{ toArrayList() },
				{ toSet() },
			)

		// colors

		@JvmStatic
		fun simpleColorCode() =
			DataTransformer<String, String>({ replace(LanguageSpeaker.smartColorReplace.flipped()) }, { replace(LanguageSpeaker.smartColorReplace) })

		// simple location

		@JvmStatic
		fun simpleLocationBukkit() =
			DataTransformer<Location, SimpleLocation>({ SimpleLocation.ofBukkit(this) }, { bukkit })

		@JvmStatic
		fun simpleLocationListBukkit() =
			DataTransformer<List<Location>, List<SimpleLocation>>({ map { SimpleLocation.ofBukkit(it) }}, { map { it.bukkit } })

		@JvmStatic
		fun simpleLocationArrayBukkit() =
			DataTransformer<Array<Location>, Array<SimpleLocation>>({ map { SimpleLocation.ofBukkit(it) }.toTypedArray()}, { map { it.bukkit }.toTypedArray() })

	}

}
