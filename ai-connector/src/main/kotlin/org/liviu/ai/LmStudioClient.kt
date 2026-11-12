package org.liviu.ai

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class LmStudioClient(
    private val baseUrl: String = "http://localhost:1234",
    private val model: String = "local-model",
    private val log: ConnectorLog = ConnectorLog { },
    private val http: HttpClient = HttpClient.newHttpClient()
) : AiClient {

    private val mapper = ObjectMapper()

    override fun complete(messages: List<Message>): AiResponse {
        require(messages.isNotEmpty()) { "messages must not be empty" }

        val body = mapper.writeValueAsString(
            mapOf(
                "model" to model,
                "messages" to messages.map { mapOf("role" to it.role.wire, "content" to it.content) }
            )
        )
        val request = HttpRequest.newBuilder(URI.create("$baseUrl/v1/chat/completions"))
            .header("Content-Type", "application/json")
            .timeout(Duration.ofSeconds(120))
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        log.log("Sending ${messages.size} message(s) to $baseUrl")
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

        val root = try {
            mapper.readTree(response.body())
        } catch (e: IOException) {
            throw AiConnectorException("Response is not valid JSON: ${response.body()}", e)
        }
        val content = root.path("choices").path(0).path("message").path("content")
        if (!content.isTextual) {
            throw AiConnectorException("Unexpected response format: ${response.body()}")
        }
        val usage = root.path("usage")
        val result = AiResponse(
            text = content.asText(),
            promptTokens = usage.path("prompt_tokens").asInt(0),
            completionTokens = usage.path("completion_tokens").asInt(0),
            seconds = seconds
        )
        log.log("Received response in %.2fs (%d prompt / %d completion tokens)"
            .format(seconds, result.promptTokens, result.completionTokens))
        return result
    }
}
