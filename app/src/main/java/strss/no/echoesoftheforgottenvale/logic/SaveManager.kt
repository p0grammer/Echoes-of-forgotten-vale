package strss.no.echoesoftheforgottenvale.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class SaveManager(context: Context) {
    private val legacyPrefs: SharedPreferences =
        context.getSharedPreferences("game_saves", Context.MODE_PRIVATE)
    private val savesDir = File(context.filesDir, "saves").apply { mkdirs() }
    private val listSeparator = "\u001F"
    private val mapSeparator = "\u001E"

    fun saveGame(slot: Int, sceneId: String, gameState: GameState): SaveSlotSummary {
        val snapshot = SaveSnapshot(
            sceneId = sceneId,
            humanity = gameState.humanity,
            corruption = gameState.corruption,
            memory = gameState.memory,
            actualChoices = gameState.actualChoices.toList(),
            perceivedChoices = gameState.perceivedChoices.toList(),
            storyFlags = gameState.storyFlags.toSet(),
            sceneVisitCounts = gameState.sceneVisitCounts.toMap(),
            falseMemoryText = gameState.falseMemoryText,
            dialogueHistory = gameState.dialogueHistory.map { SavedDialogueLine(it.first, it.second) },
            savedAtMillis = System.currentTimeMillis()
        )
        writeSnapshot(slot, snapshot)
        clearLegacySlot(slot)
        return summaryFor(slot, snapshot)
    }

    fun loadGameState(gameState: GameState, slot: Int): String? {
        val snapshot = loadSnapshot(slot) ?: return null
        restoreGameState(gameState, snapshot)
        return snapshot.sceneId
    }

    fun getSlotSummary(slot: Int): SaveSlotSummary {
        val snapshot = loadSnapshot(slot) ?: return SaveSlotSummary(
            slot = slot,
            exists = false,
            sceneId = null,
            savedAtMillis = null
        )
        return summaryFor(slot, snapshot)
    }

    fun hasSave(slot: Int): Boolean = loadSnapshot(slot) != null

    fun deleteSave(slot: Int) {
        val file = saveFile(slot)
        if (file.exists()) {
            file.delete()
        }
        clearLegacySlot(slot)
    }

    private fun writeSnapshot(slot: Int, snapshot: SaveSnapshot) {
        saveFile(slot).writeText(snapshotToJson(snapshot).toString())
    }

    private fun loadSnapshot(slot: Int): SaveSnapshot? {
        val file = saveFile(slot)
        if (file.exists()) {
            runCatching {
                return jsonToSnapshot(JSONObject(file.readText()))
            }
        }

        val legacySnapshot = loadLegacySnapshot(slot) ?: return null
        writeSnapshot(slot, legacySnapshot)
        clearLegacySlot(slot)
        return legacySnapshot
    }

    private fun restoreGameState(gameState: GameState, snapshot: SaveSnapshot) {
        gameState.humanity = snapshot.humanity
        gameState.corruption = snapshot.corruption
        gameState.memory = snapshot.memory

        gameState.actualChoices.clear()
        gameState.actualChoices.addAll(snapshot.actualChoices)

        gameState.perceivedChoices.clear()
        gameState.perceivedChoices.addAll(snapshot.perceivedChoices)

        gameState.storyFlags.clear()
        gameState.storyFlags.addAll(snapshot.storyFlags)

        gameState.sceneVisitCounts.clear()
        gameState.sceneVisitCounts.putAll(snapshot.sceneVisitCounts)

        gameState.dialogueHistory.clear()
        gameState.dialogueHistory.addAll(snapshot.dialogueHistory.map { it.speaker to it.text })

        gameState.falseMemoryText = snapshot.falseMemoryText
    }

    private fun summaryFor(slot: Int, snapshot: SaveSnapshot): SaveSlotSummary {
        return SaveSlotSummary(
            slot = slot,
            exists = true,
            sceneId = snapshot.sceneId,
            savedAtMillis = snapshot.savedAtMillis
        )
    }

    private fun snapshotToJson(snapshot: SaveSnapshot): JSONObject {
        val history = JSONArray().apply {
            snapshot.dialogueHistory.forEach { line ->
                put(
                    JSONObject().apply {
                        put("speaker", line.speaker ?: JSONObject.NULL)
                        put("text", line.text)
                    }
                )
            }
        }

        val visits = JSONObject().apply {
            snapshot.sceneVisitCounts.toSortedMap().forEach { (sceneId, count) ->
                put(sceneId, count)
            }
        }

        return JSONObject().apply {
            put("sceneId", snapshot.sceneId)
            put("humanity", snapshot.humanity)
            put("corruption", snapshot.corruption)
            put("memory", snapshot.memory)
            put("actualChoices", JSONArray(snapshot.actualChoices))
            put("perceivedChoices", JSONArray(snapshot.perceivedChoices))
            put("storyFlags", JSONArray(snapshot.storyFlags.toList().sorted()))
            put("sceneVisitCounts", visits)
            put("falseMemoryText", snapshot.falseMemoryText ?: JSONObject.NULL)
            put("dialogueHistory", history)
            put("savedAtMillis", snapshot.savedAtMillis)
        }
    }

    private fun jsonToSnapshot(json: JSONObject): SaveSnapshot {
        return SaveSnapshot(
            sceneId = json.optString("sceneId", "start"),
            humanity = json.optInt("humanity", 0),
            corruption = json.optInt("corruption", 0),
            memory = json.optInt("memory", 0),
            actualChoices = jsonArrayToStringList(json.optJSONArray("actualChoices")),
            perceivedChoices = jsonArrayToStringList(json.optJSONArray("perceivedChoices")),
            storyFlags = jsonArrayToStringList(json.optJSONArray("storyFlags")).toSet(),
            sceneVisitCounts = jsonToVisitMap(json.optJSONObject("sceneVisitCounts")),
            falseMemoryText = json.optNullableString("falseMemoryText"),
            dialogueHistory = jsonToHistory(json.optJSONArray("dialogueHistory")),
            savedAtMillis = json.optLong("savedAtMillis", System.currentTimeMillis())
        )
    }

    private fun jsonArrayToStringList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        return buildList(array.length()) {
            for (index in 0 until array.length()) {
                add(array.optString(index))
            }
        }.filter { it.isNotEmpty() }
    }

    private fun jsonToVisitMap(json: JSONObject?): Map<String, Int> {
        if (json == null) return emptyMap()
        val map = mutableMapOf<String, Int>()
        val keys = json.keys()
        while (keys.hasNext()) {
            val key = keys.next()
            map[key] = json.optInt(key, 0)
        }
        return map
    }

    private fun jsonToHistory(array: JSONArray?): List<SavedDialogueLine> {
        if (array == null) return emptyList()
        return buildList(array.length()) {
            for (index in 0 until array.length()) {
                val item = array.optJSONObject(index) ?: continue
                add(
                    SavedDialogueLine(
                        speaker = item.optNullableString("speaker"),
                        text = item.optString("text")
                    )
                )
            }
        }
    }

    private fun loadLegacySnapshot(slot: Int): SaveSnapshot? {
        if (!legacyPrefs.getBoolean("slot_${slot}_exists", false)) return null
        return SaveSnapshot(
            sceneId = legacyPrefs.getString("slot_${slot}_scene_id", "start") ?: "start",
            humanity = legacyPrefs.getInt("slot_${slot}_humanity", 0),
            corruption = legacyPrefs.getInt("slot_${slot}_corruption", 0),
            memory = legacyPrefs.getInt("slot_${slot}_memory", 0),
            actualChoices = decodeList(legacyPrefs.getString("slot_${slot}_actual_choices", null)),
            perceivedChoices = decodeList(legacyPrefs.getString("slot_${slot}_perceived_choices", null)),
            storyFlags = decodeList(legacyPrefs.getString("slot_${slot}_flags", null)).toSet(),
            sceneVisitCounts = decodeMap(legacyPrefs.getString("slot_${slot}_visits", null)),
            falseMemoryText = legacyPrefs.getString("slot_${slot}_false_memory", null),
            dialogueHistory = emptyList(),
            savedAtMillis = System.currentTimeMillis()
        )
    }

    private fun clearLegacySlot(slot: Int) {
        legacyPrefs.edit {
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

    private fun saveFile(slot: Int): File = File(savesDir, "slot_$slot.json")

    private fun decodeList(value: String?): List<String> {
        return value
            ?.takeIf { it.isNotEmpty() }
            ?.split(listSeparator)
            ?.filter { it.isNotEmpty() }
            ?: emptyList()
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

    private fun JSONObject.optNullableString(key: String): String? {
        if (!has(key) || isNull(key)) return null
        return optString(key).takeIf { it.isNotEmpty() }
    }
}
