package org.liviu.formatter

import org.liviu.logger.LogLevel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class Formatter(format: String) {
    companion object {
        var format = "[%t] [%l] %n: %m, %r"
        var TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
        private val RANDOM_MESSAGES = mapOf(
            LogLevel.TRACE to listOf(
                "Entering method",
                "Step completed",
                "Checking internal state"
            ),
            LogLevel.INFO to listOf(
                "Operation completed",
                "Everything looks good",
                "Here is your result"
            ),
            LogLevel.WARN to listOf(
                "Something unexpected happened",
                "Continuing anyway",
                "Value looks unusual"
            ),
            LogLevel.ERROR to listOf(
                "Operation failed",
                "Invalid input",
                "Something went wrong"
            )
        )
    }
    
    init {
        Companion.format = format
    }
    
    constructor() : this(Companion.format) {
    }
    
    public fun formatMessage(name: String, logLevel: LogLevel, message: String): String{
        val time = LocalTime.now().format(TIME_FORMAT)
        val randomMessage = RANDOM_MESSAGES.getValue(logLevel).random()
        
        return format.replace("%t", time)
            .replace("%l", logLevel.name)
            .replace("%n", name)
            .replace("%m", message)
            .replace("%r", randomMessage)
    }
}