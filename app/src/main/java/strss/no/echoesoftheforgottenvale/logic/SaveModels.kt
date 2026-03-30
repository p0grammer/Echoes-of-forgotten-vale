package strss.no.echoesoftheforgottenvale.logic

data class SavedDialogueLine(
    val speaker: String?,
    val text: String
)

data class SaveSnapshot(
    val sceneId: String,
    val humanity: Int,
    val corruption: Int,
    val memory: Int,
    val actualChoices: List<String>,
    val perceivedChoices: List<String>,
    val storyFlags: Set<String>,
    val sceneVisitCounts: Map<String, Int>,
    val falseMemoryText: String?,
    val dialogueHistory: List<SavedDialogueLine>,
    val savedAtMillis: Long
)

data class SaveSlotSummary(
    val slot: Int,
    val exists: Boolean,
    val sceneId: String?,
    val savedAtMillis: Long?
)
