package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message

class Agent(private val name: String, private val personality: String, private val memory: Memory, private val aiClient: AiClient) {
    fun chat(input: String): String {
        memory.add(Message(role = Role.USER, content = input))
        val aiResponse = aiClient.chat(memory.getMessages(), 
            "Your name is $name and you are a $personality agent."
        )
        memory.add(Message(role = Role.ASSISTANT, content = aiResponse.text))
        return aiResponse.text
    }
}