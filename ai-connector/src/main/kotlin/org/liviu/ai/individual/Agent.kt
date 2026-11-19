package org.liviu.ai.individual

import org.liviu.ai.aiclient.AiClient
import org.liviu.ai.individual.Message
import org.liviu.ai.aiclient.LmStudioClient
import org.liviu.logger.Logger
import org.liviu.ai.aiclient.AiConnectorException
import org.liviu.ai.individual.Toolbox
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException



class Agent(private val name: String, private val personality: String, private val memory: Memory, private val client: AiClient) {
    companion object{
        val logger = Logger("logger")
        val toolbox = Toolbox()
        private val mapper = ObjectMapper()
    }
    
    private val systemPrompt: String = "Your name is $name and you are a $personality agent." + 
            """If you need a tool, answer ONLY with a JSON object, like:
            { "tool": "calculator", "input": "12 * 7" }
            Available tools: get_time, get_date, calculator, roll_dice.
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
        
        memory.add(Message(role = Role.ASSISTANT, content = aiResponse.text))
        
        val (toolCall, toolInfo) = verifyToolCall(aiResponse.text)
        
        if(!toolCall) return aiResponse.text
        
        val (toolName, toolInput) = toolInfo
        
        return chat("$toolName $toolInput - " + toolbox.run(toolName, toolInput), Role.TOOL)
    }
    
    private fun verifyToolCall(text: String): Pair<Boolean, Pair<String, String>> {
        val start = text.indexOf('{')
        val end = text.lastIndexOf('}')
        if (start == -1 || end <= start) return Pair(false, Pair("", ""))

        val node = try {
            mapper.readTree(text.substring(start, end + 1))
        } catch (e: JsonProcessingException) {
            return Pair(false, Pair("", ""))
        }

        if (!node.isObject || !node.has("tool")) return Pair(false, Pair("", ""))
        return Pair(true, Pair(node.path("tool").asText(), node.path("input").asText("")))
    }
    
}