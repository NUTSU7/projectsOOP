package org.liviu.sink

class ConsoleSink : Sink {
    override fun write(message: String) {
        println(message)
    }
}