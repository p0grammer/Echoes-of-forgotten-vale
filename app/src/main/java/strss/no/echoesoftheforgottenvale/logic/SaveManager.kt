package strss.no.echoesoftheforgottenvale.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SaveManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_saves", Context.MODE_PRIVATE)

    fun saveGame(slot: Int, sceneId: String, gameState: GameState) {
        prefs.edit {
            putString("slot_${slot}_scene_id", sceneId)
            putInt("slot_${slot}_humanity", gameState.humanity)
            putInt("slot_${slot}_corruption", gameState.corruption)
            putInt("slot_${slot}_memory", gameState.memory)
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
}
