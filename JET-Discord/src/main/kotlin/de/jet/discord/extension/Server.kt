package de.jet.discord.extension

import org.javacord.api.DiscordApi

/**
 * Get the server with the given [id] or null if the bot is
 * not part of that server (or the server-id doesn't exist).
 * @param id the id of the searched server
 * @return the server with the given [id] or null if the bot is not part of that server
 * @author Fruxz
 * @since 1.0
 */
fun DiscordApi.server(id: Long) = try {
    getServerById(id).get()
} catch (e: NoSuchElementException) {
    null
}

/**
 * Get the first server with the given [name].
 * @param name the name of the searched server
 * @return the first server wich appeared in the search query with the given [name]
 * @author Fruxz
 * @since 1.0
 */
fun DiscordApi.server(name: String) = getServersByName(name).firstOrNull()

/**
 * Get every server with the given [name] as a list.
 * @param name the name of the searched server
 * @return a list of servers with the given [name]
 * @author Fruxz
 * @since 1.0
 */
fun DiscordApi.servers(name: String) = getServersByName(name).toList()

/**
 * Get every server where the discord bot has access to.
 * @return List of all servers.
 * @author Fruxz
 * @since 1.0
 */
fun DiscordApi.servers() = servers.toList()