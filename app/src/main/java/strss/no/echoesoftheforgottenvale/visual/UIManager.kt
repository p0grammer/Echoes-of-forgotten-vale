package strss.no.echoesoftheforgottenvale.visual

import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import strss.no.echoesoftheforgottenvale.R
import strss.no.echoesoftheforgottenvale.databinding.ActivityMainBinding

object VisualPalette {
    const val BACKGROUND = 0xFF0D0F1A.toInt()
    const val ENVIRONMENT = 0xFF2A2F3A.toInt()
    const val TRUTH = 0xFFEAEAF0.toInt()
    const val HOPE = 0xFFFFB86C.toInt()
}

class UIManager {

    fun apply(binding: ActivityMainBinding) {
        binding.mainLayout.setBackgroundColor(VisualPalette.BACKGROUND)
        binding.tvTitle.setShadowLayer(28f, 0f, 14f, 0xA6000000.toInt())
        binding.tvSceneText.setShadowLayer(16f, 0f, 6f, 0xA6000000.toInt())
        binding.tvSpeakerName.setShadowLayer(10f, 0f, 3f, 0x66000000)
        binding.tvEndingLore.setShadowLayer(16f, 0f, 6f, 0x99000000.toInt())

        listOf(
            binding.btnStartGame,
            binding.btnLoadGame,
            binding.btnSettings,
            binding.btnQuit,
            binding.btnCancelSlots,
            binding.btnBackToGame
        ).forEach {
            it.setTextColor(VisualPalette.TRUTH)
        }

        listOf(
            binding.btnHistory,
            binding.btnSave,
            binding.btnSkip,
            binding.btnAuto,
            binding.btnSettingsInGame,
            binding.btnHide
        ).forEach {
            it.alpha = 0.88f
        }
    }

    fun styleSpeaker(textView: TextView, speaker: String?) {
        textView.setTextColor(accentForSpeaker(speaker))
    }

    fun styleChoiceButton(button: Button) {
        button.background = ContextCompat.getDrawable(button.context, R.drawable.btn_choice_bg)
        button.setTextColor(VisualPalette.TRUTH)
        button.typeface = Typeface.create(button.typeface, Typeface.NORMAL)
        button.stateListAnimator = null
        button.elevation = 0f
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.foreground = null
        }
    }

    fun styleHistoryEntry(speakerText: TextView?, dialogueText: TextView) {
        speakerText?.apply {
            setTextColor(accentForSpeaker(text?.toString()))
            alpha = 0.92f
        }
        dialogueText.setTextColor(VisualPalette.TRUTH)
    }

    fun styleOverlayMessage(textView: TextView) {
        textView.background = ContextCompat.getDrawable(textView.context, R.drawable.narrative_box_bg)
        textView.setTextColor(VisualPalette.TRUTH)
    }

    fun styleDialog(dialog: Dialog) {
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    fun accentForSpeaker(speaker: String?): Int {
        return when (speaker) {
            "Seraphine", "Aethelroot" -> VisualPalette.HOPE
            else -> VisualPalette.TRUTH
        }
    }
}
