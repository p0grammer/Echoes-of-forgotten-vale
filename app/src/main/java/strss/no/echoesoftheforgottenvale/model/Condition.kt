package strss.no.echoesoftheforgottenvale.model

data class Condition(
    val statName: String,
    val operator: String,
    val value: Int
) {
    fun isMet(stats: Map<String, Int>): Boolean {
        val currentValue = stats[statName] ?: 0
        return when (operator) {
            ">" -> currentValue > value
            "<" -> currentValue < value
            ">=" -> currentValue >= value
            "<=" -> currentValue <= value
            "==" -> currentValue == value
            else -> true
        }
    }
}
