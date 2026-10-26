class ParenthesesValidator {
    
    fun isValid(str: String): Boolean {
        if(str.length % 2 != 0) return false
        if(str.isEmpty()) return true
        
        val stack = Stack<Char>()
        
        for(c in str){
            if(c =='(')
                stack.push(c)
                
            else if(c == ')'){
                if(stack.isEmpty()) return false
                stack.pop()
            }
        }
        return stack.isEmpty()
    }
}