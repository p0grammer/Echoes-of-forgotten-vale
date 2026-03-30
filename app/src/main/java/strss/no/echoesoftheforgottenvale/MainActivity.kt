package strss.no.echoesoftheforgottenvale

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import strss.no.echoesoftheforgottenvale.databinding.ActivityMainBinding
import strss.no.echoesoftheforgottenvale.logic.GameState
import strss.no.echoesoftheforgottenvale.logic.MemoryDistortionEngine
import strss.no.echoesoftheforgottenvale.logic.RealityInstabilityRenderer
import strss.no.echoesoftheforgottenvale.logic.SaveManager
import strss.no.echoesoftheforgottenvale.logic.SaveSlotSummary
import strss.no.echoesoftheforgottenvale.logic.SceneManager
import strss.no.echoesoftheforgottenvale.logic.SceneRepository
import strss.no.echoesoftheforgottenvale.model.Choice
import strss.no.echoesoftheforgottenvale.visual.AnimationController
import strss.no.echoesoftheforgottenvale.visual.AssetManager
import strss.no.echoesoftheforgottenvale.visual.UIManager
import strss.no.echoesoftheforgottenvale.visual.VisualPalette
import java.text.DateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private enum class SlotMode {
        SAVE,
        LOAD,
        NEW_GAME
    }

    private val TAG = "GameDebug"
    private lateinit var binding: ActivityMainBinding
    private lateinit var saveManager: SaveManager
    private val sceneManager = SceneManager()
    private val gameState = GameState()
    private val memoryDistortionEngine = MemoryDistortionEngine()
    private val realityInstabilityRenderer = RealityInstabilityRenderer()
    private lateinit var visualAssetManager: AssetManager
    private val uiManager = UIManager()
    private val animationController = AnimationController()

    private val handler = Handler(Looper.getMainLooper())
    private var isTyping = false
    private var isEnding = false
    private var fullText = ""
    private var typeIndex = 0
    private var currentTypingDelay = 40L

    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume = 0.7f
    private var currentMusicResId: Int = 0
    private var activeSaveSlot: Int? = null
    
    // Quick UI states
    private var isAutoMode = false
    private var isQuickPanelVisible = true

    private fun handleNarrativeTap() {
        if (!isQuickPanelVisible) {
            toggleInterface()
        } else if (isTyping) {
            skipTypewriter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)
        setupVisualSystems()
        setupMainMenu()
        setupQuickUI()
    }

    private fun setupVisualSystems() {
        visualAssetManager = AssetManager(this)
        visualAssetManager.preload(
            R.drawable.forgotten_outskirts,
            R.drawable.aethelroot_core,
            R.drawable.archive_sanctum,
            R.drawable.fractured_reality,
            R.drawable.shattered_battlefield,
            R.drawable.forest_1,
            R.drawable.forest_2,
            R.drawable.waterfall,
            R.drawable.dark_city,
            R.drawable.castle_inside,
            R.drawable.cave,
            R.drawable.hut,
            R.drawable.player,
            R.drawable.corrupted_player,
            R.drawable.aelric,
            R.drawable.seraphine,
            R.drawable.lyra,
            R.drawable.darian
        )
        binding.menuVisualRenderer.bind(visualAssetManager)
        binding.visualRenderer.bind(visualAssetManager)
        binding.menuVisualRenderer.setScene(
            R.drawable.forgotten_outskirts,
            0,
            null,
            "menu"
        )
        binding.transitionScrim.isClickable = false
        binding.transitionScrim.isFocusable = false
        binding.overlayContainer.isClickable = false
        binding.overlayContainer.isFocusable = false
        uiManager.apply(binding)
    }

    private fun setupQuickUI() {
        binding.btnHistory.setOnClickListener { showHistory() }
        binding.btnSave.setOnClickListener {
            val currentSlot = activeSaveSlot
            if (currentSlot != null && binding.gameContainer.visibility == View.VISIBLE) {
                performSave(currentSlot)
            } else {
                showSaveSlots(isSaving = true)
            }
        }
        binding.btnSave.setOnLongClickListener {
            showSaveSlots(isSaving = true)
            true
        }
        binding.btnSkip.setOnClickListener { if (isTyping) skipTypewriter() }
        binding.btnAuto.setOnClickListener {
            isAutoMode = !isAutoMode
            binding.btnAuto.alpha = if (isAutoMode) 1.0f else 0.5f
            if (isAutoMode && isTyping) {
                skipTypewriter()
            }
        }
        binding.btnSettingsInGame.setOnClickListener { showSettingsDialog() }
        binding.btnHide.setOnClickListener { toggleInterface() }
        binding.btnBackToGame.setOnClickListener { binding.historyOverlay.visibility = View.GONE }
        
        binding.btnEndToMenu.setOnClickListener { resetToMainMenu() }
        binding.btnEndLoadSave.setOnClickListener { showSaveSlots(isSaving = false) }

        binding.mainLayout.setOnClickListener(null)
        binding.visualRenderer.setOnClickListener { handleNarrativeTap() }
        binding.vTextOverlay.setOnClickListener { handleNarrativeTap() }
        binding.tvSceneText.setOnClickListener { handleNarrativeTap() }
        binding.tvSpeakerName.setOnClickListener { handleNarrativeTap() }
    }

    private fun toggleInterface() {
        isQuickPanelVisible = !isQuickPanelVisible
        val visibility = if (isQuickPanelVisible) View.VISIBLE else View.GONE
        binding.vTextOverlay.visibility = visibility
        binding.tvSpeakerName.visibility = if (isQuickPanelVisible && binding.tvSpeakerName.text.isNotEmpty()) View.VISIBLE else View.GONE
        binding.tvSceneText.visibility = visibility
        binding.svChoices.visibility = visibility
        binding.quickPanel.visibility = visibility
        binding.llStatsHud.visibility = visibility
    }

    private fun showHistory() {
        binding.llHistoryContent.removeAllViews()
        gameState.dialogueHistory.forEach { (speaker, text) ->
            val historyView = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(0, 0, 0, 36)
            }
            var speakerText: TextView? = null
            if (speaker != null) {
                speakerText = TextView(this).apply {
                    this.text = speaker
                    setTextColor(uiManager.accentForSpeaker(speaker))
                    textSize = 13f
                    alpha = 0.88f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                historyView.addView(speakerText)
            }
            val dialogueText = TextView(this).apply {
                this.text = text
                setTextColor(VisualPalette.TRUTH)
                textSize = 17f
                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,  resources.displayMetrics), 1.0f)
            }
            uiManager.styleHistoryEntry(speakerText, dialogueText)
            historyView.addView(dialogueText)
            binding.llHistoryContent.addView(historyView)
        }
        binding.historyOverlay.visibility = View.VISIBLE
        animationController.animateNarrativeLayer(
            binding.tvHistoryHeader,
            binding.llHistoryContent,
            binding.btnBackToGame
        )
    }

    private fun setupMainMenu() {
        binding.menuContainer.visibility = View.VISIBLE
        binding.gameContainer.visibility = View.GONE
        binding.creditsOverlay.visibility = View.GONE
        binding.menuContainer.alpha = 1f
        binding.llMenuButtons.visibility = View.VISIBLE
        binding.llSaveSlots.visibility = View.GONE
        isEnding = false
        binding.menuVisualRenderer.setScene(
            R.drawable.forgotten_outskirts,
            0,
            null,
            "menu"
        )
        animationController.animateNarrativeLayer(binding.tvTitle, binding.llMenuButtons)
        
        updateMusicForScene("menu")

        binding.btnStartGame.setOnClickListener {
            showSaveSlots(isSaving = false, isNewGame = true)
        }

        binding.btnLoadGame.setOnClickListener {
            showSaveSlots(isSaving = false, isNewGame = false)
        }

        binding.btnSettings.setOnClickListener { showSettingsDialog() }
        binding.btnQuit.setOnClickListener { finish() }
        
        binding.btnCancelSlots.setOnClickListener {
            binding.llSaveSlots.visibility = View.GONE
            binding.llMenuButtons.visibility = View.VISIBLE
        }
    }

    private fun resetToMainMenu() {
        activeSaveSlot = null
        binding.gameContainer.visibility = View.GONE
        binding.creditsOverlay.visibility = View.GONE
        setupMainMenu()
    }

    private fun showSaveSlots(isSaving: Boolean, isNewGame: Boolean = false) {
        val mode = when {
            isSaving -> SlotMode.SAVE
            isNewGame -> SlotMode.NEW_GAME
            else -> SlotMode.LOAD
        }
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_save_slots, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        val titleView = dialogView.findViewById<TextView>(R.id.tvSlotDialogTitle)
        val subtitleView = dialogView.findViewById<TextView>(R.id.tvSlotDialogSubtitle)
        val cancelView = dialogView.findViewById<TextView>(R.id.btnDialogCancel)
        val buttons = listOf(
            dialogView.findViewById<Button>(R.id.btnDialogSlot1),
            dialogView.findViewById<Button>(R.id.btnDialogSlot2),
            dialogView.findViewById<Button>(R.id.btnDialogSlot3),
            dialogView.findViewById<Button>(R.id.btnDialogSlot4),
            dialogView.findViewById<Button>(R.id.btnDialogSlot5)
        )

        titleView.text = when (mode) {
            SlotMode.SAVE -> "SAVE GAME"
            SlotMode.LOAD -> "LOAD MEMORY"
            SlotMode.NEW_GAME -> "NEW GAME"
        }
        subtitleView.text = when (mode) {
            SlotMode.SAVE -> "Choose a slot for this run. Long-press SAVE in game if you want a different slot."
            SlotMode.LOAD -> "Load a recorded run and resume from its saved scene and state."
            SlotMode.NEW_GAME -> "Choose where the new journey should live. Existing data in that slot will be replaced."
        }

        buttons.forEachIndexed { index, button ->
            val slotNum = index + 1
            val summary = saveManager.getSlotSummary(slotNum)
            button.text = formatSlotLabel(summary)
            button.isEnabled = mode != SlotMode.LOAD || summary.exists
            button.alpha = if (button.isEnabled) 1.0f else 0.45f
            button.setOnClickListener {
                dialog.dismiss()
                when (mode) {
                    SlotMode.SAVE -> performSave(slotNum)
                    SlotMode.LOAD -> loadFromSlot(slotNum)
                    SlotMode.NEW_GAME -> {
                        saveManager.deleteSave(slotNum)
                        gameState.reset()
                        startGame("start", slotNum)
                    }
                }
            }
        }

        cancelView.setOnClickListener { dialog.dismiss() }

        dialog.show()
        uiManager.styleDialog(dialog)

        if (mode == SlotMode.LOAD && buttons.none { it.isEnabled }) {
            Toast.makeText(this, "No save files found yet.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun performSave(slot: Int) {
        val sceneId = sceneManager.getCurrentScene()?.id ?: "start"
        saveManager.saveGame(slot, sceneId, gameState)
        activeSaveSlot = slot
        if (binding.gameContainer.visibility == View.VISIBLE) {
            showStatGainAnimation("GAME SAVED TO SLOT $slot")
        } else {
            Toast.makeText(this, "Saved to slot $slot", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadFromSlot(slot: Int) {
        val sceneId = saveManager.loadGameState(gameState, slot)
        if (sceneId == null) {
            Toast.makeText(this, "That slot is empty.", Toast.LENGTH_SHORT).show()
            return
        }
        startGame(sceneId, slot, restoreSavedState = true)
    }

    private fun formatSlotLabel(summary: SaveSlotSummary): String {
        if (!summary.exists || summary.sceneId == null) {
            return "Slot ${summary.slot} - Empty"
        }
        val sceneName = summary.sceneId
            .replace('_', ' ')
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        val savedAt = summary.savedAtMillis?.let {
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it))
        } ?: "Unknown time"
        return "Slot ${summary.slot} - $sceneName\n$savedAt"
    }

    private fun startGame(sceneId: String, slot: Int, restoreSavedState: Boolean = false) {
        isEnding = false
        activeSaveSlot = slot
        binding.llSaveSlots.visibility = View.GONE
        val enterGame = {
            binding.menuContainer.animate().setListener(null)
            binding.menuContainer.alpha = 1f
            binding.menuContainer.visibility = View.GONE
            binding.creditsOverlay.visibility = View.GONE
            binding.gameContainer.visibility = View.VISIBLE

            if (!restoreSavedState) {
                gameState.dialogueHistory.clear()
            }

            updateMusicForScene(sceneId)
            updateStatsHud(animate = false)
            loadScene(sceneId, animate = false)
        }

        if (binding.menuContainer.visibility == View.VISIBLE) {
            binding.menuContainer.animate()
                .alpha(0f)
                .setDuration(800)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.gameContainer.alpha = 0f
                        enterGame()
                        binding.gameContainer.animate().alpha(1f).setDuration(800).start()
                    }
                })
        } else {
            binding.gameContainer.alpha = 1f
            enterGame()
        }
    }

    private fun updateMusicForScene(sceneId: String) {
        val targetMusicResId = when {
            sceneId == "menu" -> R.raw.medieval_bg_1
            sceneId == "start" || sceneId.startsWith("ch1_") -> R.raw.medieval_bg_1
            sceneId.startsWith("ch2_") -> R.raw.medieval_bg_2
            sceneId.startsWith("ch3_river_") -> R.raw.medieval_bg_3
            sceneId.startsWith("ch3_battle_") -> R.raw.medieval_bg_4
            sceneId.startsWith("ch3_spire_") -> R.raw.medieval_bg_5
            sceneId.startsWith("ch4_") -> R.raw.medieval_bg_1
            else -> R.raw.medieval_bg_1
        }

        if (targetMusicResId != currentMusicResId) {
            currentMusicResId = targetMusicResId
            startBackgroundMusic(targetMusicResId)
        }
    }

    private fun startBackgroundMusic(resId: Int) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
            mediaPlayer = MediaPlayer.create(this, resId).apply {
                isLooping = true
                setVolume(currentVolume, currentVolume)
                start()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error starting music: ${e.message}")
        }
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        val sbVolume = dialogView.findViewById<SeekBar>(R.id.sbVolume)
        val cbMusic = dialogView.findViewById<CheckBox>(R.id.cbMusicToggle)
        val btnDelete = dialogView.findViewById<Button>(R.id.btnDeleteSave)
        val btnContinue = dialogView.findViewById<Button>(R.id.btnContinue)
        val btnExit = dialogView.findViewById<Button>(R.id.btnExit)

        sbVolume.progress = (currentVolume * 100).toInt()
        cbMusic.isChecked = mediaPlayer?.isPlaying ?: false

        sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                currentVolume = progress / 100f
                mediaPlayer?.setVolume(currentVolume, currentVolume)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        cbMusic.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (mediaPlayer == null) startBackgroundMusic(currentMusicResId) else mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }
        
        btnDelete.setOnClickListener {
            for (i in 1..5) saveManager.deleteSave(i)
            showStatGainAnimation("ALL SAVES DELETED")
            dialog.dismiss()
            resetToMainMenu()
        }

        btnContinue.setOnClickListener { dialog.dismiss() }
        btnExit.setOnClickListener { 
            dialog.dismiss()
            resetToMainMenu()
        }

        dialog.show()
        uiManager.styleDialog(dialog)
    }

    private fun loadScene(sceneId: String, animate: Boolean = true) {
        if (sceneId == "final_trigger") {
            showEnding()
            return
        }
        isEnding = false
        val performSceneLoad = {
            updateMusicForScene(sceneId)
            sceneManager.goToScene(sceneId)
            renderUI()
        }
        if (animate && binding.gameContainer.visibility == View.VISIBLE) {
            animationController.crossfadeScene(binding.transitionScrim, performSceneLoad)
        } else {
            performSceneLoad()
        }
    }

    private fun renderUI() {
        val scene = sceneManager.getCurrentScene() ?: return
        val presentation = memoryDistortionEngine.resolveScene(scene, gameState)

        binding.tvSpeakerName.text = presentation.speaker ?: ""
        binding.tvSpeakerName.visibility = if (presentation.speaker != null) View.VISIBLE else View.GONE
        uiManager.styleSpeaker(binding.tvSpeakerName, presentation.speaker)
        binding.visualRenderer.setScene(
            presentation.backgroundResId,
            presentation.characterResId,
            presentation.speaker,
            scene.id
        )

        binding.svChoices.visibility = View.GONE
        gameState.addToHistory(presentation.speaker, presentation.text)
        animationController.animateNarrativeLayer(binding.llStatsHud, binding.quickPanel, binding.vTextOverlay)
        startTypewriter(presentation.text)
    }

    private fun startTypewriter(text: String) {
        handler.removeCallbacksAndMessages(null)
        fullText = realityInstabilityRenderer.apply(text, gameState)
        typeIndex = 0
        isTyping = true
        currentTypingDelay = realityInstabilityRenderer.typingDelayMs(gameState).let { baseDelay ->
            if (isAutoMode) minOf(baseDelay, 12L) else baseDelay
        }
        binding.tvSceneText.text = ""

        val runnable = object : Runnable {
            override fun run() {
                if (typeIndex <= fullText.length) {
                    binding.tvSceneText.text = fullText.substring(0, typeIndex)
                    typeIndex++
                    val nextCharacter = fullText.getOrNull(typeIndex)
                    val extraPause = realityInstabilityRenderer.additionalPause(nextCharacter, gameState)
                    handler.postDelayed(this, currentTypingDelay + extraPause)
                } else {
                    onTypewriterFinished()
                }
            }
        }
        handler.post(runnable)
    }

    private fun skipTypewriter() {
        handler.removeCallbacksAndMessages(null)
        binding.tvSceneText.text = fullText
        onTypewriterFinished()
    }

    private fun onTypewriterFinished() {
        isTyping = false
        if (isEnding) return // Don't show choices if we are in the ending sequence
        
        val scene = sceneManager.getCurrentScene() ?: return
        
        binding.svChoices.animate().cancel()
        binding.llChoices.removeAllViews()
        val statsMap = gameState.getStatsMap()
        
        scene.choices.forEach { choice ->
            if (choice.condition?.isMet(statsMap) ?: true) {
                binding.llChoices.addView(createChoiceButton(choice))
            }
        }
        if (binding.llChoices.childCount > 0) {
            binding.svChoices.alpha = 1f
            binding.svChoices.translationY = 0f
            binding.svChoices.isEnabled = true
            binding.svChoices.isClickable = true
            binding.svChoices.visibility = View.VISIBLE
            binding.svChoices.requestLayout()
            binding.svChoices.post { binding.svChoices.fullScroll(View.FOCUS_UP) }
            animationController.animateChoiceList(binding.llChoices)
        } else {
            binding.svChoices.visibility = View.GONE
        }
    }

    private fun showEnding() {
        isEnding = true
        binding.llChoices.removeAllViews()
        binding.svChoices.visibility = View.GONE

        val ending = memoryDistortionEngine.resolveEnding(SceneRepository.endings, gameState)
        animationController.crossfadeScene(binding.transitionScrim) {
            binding.tvSpeakerName.visibility = View.GONE
            binding.visualRenderer.setScene(ending.backgroundResId, 0, null, "ending_${ending.backgroundResId}")

            binding.tvSceneText.text = ""
            fullText = realityInstabilityRenderer.apply(ending.text, gameState)
            typeIndex = 0
            isTyping = true
            currentTypingDelay = realityInstabilityRenderer.typingDelayMs(gameState)

            val runnable = object : Runnable {
                override fun run() {
                    if (typeIndex <= fullText.length) {
                        binding.tvSceneText.text = fullText.substring(0, typeIndex)
                        typeIndex++
                        val nextCharacter = fullText.getOrNull(typeIndex)
                        val extraPause = realityInstabilityRenderer.additionalPause(nextCharacter, gameState)
                        handler.postDelayed(this, currentTypingDelay + extraPause)
                    } else {
                        isTyping = false
                        handler.postDelayed({ startCreditScroll(ending.text) }, 3000)
                    }
                }
            }
            handler.post(runnable)
        }
    }

    private fun startCreditScroll(loreText: String) {
        binding.gameContainer.visibility = View.GONE
        binding.creditsOverlay.visibility = View.VISIBLE
        binding.llPostEndingMenu.visibility = View.INVISIBLE
        binding.tvEndingLore.text = loreText
        
        // Generate and set full credits
        val fullCredits = generateFullCredits()
        binding.tvFullCredits.text = fullCredits
        
        // Reset scroll position and start animation after a delay to ensure layout
        binding.svCredits.post {
            binding.svCredits.scrollTo(0, 0)
            
            // Wait for next layout pass to get accurate height
            binding.llCreditsContent.postDelayed({
                val scrollContentHeight = binding.llCreditsContent.height
                val scrollViewHeight = binding.svCredits.height
                
                // Animation logic: Scroll from top to bottom
                val animator = ValueAnimator.ofInt(0, scrollContentHeight)
                animator.duration = 60000 // 60 seconds
                animator.interpolator = LinearInterpolator()
                animator.addUpdateListener { animation ->
                    binding.svCredits.scrollTo(0, animation.animatedValue as Int)
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.llPostEndingMenu.visibility = View.VISIBLE
                        binding.llPostEndingMenu.alpha = 0f
                        binding.llPostEndingMenu.animate().alpha(1f).setDuration(2000).start()
                    }
                })
                animator.start()
            }, 500)
        }
    }

    private fun generateFullCredits(): String {
        val sb = StringBuilder()
        sb.append("CAST & CREW\n\n")
        sb.append("DIRECTED BY: NO STRSS\n")
        sb.append("WRITTEN BY: NO STRSS\n")
        sb.append("PROGRAMMED BY: NO STRSS\n")
        sb.append("ART DIRECTION: NO STRSS\n")
        sb.append("SOUND DESIGN: NO STRSS\n\n")
        
        sb.append("VOICE CAST\n\n")
        sb.append("The Player: Yourself\n")
        sb.append("Aelric Voss: Echo of Wisdom\n")
        sb.append("Seraphine: The Light's Grace\n")
        sb.append("Lyra: The Fallen Duty\n")
        sb.append("Darian: The Shadowed Vengeance\n\n")
        
        sb.append("ARTIFICIAL INTELLIGENCE ASSISTANCE\n\n")
        sb.append("Narrative Generation Tools\n")
        sb.append("Code Synthesis & Debugging\n")
        sb.append("Procedural Asset Generation\n\n")
        
        sb.append("SPECIAL THANKS\n\n")
        sb.append("To my family for believing in me.\n")
        sb.append("To my friends for testing the endless builds.\n")
        sb.append("To the community for the inspiration.\n\n")
        
        sb.append("THE CHRONICLES OF THE VALE\n\n")
        
        val loreSentences = listOf(
            "The Aethelroot stands as a testament to the hubris of the ancient kings.",
            "In the silence of the night, one can still hear the whispers of the Silver City.",
            "The crystal shards carry the weight of a thousand forgotten lives.",
            "Time is but a mirror in the Vale, reflecting only what we choose to see.",
            "The Spire was never a prison; it was a sanctuary for the dying light.",
            "Every step taken on the Shattered Battlefield is a step through history.",
            "Seraphine's lens reveals the truth that the river tries to wash away.",
            "Darian's armor is not made of steel, but of the shadows of his own regrets.",
            "Lyra's sword remains sharp, even as her purpose becomes a ghost.",
            "Aelric Voss stirs the fire, waiting for a savior who may never come."
        )
        
        for (i in 1..200) {
            sb.append(loreSentences.random()).append(" ")
            if (i % 3 == 0) sb.append("\n\n")
        }
        
        sb.append("\n\nPROMOTING THE FUTURE OF CREATIVITY\n")
        sb.append("ECHOES OF THE FORGOTTEN VALE\n")
        sb.append("A JOURNEY THROUGH MEMORY AND TIME\n")
        sb.append("© 2024 NO STRSS\n")
        sb.append("MADE WITH PASSION AND AI\n")
        
        return sb.toString()
    }

    private fun createChoiceButton(choice: Choice): Button {
        return createStyledButton(choice.text) {
            if (choice.statChanges.isNotEmpty()) {
                val stats = choice.statChanges.entries.joinToString(", ") { "${it.key} +${it.value}" }
                showStatGainAnimation(stats)
                gameState.applyStatChanges(choice.statChanges)
                updateStatsHud(animate = true)
            } else {
                gameState.applyStatChanges(choice.statChanges)
            }
            val memoryResult = memoryDistortionEngine.recordChoice(gameState, choice)
            if (memoryResult.wasDistorted) {
                showStatGainAnimation("MEMORY DISTORTED")
            }
            if (memoryResult.falseMemoryInserted != null) {
                showStatGainAnimation("A FALSE MEMORY TAKES ROOT")
            }
            loadScene(choice.nextSceneId)
        }
    }

    private fun updateStatsHud(animate: Boolean) {
        val stats = gameState.getStatsMap()
        updateStatProgressBar(binding.pbMemory, stats["Memory"] ?: 0, animate)
        updateStatProgressBar(binding.pbHumanity, stats["Humanity"] ?: 0, animate)
        updateStatProgressBar(binding.pbCorruption, stats["Corruption"] ?: 0, animate)
    }

    private fun updateStatProgressBar(progressBar: android.widget.ProgressBar, newValue: Int, animate: Boolean) {
        if (!animate) {
            progressBar.progress = newValue
            return
        }
        val animator = ValueAnimator.ofInt(progressBar.progress, newValue)
        animator.duration = 1000
        animator.addUpdateListener { animation ->
            progressBar.progress = animation.animatedValue as Int
        }
        animator.start()
    }

    private fun showStatGainAnimation(message: String) {
        val textView = TextView(this).apply {
            text = message
            setTextColor(VisualPalette.TRUTH)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            gravity = Gravity.CENTER
            setPadding(24, 12, 24, 12)
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                topMargin = 100
            }
            alpha = 0f
        }
        uiManager.styleOverlayMessage(textView)
        binding.overlayContainer.addView(textView)
        animationController.animateOverlayMessage(textView) {
            binding.overlayContainer.removeView(textView)
        }
    }

    private fun createStyledButton(buttonText: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = buttonText
            background = ContextCompat.getDrawable(context, R.drawable.btn_choice_bg)
            setTextColor(VisualPalette.TRUTH)
            isAllCaps = false
            isClickable = true
            isFocusable = true
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setPadding(24, 14, 24, 14)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 10)
            }
            uiManager.styleChoiceButton(this)
            setOnClickListener { onClick() }
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
