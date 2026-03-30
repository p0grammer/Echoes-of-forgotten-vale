package strss.no.echoesoftheforgottenvale.visual

import android.view.View
import android.view.ViewGroup

class AnimationController {

    fun animateNarrativeLayer(vararg views: View) {
        views.forEachIndexed { index, view ->
            view.animate().cancel()
            view.alpha = 0f
            view.translationY = 18f + (index * 6f)
            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 45).toLong())
                .setDuration(420)
                .start()
        }
    }

    fun animateChoiceList(container: ViewGroup) {
        container.alpha = 1f
        container.children().forEachIndexed { index, child ->
            child.animate().cancel()
            child.alpha = 0f
            child.translationY = 22f
            child.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay((index * 65).toLong())
                .setDuration(340)
                .start()
        }
    }

    fun crossfadeScene(scrim: View, onMidpoint: () -> Unit) {
        scrim.animate().cancel()
        scrim.alpha = 0f
        scrim.visibility = View.VISIBLE
        scrim.animate()
            .alpha(0.82f)
            .setDuration(220)
            .withEndAction {
                onMidpoint()
                scrim.animate()
                    .alpha(0f)
                    .setDuration(360)
                    .start()
            }
            .start()
    }

    fun animateOverlayMessage(view: View, onEnd: () -> Unit) {
        view.animate().cancel()
        view.alpha = 0f
        view.translationY = -18f
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(280)
            .withEndAction {
                view.animate()
                    .alpha(0f)
                    .translationY(18f)
                    .setStartDelay(1500)
                    .setDuration(360)
                    .withEndAction(onEnd)
                    .start()
            }
            .start()
    }

    private fun ViewGroup.children(): List<View> {
        return (0 until childCount).map { getChildAt(it) }
    }
}
