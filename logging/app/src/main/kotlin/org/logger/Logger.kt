enum class LogLevel {
    TRACE, INFO, WARN, ERROR
}

class Logger {
    companion object {
        var level = LogLevel.INFO
        fun setLevel(newLevel: LogLevel) {
        level = newLevel
        }
    }
    fun log(msgLevel: LogLevel, message: String) {
        if (msgLevel.ordinal >= level.ordinal) {
            // TODO: Filter messages by level.
            // TODO: Format and print the message.
        }
    }
}