package commands.text

import classes.CommandTriggerConflictException

open class Registry {
    protected val commands = arrayListOf<TextCommand<*>>()

    /**
     * Find command by specified trigger.
     *
     * @return The text command if one exists by the specified trigger, otherwise null.
     */
    fun findByTrigger(trigger: String): TextCommand<*>? {
        return commands.find { command ->
            return@find command.triggers.contains(trigger)
        }
    }

    /**
     * Register specified commands.
     *
     * @throws CommandTriggerConflictException
     *         if one of the registered commands' triggers conflicts with already registered ones.
     */
    fun register(vararg textCommands: TextCommand<*>) {
        // Look for conflicting triggers before registering the command,
        // throw CommandTriggerConflictException if one exists.
        textCommands.forEach { command ->
            val triggers = command.triggers
            val conflictingTrigger = triggers.find {
                    trigger -> findByTrigger(trigger) != null
            } ?: return@forEach

            throw CommandTriggerConflictException(conflictingTrigger)
        }

        // Register commands all specified commands.
        commands.addAll(textCommands)
    }
}