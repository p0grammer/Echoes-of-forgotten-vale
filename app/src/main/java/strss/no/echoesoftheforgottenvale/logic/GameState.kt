package strss.no.echoesoftheforgottenvale.logic

class GameState {
    private val mercifulFlags = setOf("acceptedGuilt", "releasedSouls", "sparedDarian", "soughtRepair")
    private val ruthlessFlags = setOf("justifiedRitual", "boundSouls", "killedDarian", "soughtPower")

    var humanity: Int = 0
    var corruption: Int = 0
    var memory: Int = 0

    // Dialogue history for the "History" feature
    val dialogueHistory = mutableListOf<Pair<String?, String>>()
    val actualChoices: MutableList<String> = mutableListOf()
    val perceivedChoices: MutableList<String> = mutableListOf()
    val storyFlags: MutableSet<String> = mutableSetOf()
    val sceneVisitCounts: MutableMap<String, Int> = mutableMapOf()
    var falseMemoryText: String? = null

    val memoryInstability: Int
        get() {
            val contradictionCount = actualChoices.indices.count { index ->
                actualChoices[index] != perceivedChoices.getOrNull(index)
            }
            val falseMemoryBonus = if (hasFlag("falseMemory")) 4 else 0
            return (corruption * 2 + contradictionCount * 3 + falseMemoryBonus - memory).coerceAtLeast(0)
        }

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

    fun recordChoice(actualChoice: String, perceivedChoice: String) {
        actualChoices.add(actualChoice)
        perceivedChoices.add(perceivedChoice)
    }

    fun recordSceneVisit(sceneId: String): Int {
        val visits = (sceneVisitCounts[sceneId] ?: 0) + 1
        sceneVisitCounts[sceneId] = visits
        return visits
    }

    fun getSceneVisits(sceneId: String): Int = sceneVisitCounts[sceneId] ?: 0

    fun markFlag(flag: String) {
        storyFlags.add(flag)
    }

    fun clearFlag(flag: String) {
        storyFlags.remove(flag)
    }

    fun hasFlag(flag: String): Boolean = storyFlags.contains(flag)

    fun countDistortedChoices(): Int {
        return actualChoices.indices.count { index ->
            actualChoices[index] != perceivedChoices.getOrNull(index)
        }
    }

    fun mercyScore(): Int = mercifulFlags.count(storyFlags::contains)

    fun ruinScore(): Int = ruthlessFlags.count(storyFlags::contains)

    fun latestContradiction(): Pair<String, String>? {
        for (index in actualChoices.indices.reversed()) {
            val actualChoice = actualChoices[index]
            val perceivedChoice = perceivedChoices.getOrNull(index) ?: continue
            if (actualChoice != perceivedChoice) {
                return actualChoice to perceivedChoice
            }
        }
        return null
    }

    fun reset() {
        humanity = 0
        corruption = 0
        memory = 0
        dialogueHistory.clear()
        actualChoices.clear()
        perceivedChoices.clear()
        storyFlags.clear()
        sceneVisitCounts.clear()
        falseMemoryText = null
    }

    fun getStatsMap(): Map<String, Int> {
        return mapOf(
            "Humanity" to humanity,
            "Corruption" to corruption,
            "Memory" to memory,
            "Instability" to memoryInstability,
            "Distortion" to countDistortedChoices(),
            "FalseMemory" to if (hasFlag("falseMemory")) 1 else 0,
            "Mercy" to mercyScore(),
            "Ruin" to ruinScore()
        )
    }
}
