package org.liviu

import org.liviu.ai.Message
import org.liviu.ai.Role
import org.liviu.logger.Logger

fun main() {
    val ai = createAiClient(Logger("ai"))
    val reply = ai.complete(listOf(Message(Role.USER, "Say hello in one short sentence.")))
    println("answer: ${reply.text}")
    println("prompt tokens: ${reply.promptTokens}")
    println("completion tokens: ${reply.completionTokens}")
    println("seconds: ${"%.2f".format(reply.seconds)}")
}
