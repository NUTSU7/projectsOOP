package org.parenthesesvalidator
import java.util.Stack
import org.logger.*

class ParenthesesValidator(logLevel: LogLevel, format: String) {
    
    companion object {
        val logger: Logger = Logger("Parentheses")
    }
    
    init {
        logger.setLevel(logLevel)
        if(format.isNotEmpty()) logger.setFormat(format)
    }
    
    
    fun isValid(str: String): Boolean {
        /*if(str.length % 2 != 0){
            logger.error("String length is odd, cannot be valid")
            return false
        } 
        if(str.isEmpty()){
            logger.error("String is empty")
            return false
        } */
        
        val stack = Stack<Char>()
        var errorMsg = ""
        
        var i=-1
        for(c in str){
            i++
            if(c =='('){
                logger.trace("Found '('")
                stack.push(c)
            }
            
            else if(c == ')'){
                if(stack.isEmpty()){
                    println(str)
                    print(" ".repeat(i))
                    println("^")
                    errorMsg = "Found ')' but theres no matching '('"
                    logger.warn(errorMsg)
                    break
                    //return false
                } 
                
                logger.info("Found ')'")
                stack.pop()
            }
        }
        
        if(stack.isEmpty() && errorMsg.isEmpty()){
            logger.info("The string '$str' is valid")
            return true
        } 
        
        logger.info("The string '$str' is invalid")
        return false
    }
}