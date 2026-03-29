package strss.no.echoesoftheforgottenvale.model

import strss.no.echoesoftheforgottenvale.R

object StoryData {
    val scenes = mapOf(
        "start" to Scene(
            id = "start",
            text = "You awaken in the center of the Forgotten Vale. A colossal tree of crystal memory, the Aethelroot, looms above you. You remember nothing, yet you feel a heavy weight of responsibility.",
            backgroundResId = android.R.drawable.ic_menu_gallery,
            characterResId = android.R.drawable.ic_menu_report_image,
            choices = listOf(
                Choice("Approach the Aethelroot", "approach_tree", mapOf("Memory" to 1)),
                Choice("Look for others", "search_others", mapOf("Humanity" to 1))
            )
        ),
        "approach_tree" to Scene(
            id = "approach_tree",
            text = "The Aethelroot pulses with a dim, rhythmic light. As you touch its bark, fragments of a past life flicker in your mind. You were... a Keeper?",
            backgroundResId = android.R.drawable.ic_menu_gallery,
            characterResId = android.R.drawable.ic_menu_report_image,
            choices = listOf(
                Choice("Try to remember more", "final_trigger", mapOf("Memory" to 2)),
                Choice("Pull away in fear", "final_trigger", mapOf("Corruption" to 1))
            )
        ),
        "search_others" to Scene(
            id = "search_others",
            text = "You wander the misty perimeter. In the distance, you see a figure sitting by a small fire. It is Aelric Voss, the Watcher.",
            backgroundResId = android.R.drawable.ic_menu_gallery,
            characterResId = android.R.drawable.ic_menu_report_image,
            choices = listOf(
                Choice("Speak to him", "final_trigger", mapOf("Humanity" to 2)),
                Choice("Observe from the shadows", "final_trigger", mapOf("Corruption" to 1))
            )
        )
    )

    val endings = listOf(
        Ending(
            id = "silent_void",
            condition = Condition("Corruption", ">=", 2),
            text = "THE SILENT VOID: You destroy Aethelroot. Reality collapses. Freedom through annihilation.",
            backgroundResId = android.R.drawable.ic_menu_gallery
        ),
        Ending(
            id = "true_escape",
            condition = Condition("Memory", ">=", 2),
            text = "THE TRUE ESCAPE: You integrate memory, emotion, and truth. You transcend the system.",
            backgroundResId = android.R.drawable.ic_menu_gallery
        ),
        Ending(
            id = "eternal_keeper",
            condition = Condition("Humanity", ">=", 2),
            text = "THE ETERNAL KEEPER: You preserve the system. The cycle continues. Stability over freedom.",
            backgroundResId = android.R.drawable.ic_menu_gallery
        )
    )
}
