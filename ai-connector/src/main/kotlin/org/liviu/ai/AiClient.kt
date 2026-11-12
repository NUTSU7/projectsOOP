package org.liviu.ai

data class AiResponse(
    val text: String,
    val promptTokens: Int,
    val completionTokens: Int,
    val seconds: Double
)

interface AiClient {
    fun complete(messages: List<Message>): AiResponse
}

fun interface ConnectorLog {
    fun log(message: String)
}

class AiConnectorException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
