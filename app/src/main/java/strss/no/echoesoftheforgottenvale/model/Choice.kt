package strss.no.echoesoftheforgottenvale.model

data class Choice(
    val text: String,
    val nextSceneId: String,
    val statChanges: Map<String, Int> = emptyMap(),
    val condition: Condition? = null
)
