package strss.no.echoesoftheforgottenvale.logic

import kotlin.random.Random

class RealityInstabilityRenderer(
    private val random: Random = Random.Default
) {
    private val characterGlitches = mapOf(
        'a' to '@',
        'e' to '3',
        'i' to '!',
        'o' to '0',
        's' to '$',
        'l' to '|'
    )

    fun apply(text: String, gameState: GameState): String {
        val instability = gameState.memoryInstability
        if (instability <= 1) return text

        return text.split("\n").joinToString("\n") { line ->
            distortLine(line, instability)
        }
    }

    fun typingDelayMs(gameState: GameState): Long {
        return (40L + gameState.memoryInstability * 4L + if (gameState.hasFlag("falseMemory")) 8L else 0L)
            .coerceAtMost(125L)
    }

    fun additionalPause(nextCharacter: Char?, gameState: GameState): Long {
        val instability = gameState.memoryInstability
        if (nextCharacter == null) return 0L

        var extraDelay = 0L
        if (nextCharacter in listOf('.', ',', ';', '?', '!')) {
            extraDelay += instability * 4L
        }
        if (instability >= 5 && random.nextDouble() < (instability * 0.02).coerceAtMost(0.24)) {
            extraDelay += 90L + instability * 6L
        }

        return extraDelay
    }

    private fun distortLine(line: String, instability: Int): String {
        if (line.isBlank()) return line

        val skipChance = (instability * 0.008).coerceAtMost(0.12)
        val glitchChance = (instability * 0.015).coerceAtMost(0.18)
        val words = line.split(" ")

        val distortedWords = words.mapIndexedNotNull { index, word ->
            if (shouldSkipWord(word, index, words.lastIndex, skipChance)) {
                null
            } else if (random.nextDouble() < glitchChance) {
                glitchWord(word)
            } else {
                word
            }
        }

        return distortedWords.joinToString(" ").ifBlank { line }
    }

    private fun shouldSkipWord(word: String, index: Int, lastIndex: Int, skipChance: Double): Boolean {
        if (index == 0 || index == lastIndex) return false
        if (word.length <= 4) return false
        if (!word.any(Char::isLetter)) return false
        return random.nextDouble() < skipChance
    }

    private fun glitchWord(word: String): String {
        val candidateIndexes = word.indices.filter { word[it].isLetter() }
        if (candidateIndexes.isEmpty()) return word

        val selectedIndex = candidateIndexes.random(random)
        val character = word[selectedIndex]
        val replacement = characterGlitches[character.lowercaseChar()]
            ?: if (character.isUpperCase()) '#' else '?'

        val chars = word.toCharArray()
        chars[selectedIndex] = replacement
        return String(chars)
    }
}
