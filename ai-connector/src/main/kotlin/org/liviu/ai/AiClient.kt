package org.liviu.ai

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

data class AiResponse(
    val text: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val seconds: Double
)

interface AiClientInterface {
    fun chat(messages: List<Message>): AiResponse
}

abstract class AiClient(
    protected var baseUrl: String,
    protected val model: String,
    protected val http: HttpClient,
    protected var mapper: ObjectMapper = ObjectMapper()
) : AiClientInterface {

    protected open val providerName: String = "AI Provider"
    
    protected abstract fun formatInput(messages: List<Message>): String
    
    protected open fun sendRequest(body: String, url: String): Pair<HttpResponse<String>, Double> {
        val request = HttpRequest.newBuilder(URI.create(url))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(120))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val start = System.nanoTime()
        val response = try {
            http.send(request, HttpResponse.BodyHandlers.ofString())
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw AiConnectorException("Request to LM Studio interrupted", e)
        } catch (e: IOException) {
            throw AiConnectorException("Could not reach LM Studio at $baseUrl", e)
        }
        val seconds = (System.nanoTime() - start) / 1_000_000_000.0

        if (response.statusCode() != 200) {
            throw AiConnectorException("LM Studio returned HTTP ${response.statusCode()}: ${response.body()}")
        }

        return Pair(response, seconds)
    }
    
    protected open fun formatOutput(output: Pair<HttpResponse<String>, Double>): AiResponse {
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
        
        val usage = root.path("usage")
        val result = AiResponse(
            text = message.lastOrNull() ?: "",
            promptTokens = usage.path("prompt_tokens").asInt(0),
            completionTokens = usage.path("completion_tokens").asInt(0),
            seconds = seconds
        )
        return result
    }

    override fun chat(messages: List<Message>): AiResponse {
        val body = formatInput(messages)
        val url = "$baseUrl/api/v1/chat/"
        val output = sendRequest(body, url)
        return formatOutput(output)
    }
}

class AiConnectorException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
