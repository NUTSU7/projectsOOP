package org.liviu.ai

enum class Role(val wire: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant")
}

data class Message(val role: Role, val content: String)
