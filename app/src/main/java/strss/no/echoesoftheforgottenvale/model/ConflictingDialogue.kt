package strss.no.echoesoftheforgottenvale.model

data class ConflictingDialogue(
    val actualTemplate: String,
    val perceivedTemplate: String,
    val requiredFlags: Set<String> = emptySet(),
    val minimumVisitCount: Int = 0
)
