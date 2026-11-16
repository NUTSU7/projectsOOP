package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message

class Memory(private val capacity: Int) {
    companion object {
        fun tier1() = Memory(4) 
        fun tier2() = Memory(6) 
        fun tier3() = Memory(8)    
    }
    
    private val messages = ArrayDeque<Message>()
    
    fun add(message: Message) {
        if(message.content.isEmpty()){
            throw IllegalArgumentException("Message cannot be null")
            return
        }
        
        messages.addLast(message)
        
        if (messages.size > capacity) {
            messages.removeFirst()
        }
    }
    
    fun getMessages(): List<Message> = messages.toList()
}