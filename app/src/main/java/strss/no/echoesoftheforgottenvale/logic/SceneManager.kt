package strss.no.echoesoftheforgottenvale.logic

import strss.no.echoesoftheforgottenvale.model.Scene

class SceneManager {
    private var currentSceneId: String = "start"

    fun getCurrentScene(): Scene? {
        return SceneRepository.getScene(currentSceneId)
    }

    fun goToScene(sceneId: String) {
        if (SceneRepository.scenes.containsKey(sceneId)) {
            currentSceneId = sceneId
        }
    }

    fun isCurrentScene(sceneId: String): Boolean {
        return currentSceneId == sceneId
    }
}
