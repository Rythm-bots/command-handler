package classes

class InvalidParametersException : Exception("Missing or invalid parameters.")

class CheckFailedException : Exception("You do not have permissions to execute this command.")

class CommandTriggerConflictException(trigger: String)
    : Exception("Trigger $trigger has a conflict.")