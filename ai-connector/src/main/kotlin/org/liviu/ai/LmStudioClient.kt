package org.liviu.ai

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

class LmStudioClient(
    baseUrl: String = "http://127.0.0.1:1234",
    model: String = "google/gemma-4-e2b",
    http: HttpClient = HttpClient.newHttpClient()
) : AiClient(baseUrl, model, http) {
    override val providerName: String = "LM Studio"
    
    override fun formatInput(messages: List<Message>): String {
        mapper = ObjectMapper()
        return mapper.writeValueAsString(
            mapOf(
                "model" to model,
                "input" to messages.joinToString(separator = "\n") { it.content}
            )
        )
    }
}
