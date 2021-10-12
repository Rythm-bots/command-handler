package commands

enum class TextCommandParameterType(val pattern: String) {
    STRING(""".+"""),
    INT("""\d+"""),
    USER("""(?:<@!?)?(\d+)>?"""),
    MEMBER("""(?:<@!?)?(\d+)>?""")
}

data class TextCommandParameter(
    val type: TextCommandParameterType,
    val allowMultiple: Boolean = false
)