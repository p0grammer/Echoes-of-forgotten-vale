package strss.no.echoesoftheforgottenvale.logic

import strss.no.echoesoftheforgottenvale.R
import strss.no.echoesoftheforgottenvale.model.*

object SceneRepository {
    private fun prose(text: String): String = text.trimIndent()

    val scenes = mapOf(
        // --- CHAPTER 1: THE AWAKENING ---
        "start" to Scene(
            id = "start",
            speaker = "Narrator",
            text = "The air is thick with the scent of damp earth and old sins. You awaken in the heart of the Gnarled Wilds... alone. \n\nThe Aethelroot looms above, a giant heart of glass pulsing in the dark. At your feet, a mirror lies in pieces. \n\nIn one fragment, you see your eye. In another... a hand that isn't yours. There was someone else. Someone you cannot remember. The guilt is a cold weight in your chest.",
            backgroundResId = R.drawable.forest_1,
            characterResId = 0,
            stableVoice = "A steadier thought surfaces beneath the panic: \"Follow the pieces. They still belong to someone real.\"",
            distortedVoice = "A second voice curls through the silence: \"You know why the hand is gone. You were the one who let it slip.\"",
            choices = listOf(
                Choice(
                    "Face the broken reflection",
                    "ch1_mirror",
                    mapOf("Memory" to 1),
                    actualMemory = "you knelt before the broken reflection",
                    perceivedMemoryOptions = listOf(
                        "you stared into the shard until it learned how to accuse you",
                        "you chose the mirror over the pleading hand in the glass"
                    )
                ),
                Choice(
                    "Leave the fragments behind",
                    "ch1_outskirts",
                    mapOf("Humanity" to 1),
                    actualMemory = "you left the mirror fragments behind in the roots",
                    perceivedMemoryOptions = listOf(
                        "you abandoned the voice trapped in the mirror shards",
                        "you turned away before the glass could name the dead"
                    )
                )
            )
        ),
        "ch1_mirror" to Scene(
            id = "ch1_mirror",
            speaker = "Player",
            text = "You lean over the largest shard. The reflection doesn't mirror your movement immediately. It lingers. \n\nYou see your face—unfamiliar and scarred. Then, the glass ripples. A second shadow stands behind you in the frame—a blurred shape with reaching hands. \n\nYou spin around, but there is only the forest. Then, a man in black plate armor emerges from the trees. Why does his silence feel like an accusation?",
            backgroundResId = R.drawable.forest_1,
            characterResId = R.drawable.corrupted_player,
            stableVoice = "Your breath catches on a quieter truth: \"If the mirror is lying, then something else still remembers me correctly.\"",
            distortedVoice = "The reflection moves a fraction too soon. \"Look at {mirrorSelf},\" it mouths. \"Look at the one who remembers that {perceivedChoice}.\"",
            mirrorEchoes = listOf(
                MirrorEcho(
                    actualTemplate = "One shard shows {mirrorSelf} reaching back toward the moment when {actualChoice}.",
                    perceivedTemplate = "Another shard shows {mirrorSelf} smiling as though it witnessed how {perceivedChoice}."
                )
            ),
            choices = listOf(
                Choice(
                    "Confront the man in black",
                    "ch1_darian_intro",
                    mapOf("Corruption" to 1),
                    actualMemory = "you stepped toward the man in black instead of retreating",
                    perceivedMemoryOptions = listOf(
                        "you welcomed the accusation like an old ally",
                        "you walked toward the shadow that already knew your guilt"
                    )
                ),
                Choice(
                    "Try to remember who was with you",
                    "ch1_darian_intro",
                    mapOf("Memory" to 2),
                    actualMemory = "you reached for the missing companion in your memory",
                    perceivedMemoryOptions = listOf(
                        "you remembered pulling your hand away from the one behind you",
                        "you saw someone falling and chose to keep looking forward"
                    )
                )
            )
        ),
        "ch1_darian_intro" to Scene(
            id = "ch1_darian_intro",
            speaker = "Darian",
            text = "'Still staring at yourself, Keeper? Searching for a soul that isn't there?' \n\nDarian’s voice is ragged, like grinding stone. He looks... tired. Not just from battle, but from an eternity of waiting. \n\n'The Vale is a graveyard. You built the walls. You locked the doors. And then... you chose the ritual... over them. Do you even remember their name?'",
            backgroundResId = R.drawable.cave,
            characterResId = R.drawable.darian,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Darian studies you with reluctant recognition. \"There is still a shard in me that remembers that {actualChoice}.\"",
                    perceivedTemplate = "Darian's jaw hardens. \"Do not pretend innocence. I remember that {perceivedChoice}.\""
                )
            ),
            choices = listOf(
                Choice(
                    "Who are you?",
                    "ch1_darian_identity",
                    mapOf("Humanity" to 1),
                    actualMemory = "you asked the stranger in black to name himself"
                ),
                Choice(
                    "I remember you... Darian?",
                    "ch1_darian_memory",
                    mapOf("Memory" to 2),
                    actualMemory = "you recognized Darian through the fracture of memory",
                    perceivedMemoryOptions = listOf(
                        "you spoke Darian's name like a confession",
                        "you remembered Darian only after remembering his hatred"
                    )
                )
            )
        ),
        "ch1_darian_identity" to Scene(
            id = "ch1_darian_identity",
            speaker = "Darian",
            text = "'I am the one you left behind when you locked the Spire. I am the rot in the roots of your precious tree. \n\nThese Echoing Caverns have a way of making voices carry... listen to the ghosts, Guardian. They know you chose the power over the person whose face you've now forgotten.'",
            backgroundResId = R.drawable.cave,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Search for help", "ch1_darian_to_aelric", mapOf("Humanity" to 1))
            )
        ),
        "ch1_darian_to_aelric" to Scene(
            id = "ch1_darian_to_aelric",
            speaker = "Narrator",
            text = "Leaving Darian and the damp chill of the caverns behind, you scramble up a narrow rocky path. As you emerge from the underground, the scent of burning cedar catches on the wind. Tucked between ancient oaks, a small, weathered hut flickers with a warm, inviting light.",
            backgroundResId = R.drawable.forest_1,
            characterResId = 0,
            choices = listOf(
                Choice("Approach the hut", "ch1_seek_others", mapOf("Humanity" to 1))
            )
        ),
        "ch1_darian_memory" to Scene(
            id = "ch1_darian_memory",
            speaker = "Darian",
            text = "'You remember my name, but do you remember the blood? The way you let go of their hand to grasp the core? \n\nKeep wandering. The Watcher is waiting for his puppet at the Secluded Hearth. Let's see if his fire can warm a heart made of crystal and regret.'",
            backgroundResId = R.drawable.cave,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Go to the campfire", "ch1_darian_to_aelric", mapOf("Memory" to 1))
            )
        ),
        "ch1_outskirts" to Scene(
            id = "ch1_outskirts",
            speaker = "Narrator",
            text = "You step into the Forgotten Outskirts, where the trees are skeletal and the mist tastes of iron. A woman in tarnished silver armor stands amidst the debris of a fallen watchtower, her eyes glowing with a cold, spectral light.",
            backgroundResId = R.drawable.forest_2,
            characterResId = R.drawable.lyra,
            choices = listOf(
                Choice("Speak to the warrior", "ch1_lyra_intro", mapOf("Humanity" to 2))
            )
        ),
        "ch1_lyra_intro" to Scene(
            id = "ch1_lyra_intro",
            speaker = "Lyra",
            text = " 'Halt. The Forgotten Outskirts are dangerous for those without memories. I am Lyra, Commander of the Silver Wing. Or I was, before you let the light die and left us to rot in this gray waste.'",
            backgroundResId = R.drawable.forest_2,
            characterResId = R.drawable.lyra,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Lyra keeps her blade low for a heartbeat. \"Part of me remembers that {actualChoice}. It almost sounds merciful.\"",
                    perceivedTemplate = "Lyra's armor creaks as she leans closer. \"No. I remember that {perceivedChoice}, and I remember how cold it felt.\""
                )
            ),
            choices = listOf(
                Choice(
                    "What happened here?",
                    "ch1_lyra_lore",
                    mapOf("Memory" to 1),
                    actualMemory = "you asked Lyra what became of her dead"
                ),
                Choice(
                    "I'm looking for Aelric",
                    "ch1_lyra_to_aelric",
                    mapOf("Humanity" to 1),
                    actualMemory = "you asked Lyra to lead you toward Aelric",
                    perceivedMemoryOptions = listOf(
                        "you asked Lyra where the last witness was hiding",
                        "you treated Lyra like a path through the wreckage"
                    )
                )
            )
        ),
        "ch1_lyra_to_aelric" to Scene(
            id = "ch1_lyra_to_aelric",
            speaker = "Narrator",
            text = "Following Lyra's pointed finger, you trek through the skeletal forest. The fog begins to thin as you reach a small clearing. Ahead, a plume of smoke rises from a lonely hut, its windows glowing like amber eyes in the twilight.",
            backgroundResId = R.drawable.forest_2,
            characterResId = 0,
            choices = listOf(
                Choice("Enter the clearing", "ch1_seek_others", mapOf("Humanity" to 1))
            )
        ),
        "ch1_lyra_lore" to Scene(
            id = "ch1_lyra_lore",
            speaker = "Lyra",
            text = " 'Betrayal. The Keeper chose the tree over the people. Now we wander these Outskirts as hollow echoes. Go to Aelric at the Hearth; he still believes in your redemption. I only believe in the weight of my blade.'",
            backgroundResId = R.drawable.forest_2,
            characterResId = R.drawable.lyra,
            choices = listOf(
                Choice("Head to the campfire", "ch1_lyra_to_aelric", mapOf("Humanity" to 1))
            )
        ),

        // --- CHAPTER 2: THE HUB ---
        "ch1_seek_others" to Scene(
            id = "ch1_seek_others",
            speaker = "Aelric Voss",
            text = "Aelric stirs the embers at the Secluded Hearth. The small hut offers little warmth against the encroaching void. 'I see you've met my guests. Darian still reeks of spite, and Lyra... she still carries the weight of a world she couldn't save.'",
            backgroundResId = R.drawable.hut,
            characterResId = R.drawable.aelric,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Aelric does not look up from the fire. \"The coals still remember that {actualChoice}.\"",
                    perceivedTemplate = "Aelric turns one ember with the end of his staff. \"Strange. The hearth insists that {perceivedChoice}.\""
                )
            ),
            choices = listOf(
                Choice("What is my part in this?", "ch2_aelric_truth", mapOf("Humanity" to 2)),
                Choice("I need to see more", "ch2_paths", mapOf("Memory" to 1))
            )
        ),
        "ch2_aelric_truth" to Scene(
            id = "ch2_aelric_truth",
            speaker = "Aelric Voss",
            text = " 'You were the architect of all this, from the Hearth to the Spire. Seraphine warned you, Darian fought you, and Lyra died for you. Now, you must choose which echo to follow into the heart of the Vale.'",
            backgroundResId = R.drawable.hut,
            characterResId = R.drawable.aelric,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "He finally meets your eyes. \"I would swear that {actualChoice}, and yet the roots keep offering me kinder versions of you than I trust.\"",
                    perceivedTemplate = "He finally meets your eyes. \"The roots tell a different story. They say that {perceivedChoice}.\""
                )
            ),
            choices = listOf(
                Choice("Show me the paths", "ch2_paths", mapOf())
            )
        ),
        "ch2_paths" to Scene(
            id = "ch2_paths",
            speaker = "Aelric Voss",
            text = " 'We stand in the Ancestral Lodge, where choices become history. Seraphine waits at the Crystal Falls. Lyra guards the Shattered Battlefield. And Darian... he's already at the Spire. Where will you go?'",
            backgroundResId = R.drawable.elven_house,
            characterResId = R.drawable.aelric,
            choices = listOf(
                Choice("The Crystal Falls (Seraphine)", "ch3_river_start", mapOf("Memory" to 1)),
                Choice("The Shattered Battlefield (Lyra)", "ch3_battle_start", mapOf("Humanity" to 1)),
                Choice("The Spire Gates (Darian)", "ch3_spire_start", mapOf("Corruption" to 1))
            )
        ),

        // --- CHAPTER 3: THE DEEP PATHS ---

        // PATH 1: SERAPHINE (TRUTH)
        "ch3_river_start" to Scene(
            id = "ch3_river_start",
            speaker = "Seraphine",
            text = "A woman in flowing white robes stands before the Crystal Falls. The water here flows upward, defying the ruin. 'You've come for the truth? The river doesn't lie, but its memories are cold and sharp as ice.'",
            backgroundResId = R.drawable.waterfall,
            characterResId = R.drawable.seraphine,
            revisitTextVariants = listOf(
                "When you return, the falls hesitate before rising, as though even the water is unsure which version of you to reflect.",
                "This time the spray tastes faintly of iron, and for a heartbeat the pool reflects a funeral instead of a river."
            ),
            choices = listOf(
                Choice("I am ready to see", "ch3_seraphine_vision", mapOf("Memory" to 3)),
                Choice("Touch the Spirit Beacon (To Lyra)", "ch3_battle_start", mapOf("Humanity" to 2))
            )
        ),
        "ch3_seraphine_vision" to Scene(
            id = "ch3_seraphine_vision",
            speaker = "Seraphine",
            text = "She touches the lens to your forehead. The vision is a tidal wave. \n\nYou see the Obsidian City... and yourself. You are holding someone’s hand. They are looking at you with trust. \n\nThen, the ritual begins. You pull your hand away. They fall into the dark. Their face... it's a blur of white light. Why can't you see them?",
            backgroundResId = R.drawable.dark_city,
            characterResId = R.drawable.seraphine,
            choices = listOf(
                Choice("Reach into the memory", "ch3_false_hope", mapOf("Memory" to 2))
            )
        ),
        "ch3_false_hope" to Scene(
            id = "ch3_false_hope",
            speaker = "Narrator",
            text = "You claw at the edges of the vision. You try to force the light to clear. \n\nThe person’s face begins to form... soft eyes... a familiar smile... 'I'm right here,' they whisper. \n\nFor a second, you feel whole. Then, the image cracks. It shatters like a mirror hit by a stone. The light dies. You are alone in the cold spray of the Crystal Falls again.",
            backgroundResId = R.drawable.waterfall,
            characterResId = 0,
            stableVoice = "A fragile thought breaks through the spray: \"If grief still hurts, then I am not entirely gone.\"",
            distortedVoice = "Something inside you answers the whisper with hunger: \"They were always going to fall. You only chose first.\"",
            choices = listOf(
                Choice(
                    "I have committed a great sin",
                    "ch3_seraphine_repent",
                    mapOf("Humanity" to 3),
                    actualMemory = "you admitted your guilt beneath the Crystal Falls",
                    perceivedMemoryOptions = listOf(
                        "you confessed only because the river had already exposed you",
                        "you called it sin only after the memory refused to fade"
                    ),
                    flagsToSet = setOf("acceptedGuilt")
                ),
                Choice(
                    "It was the only way!",
                    "ch3_seraphine_guilt",
                    mapOf("Corruption" to 2),
                    actualMemory = "you defended the ritual as necessary",
                    perceivedMemoryOptions = listOf(
                        "you said their life was worth the power",
                        "you called the sacrifice inevitable and meant it"
                    ),
                    flagsToSet = setOf("justifiedRitual")
                )
            )
        ),
        "ch3_seraphine_guilt" to Scene(
            id = "ch3_seraphine_guilt",
            speaker = "Seraphine",
            text = " 'Pride. Even here in the Obsidian City's shadow, it's the only thing you brought back from the void. Go then. Darian is waiting to see if your heart is still stone.'",
            backgroundResId = R.drawable.dark_city,
            characterResId = R.drawable.seraphine,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Corruption" to 2))
            )
        ),
        "ch3_seraphine_repent" to Scene(
            id = "ch3_seraphine_repent",
            speaker = "Seraphine",
            text = " 'Sorrow is the first step toward humanity. Take this fragment of the Silver Wing. Let the mist of the Crystal Falls wash away some of your rot.'",
            backgroundResId = R.drawable.waterfall,
            characterResId = R.drawable.seraphine,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Humanity" to 5))
            )
        ),

        // PATH 2: LYRA (DUTY)
        "ch3_battle_start" to Scene(
            id = "ch3_battle_start",
            speaker = "Lyra",
            text = "The iron mud of the Shattered Battlefield clings to your boots. Lyra looks at you with hollow eyes. \n\n'My soldiers died for your lies. Can you hear them? They're still waiting for the safety you promised.' \n\nA faint voice rises from the fog. Soft. Kind. You've heard it before... in a dream? Or a life you threw away? It calls a name you almost recognize.",
            backgroundResId = R.drawable.shattered_battlefield,
            characterResId = R.drawable.lyra,
            revisitTextVariants = listOf(
                "When you step here again, one fallen helmet has shifted closer to your path, as though someone moved it while you were gone.",
                "The fog parts in a new pattern now, arranging the dead like they were still trying to march home."
            ),
            choices = listOf(
                Choice("Let me speak to them", "ch3_lyra_ghosts", mapOf("Humanity" to 3)),
                Choice("Touch the Spirit Beacon (To Seraphine)", "ch3_river_start", mapOf("Memory" to 2))
            )
        ),
        "ch3_lyra_ghosts" to Scene(
            id = "ch3_lyra_ghosts",
            speaker = "Lyra",
            text = "A thousand whispers rise from the mud of the Battlefield. They don't scream; they weep. \n\nOne voice is louder than the rest. It doesn't accuse you. It just sounds... lonely. Lyra bows her head. \n\n'They just wanted to go home. You gave them a prison of crystal and a sky of lead.'",
            backgroundResId = R.drawable.shattered_battlefield,
            characterResId = R.drawable.lyra,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Lyra closes her eyes. \"Even now I can still feel that {actualChoice}.\"",
                    perceivedTemplate = "Lyra's grip tightens. \"The dead keep repeating that {perceivedChoice}. They repeat it like a prayer.\""
                )
            ),
            falseMemoryDialogue = "One of the voices in the mud rises above the others. \"We remember the night when {falseMemory}, Keeper.\" Lyra does not question it.",
            choices = listOf(
                Choice(
                    "Release their souls (The Vale begins to tremble)",
                    "ch3_lyra_free",
                    mapOf("Humanity" to 5, "Memory" to 2),
                    actualMemory = "you released the soldiers' souls from the battlefield",
                    perceivedMemoryOptions = listOf(
                        "you let the dead dissolve because remembering them hurt too much",
                        "you opened the graves only after hearing them accuse you"
                    ),
                    flagsToSet = setOf("releasedSouls")
                ),
                Choice(
                    "Bind them to the earth (Preserve the Spire's strength)",
                    "ch3_lyra_price",
                    mapOf("Corruption" to 3),
                    actualMemory = "you bound the fallen to the earth for the Spire's sake",
                    perceivedMemoryOptions = listOf(
                        "you chained the dead to the mud and called it duty",
                        "you listened to the battlefield beg and chose the Spire anyway"
                    ),
                    flagsToSet = setOf("boundSouls")
                )
            )
        ),
        "ch3_lyra_free" to Scene(
            id = "ch3_lyra_free",
            speaker = "Lyra",
            text = "For a moment, the iron Battlefield glows with a soft, warm light. Lyra looks at you, and for the first time, the coldness in her eyes softens. 'Go. Aelric awaits at the Hearth.'",
            backgroundResId = R.drawable.shattered_battlefield,
            characterResId = R.drawable.lyra,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Humanity" to 5))
            )
        ),
        "ch3_lyra_price" to Scene(
            id = "ch3_lyra_price",
            speaker = "Lyra",
            text = "She draws her sword, the steel reflecting the grim Battlefield. 'Then you are already lost. Darian was right about you. You aren't the Guardian. You are the Void.'",
            backgroundResId = R.drawable.shattered_battlefield,
            characterResId = R.drawable.lyra,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Corruption" to 5))
            )
        ),

        // PATH 3: DARIAN (POWER)
        "ch3_spire_start" to Scene(
            id = "ch3_spire_start",
            speaker = "Darian",
            text = prose(
                """
                The path toward the Spire does not feel like a direction you consciously choose, but instead like an
                inevitable pull that has been waiting patiently for your arrival.

                The forest no longer reacts to your presence. Whatever lies ahead exists beyond its influence,
                beyond its memory, beyond its reach.

                Then you see it.
                The Spire rises impossibly into a sky that no longer behaves like a sky, its surface smooth yet
                fractured, rebuilt too many times to remain whole.

                "You always hesitate here," Darian says, calm and certain.

                He stands at the base of the Spire exactly where you knew he would be, as if this moment has already
                happened before.

                "This is where it mattered," he continues.
                "This is where you decided."

                You tell him that you do not remember.

                "I know," he says.
                "That doesn't change anything."
                """
            ),
            backgroundResId = R.drawable.castle_outside,
            characterResId = R.drawable.darian,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Darian lowers his sword by an inch. \"There was a time I believed that {actualChoice}.\"",
                    perceivedTemplate = "Darian laughs without humor. \"No. I remember that {perceivedChoice}. That is the Keeper I buried.\""
                )
            ),
            falseMemoryDialogue = "The shadow pooled at Darian's feet shivers. \"Even the stones speak of how {falseMemory},\" he says, and the certainty in his voice makes your blood turn cold.",
            choices = listOf(
                Choice(
                    "I've come to fix this",
                    "ch3_darian_fix",
                    mapOf("Humanity" to 2),
                    actualMemory = "you told Darian you came to mend what you broke",
                    perceivedMemoryOptions = listOf(
                        "you promised repair because you wanted the gate open",
                        "you lied to Darian about salvation to get closer to the core"
                    ),
                    flagsToSet = setOf("soughtRepair")
                ),
                Choice(
                    "I've come for the power",
                    "ch3_darian_power",
                    mapOf("Corruption" to 5),
                    actualMemory = "you admitted you still wanted the Spire's power",
                    perceivedMemoryOptions = listOf(
                        "you came to the Spire hungry for what it cost last time",
                        "you said the throne mattered more than the life behind you"
                    ),
                    flagsToSet = setOf("soughtPower")
                )
            )
        ),
        "ch3_darian_fix" to Scene(
            id = "ch3_darian_fix",
            speaker = "Darian",
            text = prose(
                """
                "You had a choice," Darian says.
                "You could let everything collapse, or you could take control."

                You answer before you can hide from it.
                "And I chose control."

                "Yes," he replies.
                "But the real choice was what you had to lose."

                Silence settles between you and the Spire.

                "You can choose again," he says.
                "You can try to bear what you did instead of preserving it forever.
                But if you want that right, you will have to pass through me first."
                """
            ),
            backgroundResId = R.drawable.castle_outside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Fight Darian", "ch3_darian_fight", mapOf("Memory" to 3, "Humanity" to 2))
            )
        ),
        "ch3_darian_power" to Scene(
            id = "ch3_darian_power",
            speaker = "Darian",
            text = prose(
                """
                "Then you understand at last," Darian says.

                There is no satisfaction in his voice. Only exhaustion.

                "You stopped the collapse, but you never fixed it.
                You preserved it.
                That was the real choice.
                Not salvation.
                Permanence."

                He steps aside just enough for the Sanctum to breathe around him.

                "If that is still the answer you can live with, go to the Core.
                Become the Spire.
                Hold everything together and lose yourself inside the structure that remains."
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Merge with the Core", "ch3_darian_ritual", mapOf("Corruption" to 5))
            )
        ),
        "ch3_darian_fight" to Scene(
            id = "ch3_darian_fight",
            speaker = "Narrator",
            text = prose(
                """
                The battle inside the Sanctum does not feel like violence.
                It feels like argument made physical.

                Darian is strong, but certainty is heavier than strength.
                Every strike forces the same question back into your hands.

                When you finally break through his guard and drive him to one knee, he looks up at you as if the
                answer has always been there waiting.

                "This is not about choosing what is right," he says through broken breath.
                "It is about choosing what you can live with."
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice(
                    "Show mercy",
                    "ch3_darian_mercy",
                    mapOf("Humanity" to 5),
                    actualMemory = "you spared Darian when he could no longer stand",
                    perceivedMemoryOptions = listOf(
                        "you made Darian watch you decide whether he deserved to live",
                        "you spared Darian only after enjoying his fear"
                    ),
                    flagsToSet = setOf("sparedDarian")
                ),
                Choice(
                    "End his suffering",
                    "ch3_darian_kill",
                    mapOf("Corruption" to 3),
                    actualMemory = "you killed Darian in the Sanctum",
                    perceivedMemoryOptions = listOf(
                        "you struck Darian down to silence the truth",
                        "you ended Darian before he could finish naming your sin"
                    ),
                    flagsToSet = setOf("killedDarian")
                )
            )
        ),
        "ch3_darian_mercy" to Scene(
            id = "ch3_darian_mercy",
            speaker = "Darian",
            text = prose(
                """
                He does not laugh when you spare him.
                That would make the moment simpler than it deserves.

                Instead he studies you with a tired, searching expression.

                "Then live with it," he says.
                "Not the myth of it.
                Not the system you built around it.
                The truth."
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Humanity" to 2))
            )
        ),
        "ch3_darian_kill" to Scene(
            id = "ch3_darian_kill",
            speaker = "Darian",
            text = prose(
                """
                "About time," he whispers.

                His body dissolves into shadow across the Sanctum floor.

                The silence that follows does not feel victorious.
                It feels administrative, as if the Spire has simply removed one more witness and continued
                functioning.
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = 0,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Corruption" to 5))
            )
        ),
        "ch3_darian_ritual" to Scene(
            id = "ch3_darian_ritual",
            speaker = "Narrator",
            text = prose(
                """
                You step into the Core and feel the decision harden around you.

                The light does not reject you.
                It remembers the shape you made in it the first time.

                Crystal climbs your skin like agreement.
                The Spire rises through you, not before you, binding your existence to the structure that preserves
                everything in stillness.
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Corruption" to 5))
            )
        ),

        // --- CHAPTER 4: THE MIRROR ---
        "ch4_decision" to Scene(
            id = "ch4_decision",
            speaker = "Aelric Voss",
            text = prose(
                """
                The return does not feel like movement through space or time, but instead like everything collapsing
                inward into a single point that has always existed, waiting for you to arrive.

                The Aethelroot pulses above, its crystalline branches reflecting light that now feels deliberate
                rather than distant, as if it is responding to your presence directly.

                The mirror stands before you, no longer shattered, no longer fragmented, but whole in a way that
                feels impossible after everything you have seen.

                "You came back," Aelric says from behind you, calm and steady.
                "Everyone does."

                You do not turn.
                The mirror demands your attention too completely.
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.aelric,
            conflictingDialogues = listOf(
                ConflictingDialogue(
                    actualTemplate = "Aelric's expression softens for the briefest moment. \"If I trust the oldest memory in these roots, then I trust that {actualChoice}.\"",
                    perceivedTemplate = "Aelric's eyes darken in the mirrorlight. \"The roots disagree. They insist that {perceivedChoice}.\""
                ),
                ConflictingDialogue(
                    actualTemplate = "\"The battlefield still trembles because {actualChoice},\" Aelric says, almost in awe.",
                    perceivedTemplate = "\"The battlefield still trembles because {perceivedChoice},\" Aelric says, as though the judgment were settled.",
                    requiredFlags = setOf("releasedSouls")
                ),
                ConflictingDialogue(
                    actualTemplate = "\"Darian's shadow lingered, but it did not own you entirely,\" Aelric murmurs.",
                    perceivedTemplate = "\"Darian's shadow followed you here whispering that {perceivedChoice},\" Aelric murmurs.",
                    requiredFlags = setOf("killedDarian")
                )
            ),
            falseMemoryDialogue = "Aelric touches the mirror's edge. \"The Vale will not release the night when {falseMemory},\" he says, speaking of it as though he stood there himself.",
            choices = listOf(
                Choice("Look into the mirror", "ch4_mirror_final", mapOf())
            )
        ),
        "ch4_mirror_final" to Scene(
            id = "ch4_mirror_final",
            speaker = "Player",
            text = prose(
                """
                You stand before the mirror, and for the first time it does not wait.

                It shows you immediately.
                Not crowned.
                Not broken.
                Not distorted.
                Still.
                Silent.
                Aware.

                Then something forms behind you within the reflection.
                A second figure.
                Blurred.
                Incomplete.
                Unremembered.

                "They were real," Aelric says quietly.
                "They trusted you."

                The mirror shifts again, showing not details, not sequences, but truth.
                You, choosing.
                Not hesitating.
                Not questioning.
                Deciding.

                The figure behind you reaches forward, trusting, believing, expecting.
                And you step away.

                "You can't change it," you say.
                "No," Aelric replies. "But you can choose what it means."

                The mirror darkens slightly, and now you feel it.
                Not one path.
                Many.
                Each one beginning from the same place.
                From you.
                """
            ),
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.player,
            stableVoice = "Your own voice returns in fragments: \"That is not all I was. There is still a version of me that reached back.\"",
            distortedVoice = "The mirror mouths the words before you can think them. \"You were always the one who {perceivedChoice}.\"",
            mirrorEchoes = listOf(
                MirrorEcho(
                    actualTemplate = "The truest reflection shows {mirrorSelf} flinching as it remembers that {actualChoice}.",
                    perceivedTemplate = "A darker reflection smiles with your teeth, certain that {perceivedChoice}."
                ),
                MirrorEcho(
                    actualTemplate = "Behind them stands the blurred companion, still waiting to see whether you will choose truth over comfort.",
                    perceivedTemplate = "Behind them stands the blurred companion, already fading into the version of history that hurts the most."
                )
            ),
            choices = listOf(
                Choice(
                    "Hold the Vale Together",
                    "final_fairytale",
                    mapOf(),
                    condition = Condition(
                        allOf = listOf(
                            Condition("Humanity", ">=", 12),
                            Condition("Memory", ">=", 12),
                            Condition("Corruption", "<=", 8),
                            Condition("Mercy", ">=", 2),
                            Condition("Ruin", "<=", 1)
                        )
                    ),
                    actualMemory = "you chose to hold the Vale together, no matter what it cost to keep it still",
                    perceivedMemoryOptions = listOf(
                        "you chose to embalm the world in hope so you would never have to lose it again",
                        "you called the cage salvation because Seraphine's light made it beautiful"
                    ),
                    flagsToSet = setOf("ending_hold"),
                    flagsToClear = setOf("ending_release", "ending_wander", "ending_accept", "ending_mortal")
                ),
                Choice(
                    "Release the Vale From Its Cage",
                    "final_doom",
                    mapOf(),
                    condition = Condition(
                        allOf = listOf(
                            Condition("Corruption", ">=", 10),
                            Condition("Humanity", "<=", 9),
                            Condition("Ruin", ">=", 2)
                        )
                    ),
                    actualMemory = "you chose to release the Vale, even if release arrived wearing ruin",
                    perceivedMemoryOptions = listOf(
                        "you mistook annihilation for honesty and stepped into it gladly",
                        "you decided that if the memory hurt, the world that held it deserved to die"
                    ),
                    flagsToSet = setOf("ending_release"),
                    flagsToClear = setOf("ending_hold", "ending_wander", "ending_accept", "ending_mortal")
                ),
                Choice(
                    "Become the Nameless Wanderer",
                    "final_medium",
                    mapOf(),
                    condition = Condition(
                        anyOf = listOf(
                            Condition("Mercy", ">=", 1),
                            Condition("Ruin", ">=", 1),
                            Condition("Distortion", ">=", 1)
                        )
                    ),
                    actualMemory = "you chose to leave the mirror behind and walk into what the Vale could not script",
                    perceivedMemoryOptions = listOf(
                        "you ran before judgment could become permanent",
                        "you chose exile because even your own reflection refused to absolve you"
                    ),
                    flagsToSet = setOf("ending_wander"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_accept", "ending_mortal")
                ),
                Choice(
                    "Accept Aelric's Vigil",
                    "final_keeper",
                    mapOf(),
                    condition = Condition(
                        allOf = listOf(
                            Condition("Memory", ">=", 8),
                            Condition("Humanity", ">=", 6),
                            Condition("Corruption", "<=", 12),
                            Condition("Mercy", ">=", 1)
                        )
                    ),
                    actualMemory = "you chose to remain with the Vale and witness it without lying to it again",
                    perceivedMemoryOptions = listOf(
                        "you took Aelric's place so your penance could look like wisdom",
                        "you chained yourself to the wound because you no longer trusted yourself to leave it"
                    ),
                    flagsToSet = setOf("ending_accept"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_wander", "ending_mortal")
                ),
                Choice(
                    "Walk Into Mortal Dawn",
                    "final_mortal",
                    mapOf(),
                    condition = Condition(
                        allOf = listOf(
                            Condition("Humanity", ">=", 9),
                            Condition("Corruption", "<=", 8),
                            Condition("Mercy", ">=", 3),
                            Condition("Ruin", "<=", 1)
                        )
                    ),
                    actualMemory = "you chose a mortal dawn beyond the Vale, carrying only what a human heart could bear",
                    perceivedMemoryOptions = listOf(
                        "you abandoned the dead to their own remembering and called it mercy",
                        "you fled into mortality because eternity had learned your real shape"
                    ),
                    flagsToSet = setOf("ending_mortal"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_wander", "ending_accept")
                )
            )
        ),
        
        // ENDING TRIGGERS
        "final_fairytale" to Scene(
            id = "final_fairytale",
            text = prose(
                """
                Seraphine steps into the reflection beside you, not smiling, not afraid.

                "If you do this," she says, "nothing truly heals. It only remains."

                The Aethelroot answers anyway.
                Light threads through the branches, into the city, into the broken distances between every ruined
                memory, stitching shape back into the construct.

                The dead are not restored.
                The wound is not erased.
                But the Vale holds.

                Hope survives here only by becoming architecture.
                """
            ),
            backgroundResId = R.drawable.waterfall,
            characterResId = R.drawable.seraphine,
            choices = listOf(
                Choice(
                    "Let the stillness stand",
                    "final_trigger",
                    actualMemory = "you sealed the Vale in stillness so its fragile hope would not collapse",
                    perceivedMemoryOptions = listOf(
                        "you chose preservation because a beautiful prison was easier to forgive",
                        "you froze the wound and called that mercy"
                    ),
                    flagsToSet = setOf("ending_hold"),
                    flagsToClear = setOf("ending_release", "ending_wander", "ending_accept", "ending_mortal")
                )
            )
        ),
        "final_doom" to Scene(
            id = "final_doom",
            text = prose(
                """
                Darian does not stop you.

                He only watches as your hand enters the living heart of the construct and opens it.

                The city trembles first.
                Then the roads.
                Then the carefully repeated skies.

                For a breathless instant everything in the Vale becomes honest.
                Not eternal.
                Not sacred.
                Made.
                Broken.
                Ready to end.

                The release feels like terror because terror and freedom are so often twins.
                """
            ),
            backgroundResId = R.drawable.dark_city,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice(
                    "Open your hand",
                    "final_trigger",
                    actualMemory = "you opened the Vale and let its false eternity break apart",
                    perceivedMemoryOptions = listOf(
                        "you mistook destruction for truth and would not let anyone stop you",
                        "you tore the world open just to prove you could survive the sound"
                    ),
                    flagsToSet = setOf("ending_release"),
                    flagsToClear = setOf("ending_hold", "ending_wander", "ending_accept", "ending_mortal")
                )
            )
        ),
        "final_medium" to Scene(
            id = "final_medium",
            text = prose(
                """
                The mirror keeps offering futures until you stop looking.

                You step sideways from prophecy, from judgment, from every role the Vale prepared for the person who
                made it.

                A path appears where none existed a moment before.
                It does not lead toward throne, root, or ruin.
                It leads outward.

                Behind you, the mirror continues remembering.
                Ahead of you, the world does not know your name yet.
                """
            ),
            backgroundResId = R.drawable.forest_1,
            characterResId = R.drawable.player,
            choices = listOf(
                Choice(
                    "Walk into the unwritten",
                    "final_trigger",
                    actualMemory = "you stepped outside the story the Vale kept trying to force upon you",
                    perceivedMemoryOptions = listOf(
                        "you slipped away before any truth could keep hold of you",
                        "you chose to become unknowable because being known had become unbearable"
                    ),
                    flagsToSet = setOf("ending_wander"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_accept", "ending_mortal")
                )
            )
        ),
        "final_keeper" to Scene(
            id = "final_keeper",
            text = prose(
                """
                Aelric offers you the staff, but what he is really offering is witness.

                "No more disguises," he says.
                "No more myths. Let it be what it is."

                You take the weight of the Vale without claiming ownership of it.
                Not ruler.
                Not executioner.
                Not savior.

                Only the one who remains long enough to remember honestly.
                """
            ),
            backgroundResId = R.drawable.hut,
            characterResId = R.drawable.aelric,
            choices = listOf(
                Choice(
                    "Take the watch",
                    "final_trigger",
                    actualMemory = "you accepted the long watch and chose to remember without rewriting",
                    perceivedMemoryOptions = listOf(
                        "you called your sentence wisdom and put on Aelric's face",
                        "you chose the vigil because guilt feels cleaner when it is ritualized"
                    ),
                    flagsToSet = setOf("ending_accept"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_wander", "ending_mortal")
                )
            )
        ),
        "final_mortal" to Scene(
            id = "final_mortal",
            text = prose(
                """
                The magic leaves you slowly, kindly.

                Not as punishment.
                Not as reward.
                Simply as an ending.

                The mirror dims.
                The root grows distant.
                The burden narrows until it can fit inside a single human chest.

                You will remember enough to ache and too little to possess.
                That, at last, feels survivable.
                """
            ),
            backgroundResId = R.drawable.forest_2,
            characterResId = R.drawable.player,
            choices = listOf(
                Choice(
                    "Step into dawn",
                    "final_trigger",
                    actualMemory = "you let the magic fall away and chose the fragile truth of a mortal life",
                    perceivedMemoryOptions = listOf(
                        "you abandoned eternity only after it stopped flattering you",
                        "you chose a smaller life because a larger one kept telling on you"
                    ),
                    flagsToSet = setOf("ending_mortal"),
                    flagsToClear = setOf("ending_hold", "ending_release", "ending_wander", "ending_accept")
                )
            )
        )
    )

    val endings = listOf(
        Ending(
            id = "fractured_archive",
            condition = Condition("Instability", ">=", 14),
            text = "THE ARCHIVE OF WRONG LIVES: The mirror refuses to settle. Every version of the Vale survives at once, and each one insists you chose it on purpose. Cities are preserved and shattered in the same breath. Lovers bless you while soldiers curse you. In the end, the only thing enthroned is contradiction itself.",
            backgroundResId = R.drawable.fractured_reality
        ),
        Ending(
            id = "borrowed_sin",
            condition = Condition("FalseMemory", ">=", 1),
            text = "THE BORROWED SIN: The Vale leaves you one final inheritance: an atrocity that never occurred, remembered more vividly than anything true. Long after the last mirror falls dark, strangers still retell the false night as if they stood inside it with you. The lie outlives the wound because the wound asked less of them.",
            backgroundResId = R.drawable.archive_sanctum
        ),
        Ending(
            id = "silent_void",
            condition = Condition(
                allOf = listOf(
                    Condition("Corruption", ">=", 12),
                    Condition("Ruin", ">=", 2)
                )
            ),
            text = "THE ONE WHO RELEASES: You tear the memory-kingdom open and the Vale unthreads around your hands. Darian calls it honesty. Seraphine calls it loss. Both are right. What remains is not a throne but an absence wide enough for freedom to finally enter.",
            backgroundResId = R.drawable.dark_city
        ),
        Ending(
            id = "true_escape",
            condition = Condition(
                allOf = listOf(
                    Condition("Memory", ">=", 12),
                    Condition("Mercy", ">=", 2),
                    Condition("Ruin", "<=", 1)
                )
            ),
            text = "THE ONE WHO HOLDS: You choose preservation over release, and Seraphine's light becomes the seal that keeps the Vale from collapsing into truth. The city endures. The dead remain dead. Hope survives, but only because you accept the burden of keeping the wound beautifully contained.",
            backgroundResId = R.drawable.waterfall
        ),
        Ending(
            id = "eternal_keeper",
            condition = Condition(
                allOf = listOf(
                    Condition("Memory", ">=", 10),
                    Condition("Mercy", ">=", 1)
                )
            ),
            text = "THE ONE WHO ACCEPTS: You inherit Aelric's long patience and refuse both denial and annihilation. The Vale remains, not as paradise and not as prison, but as a place where memory can be witnessed without being worshiped. Acceptance proves harsher than hope and gentler than destruction.",
            backgroundResId = R.drawable.shattered_battlefield
        ),
        Ending(
            id = "unwritten_horizon",
            condition = Condition(
                anyOf = listOf(
                    Condition("Distortion", ">=", 1),
                    Condition("Mercy", ">=", 1),
                    Condition("Ruin", ">=", 1)
                )
            ),
            text = "THE UNWRITTEN HORIZON: You leave the mirror to its own obsessions and walk into a future the Vale did not prepare. Nothing crowns you. Nothing absolves you. For the first time, that uncertainty feels like a gift rather than a punishment.",
            backgroundResId = R.drawable.forest_1
        ),
        Ending(
            id = "mortal_path",
            condition = Condition(
                allOf = listOf(
                    Condition("Humanity", ">=", 9),
                    Condition("Corruption", "<=", 8),
                    Condition("Mercy", ">=", 3),
                    Condition("Ruin", "<=", 1)
                )
            ),
            text = "THE MORTAL DAWN: You step beyond the reach of the construct and let the last of its magic leave your body. You do not become innocent. You become finite. Under a real sky, grief finally weighs what a human heart can carry, and that smallness feels holy.",
            backgroundResId = R.drawable.forest_2
        )
    )

    fun getScene(id: String): Scene? = scenes[id]
}
