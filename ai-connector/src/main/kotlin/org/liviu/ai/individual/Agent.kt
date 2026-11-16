package org.liviu.ai.individual

import org.liviu.ai.AiClient
import org.liviu.ai.LmStudioClient
import org.liviu.ai.Message
import org.liviu.ai.Role
import org.liviu.ai.AiResponse


class Agent(private val name: String, private val personality: String, private val aiClient: AiClient) {
    fun chat(): String {
        val aiResponse = aiClient.chat(listOf(Message(role = Role.USER, content = "Hello, my name is $name. I am a $personality agent.")))
        return aiResponse.text
    }
}