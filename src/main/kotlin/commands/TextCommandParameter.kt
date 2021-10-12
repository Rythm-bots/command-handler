package commands

enum class TextCommandParameterType(val pattern: String, val humanReadable: String) {
    STRING("""(.+)""", "Text"),
    INT("""(\d+)""", "Number"),
    USER("""(?:<@!?)?(\d+)>?""", "User Mention")
}

data class TextCommandParameter(
    val type: TextCommandParameterType,
    val description: String? = null,
    val allowMultiple: Boolean = false,
    val amountLimit: Int = Int.MAX_VALUE
)