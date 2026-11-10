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
    
    public fun formatMessage(name: String, logLevel: LogLevel, message: String, args: Array<out Any>): String{
        val time = LocalTime.now().format(TIME_FORMAT)
        val randomMessage = RANDOM_MESSAGES.getValue(logLevel).random()
        
        val msg = if (args != null) fillArgs(message, args) else message
        
        return format.replace("%t", time)
            .replace("%l", logLevel.name)
            .replace("%n", name)
            .replace("%m", msg)
            .replace("%r", randomMessage)
    }

    private fun fillArgs(message: String, args: Array<out Any>): String {
        val sb = StringBuilder()
        var next = 0
        var i = 0
        while (i < message.length) {
            val c = message[i]
            when {
                c == '%' && i + 1 < message.length && message[i + 1] == '%' -> {
                    sb.append('%'); i += 2; continue
                }

                c == '%' && next < args.size -> sb.append(args[next++])
                else -> sb.append(c)
            }
            i++
        }
        while (next < args.size) sb.append(' ').append(args[next++])   // leftover args
        return sb.toString()
}
}