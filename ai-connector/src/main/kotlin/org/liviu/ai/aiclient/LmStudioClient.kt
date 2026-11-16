package org.liviu.ai.aiclient

import com.fasterxml.jackson.databind.ObjectMapper
import org.liviu.ai.individual.Message
import java.net.http.HttpClient

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
                "input" to messages.joinToString("\n") { "Role:${it.role.wire} Message:${it.content}" }
            )
        )
    }
}
