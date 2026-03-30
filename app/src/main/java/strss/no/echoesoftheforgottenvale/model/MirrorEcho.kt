package strss.no.echoesoftheforgottenvale.model

data class MirrorEcho(
    val actualTemplate: String,
    val perceivedTemplate: String,
    val requiredFlags: Set<String> = emptySet()
)
