package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message
import org.liviu.ai.aiclient.LmStudioClient
import org.liviu.logger.Logger
import org.liviu.ai.aiclient.AiConnectorException
import org.liviu.ai.individual.Toolbox




class Agent(private val name: String, private val personality: String, private val memory: Memory, private val client: AiClient) {
    companion object{
        val logger = Logger("logger")
        val toolbox = Toolbox()
    }
    
    private val systemPrompt: String = "Your name is $name and you are a $personality agent." + 
            """If you need a tool, answer ONLY with a JSON object, like:
            { "tool": "calculator", "input": "12 * 7" }
            If you do not need a tool, answer normally (NOT JSON)."""
    fun chat(input: String, role: Role): String {
        if(input.isEmpty()) {
            logger.warn("Input is empty")
            return "Input is empty"
        }
        
        if(input == "Tool not found" && role == Role.TOOL){
            logger.error(input)
            memory.add(Message(role = role, content = input))
            return ""
        }
        
        memory.add(Message(role = role, content = input))
        logger.trace("Added a message to memory")
        
        val messages = memory.getMessages()
        
        val aiResponse = try{
            client.chat(messages, 
                systemPrompt
            )
        } catch (e: AiConnectorException) {
            logger.error("LM Studio is not running")
            return "Error"
        }
        logger.trace("Request sent (${messages.size} messages)")
        
        logger.info("\nInput tokens: ${aiResponse.inputTokens} \n" + 
                "Output tokens: ${aiResponse.outputTokens} \n" + 
                "Tokens per second: ${aiResponse.tokensPerSecond}")
        
        memory.add(Message(role = role, content = aiResponse.text))
        
        val (toolCall, toolInfo) = verifyToolCall(aiResponse.text)
        
        if(!toolCall) return aiResponse.text
        
        val (toolName, toolInput) = toolInfo
        
        return chat("$toolName $toolInput - " + toolbox.run(toolName, toolInput), Role.TOOL)
    }
    
    private fun verifyToolCall(text: String): Pair<Boolean, Pair<String, String>> {
        if(text.contains("tool") && text.startsWith("{") && text.endsWith("}")){
            val toolName = text.substringAfter("{").substringBefore("}")
            val toolInput = text.substringAfter("}").trim()
            return Pair(true, Pair(toolName, toolInput))
        }
        return Pair(false, Pair("", ""))
    }
    
}