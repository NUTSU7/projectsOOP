package org.liviu.ai.individual

enum class Role(val wire: String) {
    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant"),
    TOOL("tool")
}

data class Message(val role: Role, val content: String)
