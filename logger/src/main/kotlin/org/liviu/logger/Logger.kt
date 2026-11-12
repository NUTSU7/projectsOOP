package org.liviu.logger

import kotlin.random.Random
import kotlin.random.nextInt
import org.liviu.sink.*
import org.liviu.formatter.Formatter


enum class LogLevel {
    TRACE, INFO, WARN, ERROR
}



class Logger(private val name: String, private val formatter: Formatter, private val sink: Sink) {

    constructor(name: String): this(name, Formatter(), ConsoleSink()) {
    }
        
    companion object {
        private var level = LogLevel.INFO
        
        public fun setLevel(newLevel: LogLevel) {
            level = newLevel
        }
    }
    

    public fun trace(message: String, vararg args: Any) = log(LogLevel.TRACE, message, args)
    public fun info(message: String, vararg args: Any) = log(LogLevel.INFO, message, args)
    public fun warn(message: String, vararg args: Any) = log(LogLevel.WARN, message, args)
    public fun error(message: String, vararg args: Any) = log(LogLevel.ERROR, message, args)

    private fun log(msgLevel: LogLevel, message: String, args: Array<out Any>) {
        if (msgLevel.ordinal < level.ordinal) return
        
        sink.write(formatter.formatMessage(name, msgLevel, message, args))
    }
    
}