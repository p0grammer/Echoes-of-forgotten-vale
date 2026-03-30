package strss.no.echoesoftheforgottenvale.model

data class Choice(
    val text: String,
    val nextSceneId: String,
    val statChanges: Map<String, Int> = emptyMap(),
    val condition: Condition? = null,
    val actualMemory: String = text,
    val perceivedMemoryOptions: List<String> = emptyList(),
    val flagsToSet: Set<String> = emptySet(),
    val flagsToClear: Set<String> = emptySet()
)
