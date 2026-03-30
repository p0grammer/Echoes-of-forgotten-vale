package strss.no.echoesoftheforgottenvale.model

data class Condition(
    val statName: String = "",
    val operator: String = "==",
    val value: Int = 0,
    val allOf: List<Condition> = emptyList(),
    val anyOf: List<Condition> = emptyList()
) {
    fun isMet(stats: Map<String, Int>): Boolean {
        if (allOf.isNotEmpty() && allOf.any { !it.isMet(stats) }) {
            return false
        }
        if (anyOf.isNotEmpty() && anyOf.none { it.isMet(stats) }) {
            return false
        }
        if (statName.isBlank()) {
            return allOf.isNotEmpty() || anyOf.isNotEmpty()
        }
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
