# Laboratory Report — Logger and Parentheses Validator
**Student: Bivol Liviu FAF-253**\
**Programming language: Kotlin (Gradle)**

# 1. Program Structure
**Logger**
- Logger is responsible for printing, formatting the message and letting the user choose the level of the log and its format. Basically prints what the program does in the terminal.  

**ParanthesesValidator**
- Is responsible for solving the parantheses problem by using a stack which LIFO to check whether the sequence is valid, basically has a start and an end.

**Collaboration**
- ParanthesesValidator uses the Logger class to log its solution and findings on each step.

# 2. Classes and Objects
**What is a class in your implementation?**
- Basically the general description of an object with their own attributes and methods.  

**Give an example of an object created from a class.**
```kotlin
companion object {
    val logger: Logger = Logger("Parentheses")
}
...
val parenthesesValidator = ParenthesesValidator(logLevel, option3 ?: "")
```  

**What state does each object store?**
- `Logger` stores only its `name` as its the only variable thats static, the other static variables `level, format, TIME_FORMAT, RANDOM_MESSAGES` are not held by each instance but rather stored at class-level

# 3. Static and Instance Members
**Which members of `Logger` are static, and why?**
- `level, format, TIME_FORMAT, and RANDOM MESSAGES` they are static (companion object - Kotlin) because they have to be the same across all instances of the Logger class, so changing it would affect all in the instances of Logger inside the app.
```kotlin
companion object {
    var level = LogLevel.INFO
    var format = "[%t] [%l] %n: %m, %r"
    var TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss")
        
    private val RANDOM_MESSAGES = mapOf(
```  

**Which member belongs to an individual logger instance?**
- `name` so each instance has their own name, included in the constructor.
```kotlin
class Logger(private val name: String) {
```  

**What happens when the logging level is changed? Does it affect every logger instance?**
-  When the logging level is changed it changes the static variable `level` and since its static it affects all the instances of logger in the whole app.
```kotlin
companion object {
    var level = LogLevel.INFO
    ...

    fun setLevel(newLevel: LogLevel) {
    level = newLevel
}
```

# 4. Methods and Responsibilities
**What is the responsibility of the `log` method?**
- To choose what levels of the log to print my comparing the ordinal of the logLevels and prints only levels under. Also it formats the message using formatMessage() method which chooses the random message if needed and replaces the placeholders.  

**Why does `ParenthesesValidator` use a logger instead of implementing logging itself?**
- Because they have 2 diffrent purposes and are 2 diffrent objects one is a validator one logger so they 2 different classes, also it would result in reimplamantation of the same code. This way we can use the logger for further implementation for other classes and also we split the logic of the validator from that of the logger. 

**Identify one example of a method call between objects or classes in your program.**.
- `logger.info("The string '$str' is valid")` in ParanthesesValidator class it calls the info method of logger instance that has been create inside of it.

# 5. Testing and Correctness
**Give one valid and one invalid input for the parentheses validator.**
- `)((()))` - not valid, `(())((()())())` - valid.  

**Explain how the program determines whether the input is valid.**
- It uses a stack which LIFO (Last In First Out) so whenever it finds an `(` it pushes it into the stack and when finds a `)` it pops the latest entered `(` from the stack so basically if theres no `(` to pop when a `)` appears the sequence is automatically invalid, in the end if it went through the full sequence succesfully it just checks if the stack is empty which translates to every `(` found its pair `)`.  

**Describe one test that helped you find or prevent an error.**
- `)((()))` because of the leading `)` i found out i should break the loop and not check the whole sequence because it doesnt have a pair in the stack already and just skipping it would result in a valid return as i dont push into the stack `)`

# 6. Reflection
**What was the most important OOP concept you applied in this laboratory?**
- `Encapsulation` as logger incapsulates it's name and most of its methods which are related to printing and formatting so no outside class has access to it's methods and logic except through controlled endpoints like `trace, info, warn, error`. Also used static and instance state variables, static being class state and instance being tied to the instance of the class.  

**If you had more time, what would you improve in your design, and why?**
- Not sure, I think I would try to make the class logger in itself static and the name just pass as a parameter for the accesable methods so theres no need to create a lot of instances of the logger if the project gets bigger, as the only different attribute between instances is the name.