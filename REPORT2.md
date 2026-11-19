# Laboratory Report: Formatters, Sinks and Local AI Agent
**Student: Bivol Liviu FAF-253**\
**Programming language: Kotlin (Gradle)**\
**Track:Local AI Agent**

# 1. The logger, split in three
**What is the single responsibility of Logger, Formatter, and ConsoleSink?**
- `Logger` only handles the types of logging it can do. It compares the level with the static `level` and then hands the message to the formatter and the sink.
```kotlin
private fun log(msgLevel: LogLevel, message: String, args: Array<out Any>) {
    if (msgLevel.ordinal < level.ordinal) return
    sink.write(formatter.formatMessage(name, msgLevel, message, args))
}
```
- `Formatter` only builds and formats the text.
- `ConsoleSink` is only responsible of printing the message to the console.

**What happens in your formatter if the message itself contains "%n"?**
- It depends on whether there are arguments. The `%n` replacement runs before `%m` is filled in, so a `%n` that comes from the message is not replaced by the name.
- With no arguments, `logger.info("file%n")` prints `file%n` as it is.
- With arguments, the `%` in the message takes the next argument, so `logger.info("x %n", 5)` prints `x 5`.
- `%r` is the exception: it is replaced last, so a `%r` inside the message would get a random text.

**Which console prints did you remove, and where do they live now?**
- The old `println(formatMessage(...))` inside `Logger.log` was moved. It is now `println(message)` in `ConsoleSink.write`, so the logger can print to any sink.

# 2. Composition and aggregation
**Show the two Logger constructors. Which one is composition, which one is aggregation? Explain with object lifetimes.**
```kotlin
class Logger(private val name: String, private val formatter: Formatter, private val sink: Sink) {

    constructor(name: String): this(name, Formatter(), ConsoleSink()) {
```
- The secondary constructor is composition. `Formatter()` and `ConsoleSink()` are created inside, nobody else has them, so they live and die with the `Logger`.
- The primary constructor is aggregation. `formatter` and `sink` are given from outside, so they can outlive the logger or be shared with other loggers.

**Find one more example of each in your track. Include snippets.**
- Aggregation: `Agent` gets its memory and client from `main`, it does not create them.
```kotlin
val client = LmStudioClient()
val agent = Agent("Pirate", "a grumpy pirate who answers in short sentences", Memory.tier2(), client)
```
`Memory` and `client` live as long as `main`, not as long as the agent.
- Composition: `LmStudioClient` creates its own mapper and never gives it away.
```kotlin
override fun formatInput(messages: List<Message>, systemPrompt: String): String {
    mapper = ObjectMapper()
```

**Track 2: what would happen if two agents shared one Memory object?**
- Both agents write into the same `Deque`, so the capacity is shared. The messages of one agent might push out the messages of the other.
- Each agent sends the other one's lines to LM Studio as its own history, while it still has its own system prompt. So the answers will probably mix up.

# 3. Static vs shared objects
**In Lab 1 the format was static. Now it is an object that can be shared. What did you gain? What did you lose?**
- I gained that `Formatter` is an object, so `Logger` gets it from outside and I can pass a different formatter.
- I lost the ability to have a per-object format yet. `Formatter(format)` sets `Companion.format = format`, so `format` is still shared. Two formatters with different formats overwrite each other.

**Why is the level still static? Would you change that?**
- `setLevel` changes one `level` for every `Logger`, so one call in `main` changes the level of logging of the whole app.
- I would change it if diffrent modules of the app would need diffrent logging levels at the same time.

# 4. Your track
**Track 2: how does the tool protocol work, step by step?**
1. `Agent.chat` adds the user input to `Memory`. The tiers keep 4, 6 or 8 messages, and the oldest one is dropped when the memory gets full.
2. `LmStudioClient.formatInput` puts the whole memory into one string (`Role:user Message:...; ...`) with the system prompt, and sends it as JSON to `/api/v1/chat/`.
3. `formatOutput` reads the `message` from the `output` array and the numbers from `stats`.
4. `Agent` logs the token numbers and saves the assistant reply in memory.
5. `verifyToolCall` takes the text from the first `{` to the last `}` and parses it with `readTree`. If it parses to an object with a `tool` field, the `tool` and `input` values are read from the fields. Then `Toolbox.run` is called and its result goes back into `chat` with `Role.TOOL`
6. If there is no tool call, the reply is returned.  

**Measurements (model google/gemma-4-e2b, 10-turn, single run)***

**Pirate personality ("a grumpy pirate who answers in short sentences")**

| Turn | tier1 input tok | tier1 tok/s | tier3 input tok | tier3 tok/s |
|---|---|---|---|---|
| 1 | 113 | 34.0 | 113 | 38.1 |
| 2 | 141 | 34.8 | 141 | 32.4 |
| 3 | 190 | 34.4 | 199 | 43.9 |
| 4 | 191 | 38.1 | 221 | 38.0 |
| 5 | 161 | 44.2 | 239 | 39.8 |
| 6 | 153 | 38.4 | 234 | 36.4 |
| 7 | 150 | 36.6 | 203 | 36.5 |
| 8 | 159 | 35.6 | 210 | 37.8 |
| 9 | 164 | 38.3 | 211 | 42.3 |
| 10 | 162 | 39.8 | 211 | 37.6 |
| **Average** | **158.4** | **37.4** | **198.2** | **38.3** |

Tier1 input stays between 113 and 191 tokens. Tier3 rises to about 210–240 and holds. Tokens/s stays between 32 and 44 on both tiers, with no trend.

**Philosopher personality ("a philosopher who answers with thoughtful questions and reasoned arguments")**

| Turn | tier1 input tok | tier1 tok/s | tier3 input tok | tier3 tok/s |
|---|---|---|---|---|
| 1 | 115 | 33.1 | 115 | 32.9 |
| 2 | 197 | 33.5 | 189 | 33.2 |
| 3 | 384 | 32.8 | 765 | 33.1 |
| 4 | 548 | 32.2 | 950 | 32.7 |
| 5 | 650 | 32.0 | 1230 | 32.6 |
| 6 | 543 | 35.0 | 1210 | 34.5 |
| 7 | 281 | 33.6 | 667 | 32.8 |
| 8 | 203 | 33.1 | 696 | 33.0 |
| 9 | 608 | 34.1 | 863 | 33.7 |
| 10 | 613 | 35.2 | 872 | 35.2 |
| **Average** | **414.2** | **33.5** | **755.7** | **33.4** |

The philosopher gives long replies, so input grows fast. Tier1 drops back to about 200 when its window is cut (turns 7–8). Tier3 keeps more of the history, so its input is much higher from turn 3 onward. Tokens/s stays between 32 and 35 on both tiers but it is slower probably because of the higher input tokens. Also it seems the less the context is filled up the longer the replies the agent offers.  

**Prompts that broke tool protocol**
- A normal answer that shows JSON gets run as a tool call. Fix: Enforced `verifyToolCall()`
- Wrong answers from tool calls, Fix: Enforcing `systemPrompt`
- Chained request: the model claims tools ran without calling them

# 5. Reflection
**Where did you stare at an empty method the longest? What finally unblocked you?**
- I haven't started at any empty method. Mostly everything was straight forward, though the toolcall was a bit weird as you basically had to transform Agent `chat()` into a recurssive function for a tool call while also keeping track of every message and the tool call in itself.

**If you had more time, what would you improve, and why?**
- `Memory` - It definetly can be tweaked so you store only the most valuable stuff and not drop important messages.
- `Tool Calling` - Agent doesn't seem to like to make tool calls and even when it does, it doesnt use the answers from the tool calls.
- `System Prompt` - Probably it is the cause for like 70% of the issues as its vague and definetly causes issues for tool calling. 