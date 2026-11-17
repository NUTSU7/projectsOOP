package org.liviu.ai.individual


class Toolbox {
    private fun get_time(): String {
        val currentTime = java.time.LocalDateTime.now()
        return currentTime.toString()
    }
    
    private fun get_date(): String {
        val currentDate = java.time.LocalDate.now()
        return currentDate.toString()
    }
    
    private fun calculator(expression: String): String {
        val a: Double
        val b: Double
        val operator: String
        val parts = expression.split(" ")
        if (parts.size != 3)
            return "Not good math"
            
        a = parts[0].toDoubleOrNull() ?: return "Not good math"
        operator = parts[1]
        b = parts[2].toDoubleOrNull() ?: return "Not good math"
        
        return when (operator) {
            "+" -> (a + b).toString()
            "-" -> (a - b).toString()
            "*" -> (a * b).toString()
            "/" -> if (b != 0.0) (a / b).toString() else "Division by zero"
            else -> "Not good math"
        }
    }
    
    private fun roll_dice(): String {
        val sides = 6
        val result = (1..sides).random()
        return "Rolled a dice: $result"
        
    }
    
    fun run(toolname: String, input: String): String {
        return when (toolname) {
            "get_time" -> get_time()
            "get_date" -> get_date()
            "calculator" -> calculator(input)
            "rooll_dice" -> roll_dice()
            else -> {
                Agent.logger.error("Tool not found")
                "Tool not found"
            }
        }
    }
}