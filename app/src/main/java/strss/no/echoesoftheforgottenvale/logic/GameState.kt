package strss.no.echoesoftheforgottenvale.logic

class GameState {
    var humanity: Int = 0
    var corruption: Int = 0
    var memory: Int = 0
    
    // Dialogue history for the "History" feature
    val dialogueHistory = mutableListOf<Pair<String?, String>>()

    fun applyStatChanges(changes: Map<String, Int>) {
        changes.forEach { (stat, change) ->
            when (stat) {
                "Humanity" -> humanity += change
                "Corruption" -> corruption += change
                "Memory" -> memory += change
            }
        }
    }
    
    fun addToHistory(speaker: String?, text: String) {
        if (dialogueHistory.isEmpty() || dialogueHistory.last().second != text) {
            dialogueHistory.add(speaker to text)
            // Keep only last 50 lines to save memory
            if (dialogueHistory.size > 50) dialogueHistory.removeAt(0)
        }
    }

    fun getStatsMap(): Map<String, Int> {
        return mapOf(
            "Humanity" to humanity,
            "Corruption" to corruption,
            "Memory" to memory
        )
    }
}
