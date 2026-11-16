package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message

class Memory {
    private val messages: MutableList<Message> = mutableListOf()
    
    fun add(message: Message) {
        messages.add(message)
    }
    
    fun getMessages(): List<Message> = messages.toList()
}