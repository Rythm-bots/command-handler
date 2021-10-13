import commands.CommandsEventsHandler
import commands.TextCommandRegistry
import events.classes.Registration
import events.genericHandlers.ReadyEventHandler
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import utils.Environment

fun configureMemoryUsage(jda: JDABuilder) {
    jda.disableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY)
    jda.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
}

fun main() {
    val jdaBuilder = JDABuilder.createDefault(Environment.TOKEN)

    // Configure intents and caching
    configureMemoryUsage(jdaBuilder)

    val builtJDA = jdaBuilder.build()

    val commandsRegistry = TextCommandRegistry()

    // No need to worry about disabling or enabling handlers here,
    // handlers are by design aware of whether they're enabled or not.
    Registration.registerGenericEvents(
        builtJDA,
        ReadyEventHandler(commandsRegistry),
        CommandsEventsHandler(commandsRegistry)
    )


    builtJDA.awaitReady()
}