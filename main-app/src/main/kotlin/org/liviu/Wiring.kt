package org.liviu

import org.liviu.ai.AiClient
import org.liviu.ai.LmStudioClient
import org.liviu.logger.Logger

fun createAiClient(logger: Logger): AiClient =
    LmStudioClient(log = { message -> logger.info(message) })
