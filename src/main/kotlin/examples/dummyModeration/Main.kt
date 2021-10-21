package examples.dummyModeration

import commands.text.CommandsEventsHandler
import commands.text.Registry
import events.classes.registerGenericEvents
import examples.dummyModeration.modules.basic.BasicModule
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

    builtJDA.awaitReady()

    registerGenericEvents(
        builtJDA,
        CommandsEventsHandler(commandsRegistry)
    )

    BasicModule(commandsRegistry)
}
