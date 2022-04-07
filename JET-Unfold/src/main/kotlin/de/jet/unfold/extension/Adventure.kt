package de.jet.unfold.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer

/**
 * This value represents the [LegacyComponentSerializer] instance, which
 * is used to convert between strings/objects and [Component]s.
 * @see ComponentLike.asString
 * @see String.asComponent
 * @see String.asStyledComponent
 * @author Fruxz
 * @since 1.0
 */
val adventureSerializer = LegacyComponentSerializer
	.builder().extractUrls().hexColors().build()

/**
 * This value represents the [MiniMessage] instance, which
 * is used to convert between strings/objects and [Component]s.
 * This is especially adding the [String]-features like `<rainbow>`!
 * @see ComponentLike.asStyledString
 * @see String.asStyledComponent
 * @author Fruxz
 * @since 1.0
 */
val miniMessageSerializer = MiniMessage.miniMessage()

/**
 * This computational value converts this [ComponentLike]
 * into a [String] by using the [LegacyComponentSerializer],
 * provided by the [adventureSerializer] value.
 * @see adventureSerializer
 * @author Fruxz
 * @since 1.0
 */
val ComponentLike.asString: String
	get() = adventureSerializer.serialize(asComponent())

/**
 * This computational value converts this [String] into a [TextComponent]
 * by using the [LegacyComponentSerializer], provided by the
 * [adventureSerializer] value.
 * @see adventureSerializer
 * @author Fruxz
 * @since 1.0
 */
val String.asComponent: TextComponent
	get() = adventureSerializer.deserializeOr(this, Component.text("FAILED", NamedTextColor.RED))!!

/**
 * This computational value converts this [ComponentLike]
 * into a [String] by using the [MiniMessage], provided by the
 * [miniMessageSerializer] value.
 * This is especially adding the [String]-features like `<rainbow>`!
 * @see miniMessageSerializer
 * @author Fruxz
 * @since 1.0
 */
val ComponentLike.asStyledString: String
	get() = miniMessageSerializer.serialize(asComponent())

/**
 * This computational value converts this [String]into a [TextComponent]
 * by using the [MiniMessage], provided by the
 * [miniMessageSerializer] value.
 * This is especially adding the [String]-features like `<rainbow>`!
 * @see miniMessageSerializer
 * @author Fruxz
 * @since 1.0
 */
val String.asStyledComponent: TextComponent
	get() = Component.text().append(miniMessageSerializer.deserializeOr(this, Component.text("FAILED", NamedTextColor.RED))!!).build()