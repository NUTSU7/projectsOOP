package org.liviu.ai.aiclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.liviu.ai.individual.Message
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

data class AiResponse(
    val text: String,
    val inputTokens: Int,
    val outputTokens: Int,
    val tokensPerSecond: Double
)

interface AiClientInterface {
    fun chat(messages: List<Message>, systemPrompt: String): AiResponse
}

abstract class AiClient(
    protected var baseUrl: String,
    protected val model: String,
    protected val http: HttpClient,
    protected var mapper: ObjectMapper = ObjectMapper()
) : AiClientInterface {

    protected open val providerName: String = "AI Provider"
    
    protected abstract fun formatInput(messages: List<Message>, systemPrompt: String): String
    
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
    
    protected abstract fun formatOutput(output: Pair<HttpResponse<String>, Double>): AiResponse

    abstract override fun chat(messages: List<Message>, systemPrompt: String): AiResponse

}

class AiConnectorException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
