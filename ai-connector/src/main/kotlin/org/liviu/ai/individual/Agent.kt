package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message
import org.liviu.ai.aiclient.LmStudioClient
import org.liviu.logger.Logger
import org.liviu.ai.aiclient.AiConnectorException




class Agent(private val name: String, private val personality: String, private val memory: Memory, private val client: AiClient) {
    companion object{
        val logger = Logger("logger")
    }
    
    fun chat(input: String): String {
        
        memory.add(Message(role = Role.USER, content = input))
        logger.trace("Added a message to memory")
        
        val messages = memory.getMessages()
        
        val aiResponse = try{
            client.chat(messages, 
                "Your name is $name and you are a $personality agent."
            )
        } catch (e: AiConnectorException) {
            logger.error("LM Studio is not running")
            return "Error"
        }
        logger.trace("Request sent (${messages.size} messages)")
        
        logger.info("\nPrompt tokens: ${aiResponse.promptTokens} \n" + 
                "Completion tokens: ${aiResponse.completionTokens} \n" + 
                "Tokens per second: ${aiResponse.completionTokens / aiResponse.seconds}")
        
        memory.add(Message(role = Role.ASSISTANT, content = aiResponse.text))
        return aiResponse.text
    }
}