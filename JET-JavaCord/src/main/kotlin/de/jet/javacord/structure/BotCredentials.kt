package de.jet.javacord.structure

/**
 * The credentials of the discord bot, currently only the token is required.
 * @param botToken The token of the bot, which is required to connect to the discord api.
 * @author Fruxz
 * @since 1.0
 */
data class BotCredentials(
	var botToken: String? = null,
)