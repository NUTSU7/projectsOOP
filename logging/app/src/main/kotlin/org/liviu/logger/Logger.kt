package org.liviu.logger

import kotlin.random.Random
import kotlin.random.nextInt


enum class LogLevel {
    TRACE, INFO, WARN, ERROR
}

class Logger(private val name: String) {
    companion object {
        var level = LogLevel.INFO
    }
    
    fun setLevel(newLevel: LogLevel) {
        level = newLevel
    }
    
    public fun trace(message: String) = log(LogLevel.TRACE, message)
    public fun info(message: String) = log(LogLevel.INFO, message)
    public fun warn(message: String) = log(LogLevel.WARN, message)
    public fun error(message: String) = log(LogLevel.ERROR, message)
    
    private fun log(msgLevel: LogLevel, message: String){
        if (msgLevel.ordinal < level.ordinal) return
        
        //println(formatMessage(format, msgLevel, message))
    }
    
}