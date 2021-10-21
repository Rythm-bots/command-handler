package fm.rythm.commandhandler.examples.dummyModeration

import fm.rythm.commandhandler.textcommands.CommandsEventsHandler
import fm.rythm.commandhandler.textcommands.Registry
import fm.rythm.commandhandler.classes.registerGenericEvents
import fm.rythm.commandhandler.examples.dummyModeration.modules.basic.BasicModule
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
