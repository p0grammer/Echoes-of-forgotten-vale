package strss.no.echoesoftheforgottenvale.model

data class Scene(
    val id: String,
    val text: String,
    val speaker: String? = null,
    val backgroundResId: Int,
    val characterResId: Int,
    val choices: List<Choice>
)
