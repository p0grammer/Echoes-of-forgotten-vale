package strss.no.echoesoftheforgottenvale.model

data class Scene(
    val id: String,
    val text: String,
    val speaker: String? = null,
    val backgroundResId: Int,
    val characterResId: Int,
    val choices: List<Choice>,
    val stableVoice: String? = null,
    val distortedVoice: String? = null,
    val revisitTextVariants: List<String> = emptyList(),
    val conflictingDialogues: List<ConflictingDialogue> = emptyList(),
    val mirrorEchoes: List<MirrorEcho> = emptyList(),
    val falseMemoryDialogue: String? = null
)
