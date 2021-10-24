package fm.rythm.commandhandler.textcommands

import fm.rythm.commandhandler.classes.CommandTriggerConflictException

open class Registry {
    val commands = arrayListOf<TextCommand<*>>()

    open fun getCommandsList(): ArrayList<TextCommand<*>> {
        return commands
    }

    /**
     * Find command by specified trigger.
     *
     * @return The text command if one exists by the specified trigger, otherwise null.
     */
    fun findByTrigger(trigger: String): TextCommand<*>? {
        return getCommandsList().find { command ->
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
        getCommandsList().addAll(textCommands)
    }
}