package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.entities.Message

abstract class TextCommand<Parameters>(
    private val names: ArrayList<String>,
    private val parameters: LinkedHashMap<String, Parameter>
) {
    private val subcommandRegistry = arrayListOf<TextCommand<*>>()

    open fun getNames(): ArrayList<String> {
        return names
    }

    open fun getSubcommandRegistry(): ArrayList<TextCommand<*>> {
        return subcommandRegistry
    }

    open fun getParameters(): LinkedHashMap<String, Parameter> {
        return parameters
    }

    abstract fun check(context: CommandContext<Parameters>): Boolean

    abstract fun parameterFactory(parameters: HashMap<String, Any>): Parameters

    abstract fun onExecuted(context: CommandContext<Parameters>)

    fun execute(
        rawParameterValues: HashMap<String, Any>,
        message: Message
    ): Boolean {
        val parameters = parameterFactory(rawParameterValues)
        val context = commandContextFactory(message, parameters)

        val isPermitted = check(context)

        if (!isPermitted)
            return false

        onExecuted(context)
        return true
    }
}