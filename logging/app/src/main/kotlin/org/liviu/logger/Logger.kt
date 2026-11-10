package org.liviu.logger

import kotlin.random.Random
import kotlin.random.nextInt
import org.liviu.consolesink.ConsoleSink
import org.liviu.formatter.Formatter


enum class LogLevel {
    TRACE, INFO, WARN, ERROR
}



class Logger(private val name: String, private val formatter: Formatter) {

    constructor(name: String): this(name, Formatter(Formatter.format)) {
    }
        
    companion object {
        private var level = LogLevel.INFO
        
        fun setLevel(newLevel: LogLevel) {
            level = newLevel
        }
    }
    

    public fun trace(message: String) = log(LogLevel.TRACE, message)
    public fun info(message: String) = log(LogLevel.INFO, message)
    public fun warn(message: String) = log(LogLevel.WARN, message)
    public fun error(message: String) = log(LogLevel.ERROR, message)
    
    private fun log(msgLevel: LogLevel, message: String){
        if (msgLevel.ordinal < level.ordinal) return
        
        ConsoleSink.write(formatter.formatMessage(name, msgLevel, message))
    }
    
}