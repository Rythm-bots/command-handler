package examples.basic

import commands.text.CommandsEventsHandler
import commands.text.Registry
import events.classes.registerGenericEvents
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

fun configureMemoryUsage(jda: JDABuilder) {
    jda.disableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY)
    jda.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
}

fun main() {
    val token: String = System.getenv("TOKEN")
    val jdaBuilder = JDABuilder.createDefault(token)

    // Configure intents and caching
    configureMemoryUsage(jdaBuilder)

    val builtJDA = jdaBuilder.build()

    val commandsRegistry = Registry()

    // No need to worry about disabling or enabling handlers here,
    // handlers are by design aware of whether they're enabled or not.
    registerGenericEvents(
        builtJDA,
        ReadyEventHandler(commandsRegistry),
        CommandsEventsHandler(commandsRegistry)
    )

    builtJDA.awaitReady()
}