package fm.rythm.commandhandler.textcommands

enum class ParameterType(val regex: String, val humanReadable: String) {
    TEXT("""(.+)""", "Text"),
    INT("""(\d+)""", "32-bit number"),
    LONG("""(\d+)""", "64-bit number"),
    USER("""(?:<@!?)?(\d+)>?""", "User Mention")
}