import classes.CheckFailedException
import classes.InvalidParametersException
import commands.CommandsEventsHandler
import commands.TextCommand
import commands.TextCommandRegistry
import events.genericHandlers.ReadyEventHandler
import events.classes.Registration
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.MessageBuilder
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag
import utils.Environment
import utils.sendSafe

fun configureMemoryUsage(jda: JDABuilder) {
    jda.disableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY)
    jda.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
}

fun errorHandler(error: Exception, message: Message, command: TextCommand<*>): Boolean {
    val channel = message.channel

    if (error !is CheckFailedException && error !is InvalidParametersException)
    {
        channel.sendSafe("An error occurred whilst executing your command.").queue()
        return true
    }

    if (error is InvalidParametersException)
    {
        val messageBuilder = MessageBuilder()
            .setContent(error.message!!)
            .setEmbed(command.generateEmbed(0xe6435e).build())

        channel.sendSafe(messageBuilder).queue()
        return true;
    }

    channel.sendSafe(error.message!!).queue()
    return true
}

fun main() {
    val jdaBuilder = JDABuilder.createDefault(Environment.TOKEN)

    // Configure intents and caching
    configureMemoryUsage(jdaBuilder)

    val builtJDA = jdaBuilder.build()

    val commandsRegistry = TextCommandRegistry(::errorHandler)

    // No need to worry about disabling or enabling handlers here,
    // handlers are by design aware of whether they're enabled or not.
    Registration.registerGenericEvents(
        builtJDA,
        ReadyEventHandler(commandsRegistry),
        CommandsEventsHandler(commandsRegistry)
    )


    builtJDA.awaitReady()
}