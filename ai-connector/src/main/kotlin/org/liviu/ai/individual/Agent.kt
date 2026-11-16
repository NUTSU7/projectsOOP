package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient


class Agent(private val name: String, private val personality: String, private val aiClient: AiClient) {
    fun chat(input: String): String {
        val aiResponse = aiClient.chat(listOf(Message(role = Role.USER, content = input)), 
            "Your name is $name and you are a $personality agent.")
            
        return aiResponse.text
    }
}