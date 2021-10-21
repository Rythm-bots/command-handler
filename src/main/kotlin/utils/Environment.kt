package utils

class Environment {
    companion object {
        val DISABLED_EVENTS = (System.getenv("DISABLED_EVENTS") ?: "")
            .replace(" ", "")
            .split(",")
        val ENABLED_MODULES = (System.getenv("ENABLED_MODULES") ?: "")
            .replace(" ", "")
            .split(",")
        val PREFIXES = (System.getenv("PREFIXES") ?: "=")
            .replace(" ", "")
            .split(",")
    }
}