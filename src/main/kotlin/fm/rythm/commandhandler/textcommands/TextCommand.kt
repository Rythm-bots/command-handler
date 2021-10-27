package fm.rythm.commandhandler.textcommands

import net.dv8tion.jda.api.entities.Message

abstract class TextCommand<Parameters>(
    private val names: ArrayList<String>,
    private val parameters: LinkedHashMap<String, Parameter>
) {
    private val registry = arrayListOf<TextCommand<*>>()

    open fun getNames(): ArrayList<String> {
        return names
    }

    open fun getRegistry(): ArrayList<TextCommand<*>> {
        return registry
    }

    open fun getParameters(): LinkedHashMap<String, Parameter> {
        return parameters
    }

    abstract fun parameterFactory(parameters: HashMap<String, Any>): Parameters

    abstract fun onExecuted(parameters: CommandContext<Parameters>)

    fun execute(
        rawParameterValues: HashMap<String, Any>,
        message: Message
    ) {
        val parameters = parameterFactory(rawParameterValues)
        val context = commandContextFactory(message, parameters)

        onExecuted(context)
    }
}