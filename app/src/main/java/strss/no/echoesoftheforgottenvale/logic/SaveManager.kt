package strss.no.echoesoftheforgottenvale.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SaveManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_saves", Context.MODE_PRIVATE)
    private val listSeparator = "\u001F"
    private val mapSeparator = "\u001E"

    fun saveGame(slot: Int, sceneId: String, gameState: GameState) {
        prefs.edit {
            putString("slot_${slot}_scene_id", sceneId)
            putInt("slot_${slot}_humanity", gameState.humanity)
            putInt("slot_${slot}_corruption", gameState.corruption)
            putInt("slot_${slot}_memory", gameState.memory)
            putString("slot_${slot}_actual_choices", encodeList(gameState.actualChoices))
            putString("slot_${slot}_perceived_choices", encodeList(gameState.perceivedChoices))
            putString("slot_${slot}_flags", encodeList(gameState.storyFlags.toList()))
            putString("slot_${slot}_visits", encodeMap(gameState.sceneVisitCounts))
            putString("slot_${slot}_false_memory", gameState.falseMemoryText)
            putBoolean("slot_${slot}_exists", true)
        }
    }

    fun loadCurrentSceneId(slot: Int = 0): String {
        return prefs.getString("slot_${slot}_scene_id", "start") ?: "start"
    }

    fun loadGameState(gameState: GameState, slot: Int = 0) {
        gameState.humanity = prefs.getInt("slot_${slot}_humanity", 0)
        gameState.corruption = prefs.getInt("slot_${slot}_corruption", 0)
        gameState.memory = prefs.getInt("slot_${slot}_memory", 0)
        gameState.actualChoices.clear()
        gameState.actualChoices.addAll(decodeList(prefs.getString("slot_${slot}_actual_choices", null)))
        gameState.perceivedChoices.clear()
        gameState.perceivedChoices.addAll(decodeList(prefs.getString("slot_${slot}_perceived_choices", null)))
        gameState.storyFlags.clear()
        gameState.storyFlags.addAll(decodeList(prefs.getString("slot_${slot}_flags", null)))
        gameState.sceneVisitCounts.clear()
        gameState.sceneVisitCounts.putAll(decodeMap(prefs.getString("slot_${slot}_visits", null)))
        gameState.falseMemoryText = prefs.getString("slot_${slot}_false_memory", null)
    }

    fun hasSave(slot: Int): Boolean {
        return prefs.getBoolean("slot_${slot}_exists", false)
    }

    fun deleteSave(slot: Int) {
        prefs.edit {
            remove("slot_${slot}_scene_id")
            remove("slot_${slot}_humanity")
            remove("slot_${slot}_corruption")
            remove("slot_${slot}_memory")
            remove("slot_${slot}_actual_choices")
            remove("slot_${slot}_perceived_choices")
            remove("slot_${slot}_flags")
            remove("slot_${slot}_visits")
            remove("slot_${slot}_false_memory")
            putBoolean("slot_${slot}_exists", false)
        }
    }

    // Default legacy support for "current" session
    fun saveSession(sceneId: String, gameState: GameState) {
        saveGame(0, sceneId, gameState)
    }

    fun loadCurrentSceneId(): String = loadCurrentSceneId(0)
    fun loadGameState(gameState: GameState) = loadGameState(gameState, 0)
    fun clearSave() = deleteSave(0)

    private fun encodeList(values: List<String>): String {
        return values.joinToString(listSeparator)
    }

    private fun decodeList(value: String?): List<String> {
        return value
            ?.takeIf { it.isNotEmpty() }
            ?.split(listSeparator)
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
    }

    private fun encodeMap(values: Map<String, Int>): String {
        return values.entries.joinToString(listSeparator) { (key, number) ->
            "$key$mapSeparator$number"
        }
    }

    private fun decodeMap(value: String?): Map<String, Int> {
        if (value.isNullOrEmpty()) return emptyMap()
        return value.split(listSeparator).mapNotNull { entry ->
            val parts = entry.split(mapSeparator)
            if (parts.size != 2) {
                null
            } else {
                parts[0] to (parts[1].toIntOrNull() ?: return@mapNotNull null)
            }
        }.toMap()
    }
}
