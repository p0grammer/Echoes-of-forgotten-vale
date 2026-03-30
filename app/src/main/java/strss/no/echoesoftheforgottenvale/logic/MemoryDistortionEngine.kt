package strss.no.echoesoftheforgottenvale.logic

import kotlin.random.Random
import strss.no.echoesoftheforgottenvale.model.Choice
import strss.no.echoesoftheforgottenvale.model.ConflictingDialogue
import strss.no.echoesoftheforgottenvale.model.Ending
import strss.no.echoesoftheforgottenvale.model.MirrorEcho
import strss.no.echoesoftheforgottenvale.model.Scene

data class ChoiceMemoryResult(
    val actualChoice: String,
    val perceivedChoice: String,
    val wasDistorted: Boolean,
    val falseMemoryInserted: String?
)

data class ScenePresentation(
    val speaker: String?,
    val text: String,
    val backgroundResId: Int,
    val characterResId: Int
)

data class EndingPresentation(
    val text: String,
    val backgroundResId: Int
)

class MemoryDistortionEngine(
    private val random: Random = Random.Default
) {
    private val falseMemoryPool = listOf(
        "you led the Silver Wing into the mist and barred the gate behind them",
        "you watched Seraphine vanish inside the glass and called it necessary",
        "you erased Aelric's face from the first mirror with your own hand",
        "you heard the nameless one beg for mercy and still closed the Spire"
    )

    fun recordChoice(gameState: GameState, choice: Choice): ChoiceMemoryResult {
        choice.flagsToSet.forEach(gameState::markFlag)
        choice.flagsToClear.forEach(gameState::clearFlag)

        val actualChoice = choice.actualMemory.ifBlank { choice.text }
        val perceivedChoice = if (shouldDistortChoice(gameState, choice)) {
            selectPerceivedChoice(choice, actualChoice, gameState)
        } else {
            actualChoice
        }

        gameState.recordChoice(actualChoice, perceivedChoice)
        if (actualChoice != perceivedChoice) {
            gameState.markFlag("memoryFractured")
        }

        val falseMemory = maybeInjectFalseMemory(gameState, sceneId = choice.nextSceneId)
        return ChoiceMemoryResult(
            actualChoice = actualChoice,
            perceivedChoice = perceivedChoice,
            wasDistorted = actualChoice != perceivedChoice,
            falseMemoryInserted = falseMemory
        )
    }

    fun resolveScene(scene: Scene, gameState: GameState): ScenePresentation {
        val visitCount = gameState.recordSceneVisit(scene.id)
        if (scene.id.contains("mirror", ignoreCase = true) || scene.mirrorEchoes.isNotEmpty()) {
            gameState.markFlag("mirrorWitness")
        }
        maybeInjectFalseMemory(gameState, scene.id)

        val sections = mutableListOf<String>()
        sections += scene.text.trim()

        resolveRevisitText(scene, visitCount)?.let(sections::add)
        resolveConflictingDialogues(scene.conflictingDialogues, gameState, visitCount)?.let(sections::add)
        resolveAmbientNpcMemory(scene, gameState)?.let(sections::add)
        resolveMirrorText(scene, gameState)?.let(sections::add)
        resolveFalseMemoryLine(scene, gameState)?.let(sections::add)
        resolveInternalVoice(scene, gameState)?.let(sections::add)

        return ScenePresentation(
            speaker = scene.speaker,
            text = sections.filter { it.isNotBlank() }.joinToString("\n\n"),
            backgroundResId = scene.backgroundResId,
            characterResId = scene.characterResId
        )
    }

    fun resolveEnding(endings: List<Ending>, gameState: GameState): EndingPresentation {
        val ending = selectEnding(endings, gameState)
        return EndingPresentation(
            text = buildEndingText(ending, gameState),
            backgroundResId = ending.backgroundResId
        )
    }

    private fun shouldDistortChoice(gameState: GameState, choice: Choice): Boolean {
        val instability = gameState.memoryInstability
        var chance = 0.10 +
            (gameState.corruption * 0.03) +
            (instability * 0.02) -
            (gameState.memory * 0.015)

        if (choice.perceivedMemoryOptions.isNotEmpty()) {
            chance += 0.08
        }
        if (gameState.hasFlag("mirrorWitness")) {
            chance += 0.05
        }
        if (gameState.hasFlag("falseMemory")) {
            chance += 0.08
        }

        return random.nextDouble() < chance.coerceIn(0.08, 0.88)
    }

    private fun selectEnding(endings: List<Ending>, gameState: GameState): Ending {
        val statsMap = gameState.getStatsMap()
        val instability = statsMap["Instability"] ?: 0
        val distortion = statsMap["Distortion"] ?: 0

        if (instability >= 18 && distortion >= 4) {
            endingById(endings, "fractured_archive")?.let { return it }
        }
        if (gameState.hasFlag("falseMemory") && distortion >= 3) {
            endingById(endings, "borrowed_sin")?.let { return it }
        }

        selectedEndingId(gameState, statsMap)?.let { endingId ->
            endingById(endings, endingId)?.let { return it }
        }

        return endings.firstOrNull { it.condition.isMet(statsMap) } ?: endings.last()
    }

    private fun selectedEndingId(gameState: GameState, statsMap: Map<String, Int>): String? {
        val mercy = statsMap["Mercy"] ?: 0
        val ruin = statsMap["Ruin"] ?: 0

        return when {
            gameState.hasFlag("ending_hold") && mercy >= 2 && ruin <= 1 -> "true_escape"
            gameState.hasFlag("ending_release") && ruin >= 2 -> "silent_void"
            gameState.hasFlag("ending_accept") && mercy >= 1 -> "eternal_keeper"
            gameState.hasFlag("ending_mortal") && mercy >= 3 && ruin <= 1 -> "mortal_path"
            gameState.hasFlag("ending_mortal") || gameState.hasFlag("ending_wander") -> "unwritten_horizon"
            else -> inferEndingIdFromMemory(gameState, mercy, ruin)
        }
    }

    private fun inferEndingIdFromMemory(gameState: GameState, mercy: Int, ruin: Int): String? {
        val signals = buildList {
            addAll(gameState.actualChoices.takeLast(3))
            addAll(gameState.perceivedChoices.takeLast(3))
        }.joinToString(" ").lowercase()

        return when {
            "hold the vale" in signals || "stillness" in signals || "sealed the vale" in signals ->
                if (mercy >= 2 && ruin <= 1) "true_escape" else "unwritten_horizon"
            "release the vale" in signals || "break apart" in signals || "entropy" in signals ->
                if (ruin >= 2) "silent_void" else "unwritten_horizon"
            "take the watch" in signals || "aelric" in signals || "remember without rewriting" in signals ->
                if (mercy >= 1) "eternal_keeper" else "unwritten_horizon"
            "mortal dawn" in signals || "mortal life" in signals || "step into dawn" in signals ->
                if (mercy >= 3 && ruin <= 1) "mortal_path" else "unwritten_horizon"
            "unwritten" in signals || "wanderer" in signals || "walk away" in signals ->
                "unwritten_horizon"
            else -> null
        }
    }

    private fun endingById(endings: List<Ending>, id: String): Ending? {
        return endings.firstOrNull { it.id == id }
    }

    private fun selectPerceivedChoice(choice: Choice, actualChoice: String, gameState: GameState): String {
        if (choice.perceivedMemoryOptions.isNotEmpty()) {
            return choice.perceivedMemoryOptions.random(random)
        }

        val replacements = listOf(
            "saved" to "abandoned",
            "save" to "abandon",
            "spared" to "condemned",
            "release" to "bind",
            "released" to "bound",
            "remember" to "erase",
            "look" to "turn away",
            "fix" to "claim",
            "mercy" to "silence",
            "help" to "witnesses"
        )

        replacements.firstOrNull { (source, _) ->
            actualChoice.contains(source, ignoreCase = true)
        }?.let { (source, target) ->
            return actualChoice.replace(source, target, ignoreCase = true)
        }

        val wrappers = listOf(
            "the Vale insists that %s",
            "you could swear that %s, though it feels wrong",
            "every shard of glass remembers that %s"
        )
        return wrappers.random(random).format(actualChoice)
    }

    private fun maybeInjectFalseMemory(gameState: GameState, sceneId: String): String? {
        if (gameState.hasFlag("falseMemory")) return null
        if (gameState.actualChoices.size < 3) return null
        if (!sceneId.startsWith("ch3_") && !sceneId.startsWith("ch4_") && !sceneId.contains("final")) return null

        val instability = gameState.memoryInstability
        val chance = when {
            sceneId.contains("mirror", ignoreCase = true) -> 0.28 + instability * 0.03
            else -> 0.15 + instability * 0.025
        }.coerceIn(0.15, 0.82)

        if (instability < 6 && gameState.corruption < 6) return null
        if (random.nextDouble() >= chance) return null

        val falseMemory = falseMemoryPool
            .filterNot { gameState.perceivedChoices.contains(it) }
            .ifEmpty { falseMemoryPool }
            .random(random)

        gameState.falseMemoryText = falseMemory
        gameState.markFlag("falseMemory")
        gameState.perceivedChoices.add(falseMemory)
        return falseMemory
    }

    private fun resolveRevisitText(scene: Scene, visitCount: Int): String? {
        if (visitCount <= 1 || scene.revisitTextVariants.isEmpty()) return null
        val index = (visitCount - 2) % scene.revisitTextVariants.size
        return scene.revisitTextVariants[index]
    }

    private fun resolveConflictingDialogues(
        dialogues: List<ConflictingDialogue>,
        gameState: GameState,
        visitCount: Int
    ): String? {
        val activeDialogues = dialogues.filter { dialogue ->
            dialogue.requiredFlags.all(gameState::hasFlag) && visitCount >= dialogue.minimumVisitCount
        }
        if (activeDialogues.isEmpty()) return null

        val useDistortedVariant = shouldUseDistortedPerspective(gameState)
        return activeDialogues.joinToString("\n\n") { dialogue ->
            val template = if (useDistortedVariant) {
                dialogue.perceivedTemplate
            } else {
                dialogue.actualTemplate
            }
            fillTemplate(template, gameState)
        }
    }

    private fun resolveAmbientNpcMemory(scene: Scene, gameState: GameState): String? {
        val speaker = scene.speaker ?: return null
        if (speaker == "Narrator" || speaker == "Player" || scene.conflictingDialogues.isNotEmpty()) return null

        val contradiction = gameState.latestContradiction() ?: return null
        val templates = if (shouldUseDistortedPerspective(gameState)) {
            listOf(
                "\"Do not lie to me,\" $speaker says. \"The Vale remembers that {perceivedChoice}.\"",
                "$speaker watches you without blinking. \"You still carry it with you: {perceivedChoice}.\""
            )
        } else {
            listOf(
                "$speaker lowers their voice. \"Some part of you still remembers that {actualChoice}.\"",
                "\"I can see the truth trying to survive in you,\" $speaker murmurs. \"{actualChoice}.\""
            )
        }

        return if (contradiction.first == contradiction.second && random.nextDouble() > 0.35) {
            null
        } else {
            fillTemplate(templates.random(random), gameState)
        }
    }

    private fun resolveMirrorText(scene: Scene, gameState: GameState): String? {
        if (!scene.id.contains("mirror", ignoreCase = true) && scene.mirrorEchoes.isEmpty()) return null

        val lines = mutableListOf<String>()
        if (scene.mirrorEchoes.isNotEmpty()) {
            val useDistortedVariant = shouldUseDistortedPerspective(gameState)
            lines += scene.mirrorEchoes
                .filter { echo -> echo.requiredFlags.all(gameState::hasFlag) }
                .map { echo: MirrorEcho ->
                    val template = if (useDistortedVariant) {
                        echo.perceivedTemplate
                    } else {
                        echo.actualTemplate
                    }
                    fillTemplate(template, gameState)
                }
        } else {
            val actual = actualReference(gameState)
            val perceived = perceivedReference(gameState)
            lines += if (actual == perceived) {
                "The glass offers only one weary self, still shaped by how $actual."
            } else {
                "Two reflections drag across the glass. One remembers that $actual. The other swears that $perceived."
            }
        }

        gameState.falseMemoryText?.let { falseMemory ->
            lines += "A third reflection lingers behind them all, mouthing the impossible memory that $falseMemory."
        }

        return lines.filter { it.isNotBlank() }.joinToString("\n\n").ifBlank { null }
    }

    private fun resolveFalseMemoryLine(scene: Scene, gameState: GameState): String? {
        if (!gameState.hasFlag("falseMemory")) return null
        return scene.falseMemoryDialogue?.let { fillTemplate(it, gameState) }
    }

    private fun resolveInternalVoice(scene: Scene, gameState: GameState): String? {
        val stableVoice = scene.stableVoice
        val distortedVoice = scene.distortedVoice
        if (stableVoice == null && distortedVoice == null) return null

        val template = if (distortedVoice != null && shouldUseDistortedPerspective(gameState)) {
            distortedVoice
        } else {
            stableVoice ?: distortedVoice
        }

        return template?.let { fillTemplate(it, gameState) }
    }

    private fun shouldUseDistortedPerspective(gameState: GameState): Boolean {
        if (gameState.hasFlag("falseMemory")) return true
        if (gameState.corruption > gameState.memory) return true

        val instability = gameState.memoryInstability
        val contradictionBonus = if (gameState.latestContradiction() != null) 0.15 else 0.0
        val chance = (0.08 + instability * 0.03 + contradictionBonus).coerceIn(0.05, 0.85)
        return random.nextDouble() < chance
    }

    private fun buildEndingText(ending: Ending, gameState: GameState): String {
        val contradiction = gameState.latestContradiction()
        val actual = contradiction?.first ?: actualReference(gameState)
        val perceived = contradiction?.second ?: perceivedReference(gameState)

        val suffix = buildString {
            if (actual != perceived) {
                append(" The last surviving shard of truth insists that ")
                append(actual)
                append(", but the Vale crowns the version of you who believes that ")
                append(perceived)
                append(".")
            }
            gameState.falseMemoryText?.let { falseMemory ->
                append(" When the final witnesses speak, they all agree that ")
                append(falseMemory)
                append(", and only you feel the lie scratching beneath your skin.")
            }
        }

        return ending.text + suffix
    }

    private fun fillTemplate(template: String, gameState: GameState): String {
        val actual = actualReference(gameState)
        val perceived = perceivedReference(gameState)
        val firstActual = gameState.actualChoices.firstOrNull() ?: actual
        val firstPerceived = gameState.perceivedChoices.firstOrNull() ?: perceived
        val falseMemory = gameState.falseMemoryText ?: perceived
        val reflection = if (gameState.corruption >= gameState.humanity) {
            "a cracked sovereign wearing your smile"
        } else {
            "a tired stranger who still looks human"
        }

        return template
            .replace("{actualChoice}", actual)
            .replace("{perceivedChoice}", perceived)
            .replace("{firstActualChoice}", firstActual)
            .replace("{firstPerceivedChoice}", firstPerceived)
            .replace("{falseMemory}", falseMemory)
            .replace("{mirrorSelf}", reflection)
    }

    private fun actualReference(gameState: GameState): String {
        return gameState.latestContradiction()?.first
            ?: gameState.actualChoices.lastOrNull()
            ?: "you reached for a life you could not fully remember"
    }

    private fun perceivedReference(gameState: GameState): String {
        return gameState.latestContradiction()?.second
            ?: gameState.falseMemoryText
            ?: gameState.perceivedChoices.lastOrNull()
            ?: actualReference(gameState)
    }
}
