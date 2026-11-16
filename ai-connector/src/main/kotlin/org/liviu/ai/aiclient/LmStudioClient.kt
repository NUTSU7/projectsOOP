package org.liviu.ai.aiclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.liviu.ai.individual.Message
import java.net.http.HttpClient
import java.net.http.HttpResponse
import java.io.IOException

class LmStudioClient(
    baseUrl: String = "http://127.0.0.1:1234",
    model: String = "google/gemma-4-e2b",
    http: HttpClient = HttpClient.newHttpClient()
) : AiClient(baseUrl, model, http) {
    override val providerName: String = "LM Studio"
    
    override fun chat(messages: List<Message>, systemPrompt: String): AiResponse {
        val body = formatInput(messages, systemPrompt)
        val url = "$baseUrl/api/v1/chat/"
        val output = sendRequest(body, url)
        return formatOutput(output)
    }

    override fun formatInput(messages: List<Message>, systemPrompt: String): String {
        mapper = ObjectMapper()
        return mapper.writeValueAsString(
            mapOf(
                "model" to model,
                "input" to messages.joinToString("; ") { "Role:${it.role.wire} Message:${it.content}" },
                "system_prompt" to systemPrompt
            )
        )
    }

    override fun formatOutput(output: Pair<HttpResponse<String>, Double>): AiResponse {
        val (response, seconds) = output
        mapper = ObjectMapper()

        val root = try {
            mapper.readTree(response.body())
        } catch (e: IOException) {
            throw AiConnectorException("Response is not valid JSON: ${response.body()}", e)
        }

        val outputNode = root.path("output")
        if (!outputNode.isArray) {
            throw AiConnectorException("Unexpected response format (missing 'output' array): ${response.body()}")
        }

        val message = outputNode
            .filter { it.path("type").asText() == "message" && it.path("content").isTextual }
            .map { it.path("content").asText() }

        if (message.isEmpty()) {
            throw AiConnectorException("No message found in response: ${response.body()}")
        }

        val usage = root.path("stats")
        val result = AiResponse(
            text = message.lastOrNull() ?: "",
            inputTokens = usage.path("input_tokens").asInt(0),
            outputTokens = usage.path("total_output_tokens").asInt(0),
            tokensPerSecond = usage.path("tokens_per_second").asDouble(0.0)
        )
        return result
    }
}
