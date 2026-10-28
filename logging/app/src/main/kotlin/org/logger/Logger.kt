package org.logger
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random
import kotlin.random.nextInt


enum class LogLevel {
    TRACE, INFO, WARN, ERROR
}

class Logger(private val name: String) {
    companion object {
        var level = LogLevel.INFO
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
    
    fun setLevel(newLevel: LogLevel) {
        level = newLevel
    }
        
    fun setFormat(newFormat: String) {
        format = newFormat
    }
    
    public fun trace(message: String) = log(LogLevel.TRACE, message)
    public fun info(message: String) = log(LogLevel.INFO, message)
    public fun warn(message: String) = log(LogLevel.WARN, message)
    public fun error(message: String) = log(LogLevel.ERROR, message)
    
    private fun log(msgLevel: LogLevel, message: String){
        if (msgLevel.ordinal < level.ordinal) return
        
        println(formatMessage(format, msgLevel, message))
    }
    
    private fun formatMessage(format: String, logLevel: LogLevel, message: String): String{
        val time = LocalTime.now().format(TIME_FORMAT)
        val randomMessage = RANDOM_MESSAGES.getValue(logLevel).random()
        
        return format.replace("%t", time)
            .replace("%l", logLevel.name)
            .replace("%n", name)
            .replace("%m", message)
            .replace("%r", randomMessage)
    }
}