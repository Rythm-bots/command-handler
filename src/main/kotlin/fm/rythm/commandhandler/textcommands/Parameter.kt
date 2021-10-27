package fm.rythm.commandhandler.textcommands

data class Parameter(
    val type: ParameterType,
    val helpDescription: String = "N/A",
    val allowMultiple: Boolean = false,
)