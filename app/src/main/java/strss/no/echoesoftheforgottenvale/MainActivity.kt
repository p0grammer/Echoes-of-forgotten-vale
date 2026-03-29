package strss.no.echoesoftheforgottenvale

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.Color
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
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import strss.no.echoesoftheforgottenvale.databinding.ActivityMainBinding
import strss.no.echoesoftheforgottenvale.logic.GameState
import strss.no.echoesoftheforgottenvale.logic.SaveManager
import strss.no.echoesoftheforgottenvale.logic.SceneManager
import strss.no.echoesoftheforgottenvale.logic.SceneRepository
import strss.no.echoesoftheforgottenvale.model.Choice

class MainActivity : AppCompatActivity() {

    private val TAG = "GameDebug"
    private lateinit var binding: ActivityMainBinding
    private lateinit var saveManager: SaveManager
    private val sceneManager = SceneManager()
    private val gameState = GameState()

    private val handler = Handler(Looper.getMainLooper())
    private var isTyping = false
    private var isEnding = false
    private var fullText = ""
    private var typeIndex = 0
    private val typingDelay = 40L 

    private var mediaPlayer: MediaPlayer? = null
    private var currentVolume = 0.7f
    private var currentMusicResId: Int = 0
    
    // Quick UI states
    private var isAutoMode = false
    private var isQuickPanelVisible = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        saveManager = SaveManager(this)
        
        setupMainMenu()
        setupQuickUI()
        setupCharacterAnimations()
    }

    private fun setupCharacterAnimations() {
        val breathingAnimator = ValueAnimator.ofFloat(1.0f, 1.02f)
        breathingAnimator.duration = 3000
        breathingAnimator.repeatMode = ValueAnimator.REVERSE
        breathingAnimator.repeatCount = ValueAnimator.INFINITE
        breathingAnimator.interpolator = AccelerateDecelerateInterpolator()
        breathingAnimator.addUpdateListener { animation ->
            val scale = animation.animatedValue as Float
            binding.ivCharacter.scaleX = scale
            binding.ivCharacter.scaleY = scale
            binding.ivCharacterAura.scaleX = scale * 1.05f
            binding.ivCharacterAura.scaleY = scale * 1.05f
        }
        breathingAnimator.start()

        val auraAnimator = ValueAnimator.ofFloat(0.3f, 0.6f)
        auraAnimator.duration = 2000
        auraAnimator.repeatMode = ValueAnimator.REVERSE
        auraAnimator.repeatCount = ValueAnimator.INFINITE
        auraAnimator.addUpdateListener { animation ->
            if (binding.ivCharacter.visibility == View.VISIBLE) {
                binding.ivCharacterAura.alpha = animation.animatedValue as Float
            }
        }
        auraAnimator.start()
    }

    private fun updateAuraColor(speaker: String?) {
        val color = when (speaker) {
            "Aethelroot" -> Color.argb(100, 255, 215, 0) 
            "Memory Fragment", "Whispers" -> Color.argb(100, 0, 255, 204) 
            "Darian", "Voices" -> Color.argb(100, 255, 68, 68) 
            else -> Color.argb(50, 255, 255, 255) 
        }
        binding.ivCharacterAura.setColorFilter(color)
    }

    private fun setupQuickUI() {
        binding.btnHistory.setOnClickListener { showHistory() }
        binding.btnSave.setOnClickListener { showSaveSlots(isSaving = true) }
        binding.btnSkip.setOnClickListener { if (isTyping) skipTypewriter() }
        binding.btnAuto.setOnClickListener {
            isAutoMode = !isAutoMode
            binding.btnAuto.alpha = if (isAutoMode) 1.0f else 0.5f
        }
        binding.btnSettingsInGame.setOnClickListener { showSettingsDialog() }
        binding.btnHide.setOnClickListener { toggleInterface() }
        binding.btnBackToGame.setOnClickListener { binding.historyOverlay.visibility = View.GONE }
        
        binding.btnEndToMenu.setOnClickListener { resetToMainMenu() }
        binding.btnEndLoadSave.setOnClickListener { 
            binding.creditsOverlay.visibility = View.GONE
            showSaveSlots(isSaving = false) 
        }
        
        binding.mainLayout.setOnClickListener {
            if (!isQuickPanelVisible) toggleInterface()
            else if (isTyping) skipTypewriter()
        }
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
                setPadding(0, 0, 0, 48)
            }
            if (speaker != null) {
                val speakerText = TextView(this).apply {
                    this.text = speaker
                    setTextColor(Color.WHITE)
                    textSize = 14f
                    alpha = 0.6f
                    setTypeface(null, android.graphics.Typeface.BOLD)
                }
                historyView.addView(speakerText)
            }
            val dialogueText = TextView(this).apply {
                this.text = text
                setTextColor(Color.WHITE)
                textSize = 18f
                setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f,  resources.displayMetrics), 1.0f)
            }
            historyView.addView(dialogueText)
            binding.llHistoryContent.addView(historyView)
        }
        binding.historyOverlay.visibility = View.VISIBLE
    }

    private fun setupMainMenu() {
        binding.menuContainer.visibility = View.VISIBLE
        binding.gameContainer.visibility = View.GONE
        binding.creditsOverlay.visibility = View.GONE
        binding.menuContainer.alpha = 1f
        binding.llMenuButtons.visibility = View.VISIBLE
        binding.llSaveSlots.visibility = View.GONE
        isEnding = false
        
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
        binding.gameContainer.visibility = View.GONE
        binding.creditsOverlay.visibility = View.GONE
        setupMainMenu()
    }

    private fun showSaveSlots(isSaving: Boolean, isNewGame: Boolean = false) {
        binding.llMenuButtons.visibility = View.GONE
        binding.llSaveSlots.visibility = View.VISIBLE
        binding.tvSlotTitle.text = if (isSaving) "SAVE GAME" else if (isNewGame) "NEW GAME" else "LOAD MEMORY"
        
        val slots = listOf(binding.btnSlot1, binding.btnSlot2, binding.btnSlot3, binding.btnSlot4, binding.btnSlot5)
        slots.forEachIndexed { index, button ->
            val slotNum = index + 1
            val hasSave = saveManager.hasSave(slotNum)
            button.text = if (hasSave) "Slot $slotNum - ${saveManager.loadCurrentSceneId(slotNum)}" else "Slot $slotNum - Empty"
            
            button.setOnClickListener {
                if (isSaving) {
                    saveManager.saveGame(slotNum, sceneManager.getCurrentScene()?.id ?: "start", gameState)
                    showStatGainAnimation("GAME SAVED TO SLOT $slotNum")
                    binding.llSaveSlots.visibility = View.GONE
                } else {
                    if (isNewGame) {
                        saveManager.deleteSave(slotNum)
                        gameState.humanity = 0
                        gameState.corruption = 0
                        gameState.memory = 0
                        gameState.dialogueHistory.clear()
                        startGame("start", slotNum)
                    } else if (hasSave) {
                        saveManager.loadGameState(gameState, slotNum)
                        startGame(saveManager.loadCurrentSceneId(slotNum), slotNum)
                    }
                }
            }
        }
    }

    private fun startGame(sceneId: String, slot: Int) {
        binding.llSaveSlots.visibility = View.GONE
        binding.menuContainer.animate()
            .alpha(0f)
            .setDuration(800)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    binding.menuContainer.visibility = View.GONE
                    binding.gameContainer.visibility = View.VISIBLE
                    binding.gameContainer.alpha = 0f
                    binding.gameContainer.animate().alpha(1f).setDuration(800).start()
                    
                    updateMusicForScene(sceneId)
                    updateStatsHud(animate = false)
                    loadScene(sceneId)
                }
            })
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
    }

    private fun loadScene(sceneId: String) {
        if (sceneId == "final_trigger") {
            showEnding()
            return
        }
        updateMusicForScene(sceneId)
        sceneManager.goToScene(sceneId)
        renderUI()
    }

    private fun renderUI() {
        val scene = sceneManager.getCurrentScene() ?: return
        binding.tvSpeakerName.text = scene.speaker ?: ""
        binding.tvSpeakerName.visibility = if (scene.speaker != null) View.VISIBLE else View.GONE
        
        if (scene.backgroundResId != 0) {
            binding.ivBackground.setImageResource(scene.backgroundResId)
        }
        
        if (scene.characterResId != 0) {
            binding.ivCharacter.setImageResource(scene.characterResId)
            binding.ivCharacterAura.setImageResource(scene.characterResId) 
            binding.ivCharacter.visibility = View.VISIBLE
            binding.ivCharacterAura.visibility = View.VISIBLE
            updateAuraColor(scene.speaker)
        } else {
            binding.ivCharacter.visibility = View.GONE
            binding.ivCharacterAura.visibility = View.GONE
        }

        binding.llChoices.visibility = View.GONE
        gameState.addToHistory(scene.speaker, scene.text)
        startTypewriter(scene.text)
    }

    private fun startTypewriter(text: String) {
        handler.removeCallbacksAndMessages(null)
        fullText = text
        typeIndex = 0
        isTyping = true
        binding.tvSceneText.text = ""
        
        val runnable = object : Runnable {
            override fun run() {
                if (typeIndex <= fullText.length) {
                    binding.tvSceneText.text = fullText.substring(0, typeIndex)
                    typeIndex++
                    handler.postDelayed(this, typingDelay)
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
        
        binding.llChoices.removeAllViews()
        val statsMap = gameState.getStatsMap()
        
        scene.choices.forEach { choice ->
            if (choice.condition?.isMet(statsMap) ?: true) {
                binding.llChoices.addView(createChoiceButton(choice))
            }
        }
        binding.llChoices.visibility = View.VISIBLE
    }

    private fun showEnding() {
        isEnding = true
        binding.llChoices.removeAllViews()
        binding.llChoices.visibility = View.GONE
        
        val statsMap = gameState.getStatsMap()
        val ending = SceneRepository.endings.find { it.condition.isMet(statsMap) } 
            ?: SceneRepository.endings.last()

        binding.tvSpeakerName.visibility = View.GONE
        binding.ivCharacter.visibility = View.GONE
        binding.ivCharacterAura.visibility = View.GONE
        binding.ivBackground.setImageResource(ending.backgroundResId)
        
        binding.tvSceneText.text = ""
        fullText = ending.text
        typeIndex = 0
        isTyping = true
        
        val runnable = object : Runnable {
            override fun run() {
                if (typeIndex <= fullText.length) {
                    binding.tvSceneText.text = fullText.substring(0, typeIndex)
                    typeIndex++
                    handler.postDelayed(this, typingDelay)
                } else {
                    isTyping = false
                    handler.postDelayed({ startCreditScroll(ending.text) }, 3000)
                }
            }
        }
        handler.post(runnable)
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
            setTextColor(Color.WHITE)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            gravity = Gravity.CENTER
            setBackgroundResource(R.drawable.narrative_box_bg) 
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
        binding.overlayContainer.addView(textView)
        textView.animate()
            .alpha(1f)
            .translationY(20f)
            .setDuration(1000)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    textView.animate()
                        .alpha(0f)
                        .setStartDelay(1500)
                        .setDuration(800)
                        .setListener(object : AnimatorListenerAdapter() {
                            override fun onAnimationEnd(animation: Animator) {
                                binding.overlayContainer.removeView(textView)
                            }
                        })
                }
            })
    }

    private fun createStyledButton(buttonText: String, onClick: () -> Unit): Button {
        return Button(this).apply {
            text = buttonText
            background = ContextCompat.getDrawable(context, R.drawable.medieval_button_bg)
            setTextColor(Color.WHITE)
            isAllCaps = false
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setPadding(24, 8, 24, 8)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 8)
            }
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
