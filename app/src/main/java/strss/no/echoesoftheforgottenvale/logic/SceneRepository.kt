package strss.no.echoesoftheforgottenvale.logic

import strss.no.echoesoftheforgottenvale.R
import strss.no.echoesoftheforgottenvale.model.*

object SceneRepository {
    val scenes = mapOf(
        // --- CHAPTER 1: THE AWAKENING ---
        "start" to Scene(
            id = "start",
            speaker = "Narrator",
            text = "The air is thick with the scent of damp earth and old sins. You awaken in the heart of the Gnarled Wilds... alone. \n\nThe Aethelroot looms above, a giant heart of glass pulsing in the dark. At your feet, a mirror lies in pieces. \n\nIn one fragment, you see your eye. In another... a hand that isn't yours. There was someone else. Someone you cannot remember. The guilt is a cold weight in your chest.",
            backgroundResId = R.drawable.forest_1,
            characterResId = 0,
            choices = listOf(
                Choice("Face the broken reflection", "ch1_mirror", mapOf("Memory" to 1)),
                Choice("Leave the fragments behind", "ch1_outskirts", mapOf("Humanity" to 1))
            )
        ),
        "ch1_mirror" to Scene(
            id = "ch1_mirror",
            speaker = "Player",
            text = "You lean over the largest shard. The reflection doesn't mirror your movement immediately. It lingers. \n\nYou see your face—unfamiliar and scarred. Then, the glass ripples. A second shadow stands behind you in the frame—a blurred shape with reaching hands. \n\nYou spin around, but there is only the forest. Then, a man in black plate armor emerges from the trees. Why does his silence feel like an accusation?",
            backgroundResId = R.drawable.forest_1,
            characterResId = R.drawable.corrupted_player,
            choices = listOf(
                Choice("Confront the man in black", "ch1_darian_intro", mapOf("Corruption" to 1)),
                Choice("Try to remember who was with you", "ch1_darian_intro", mapOf("Memory" to 2))
            )
        ),
        "ch1_darian_intro" to Scene(
            id = "ch1_darian_intro",
            speaker = "Darian",
            text = "'Still staring at yourself, Keeper? Searching for a soul that isn't there?' \n\nDarian’s voice is ragged, like grinding stone. He looks... tired. Not just from battle, but from an eternity of waiting. \n\n'The Vale is a graveyard. You built the walls. You locked the doors. And then... you chose the ritual... over them. Do you even remember their name?'",
            backgroundResId = R.drawable.cave,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Who are you?", "ch1_darian_identity", mapOf("Humanity" to 1)),
                Choice("I remember you... Darian?", "ch1_darian_memory", mapOf("Memory" to 2))
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
            choices = listOf(
                Choice("What happened here?", "ch1_lyra_lore", mapOf("Memory" to 1)),
                Choice("I'm looking for Aelric", "ch1_lyra_to_aelric", mapOf("Humanity" to 1))
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
            choices = listOf(
                Choice("I have committed a great sin", "ch3_seraphine_repent", mapOf("Humanity" to 3)),
                Choice("It was the only way!", "ch3_seraphine_guilt", mapOf("Corruption" to 2))
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
            choices = listOf(
                Choice("Release their souls (The Vale begins to tremble)", "ch3_lyra_free", mapOf("Humanity" to 5, "Memory" to 2)),
                Choice("Bind them to the earth (Preserve the Spire's strength)", "ch3_lyra_price", mapOf("Corruption" to 3))
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
            text = "Darian stands at the Spire’s Threshold, his armor bleeding shadow onto the white stone. \n\n'Finally. You've come to reclaim your throne? Or to finally die like a man in the heart of your folly?' \n\nHe grips his sword. 'The one you failed... they're watching from the dark. Waiting for you to finish what you started.'",
            backgroundResId = R.drawable.castle_outside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("I've come to fix this", "ch3_darian_fix", mapOf("Humanity" to 2)),
                Choice("I've come for the power", "ch3_darian_power", mapOf("Corruption" to 5))
            )
        ),
        "ch3_darian_fix" to Scene(
            id = "ch3_darian_fix",
            speaker = "Darian",
            text = "'Fix it? You think you can fix a broken world after you shattered their life for a handful of starlight? \n\nYou'll have to walk through me and into the Spire’s Maw first.'",
            backgroundResId = R.drawable.castle_outside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Fight Darian", "ch3_darian_fight", mapOf("Memory" to 3, "Humanity" to 2))
            )
        ),
        "ch3_darian_power" to Scene(
            id = "ch3_darian_power",
            speaker = "Darian",
            text = "He laughs, a hollow sound that echoes through the Sanctum of Sorrows. \n\n'That's the Keeper I know! The one who trades love for longevity. Come then, take the core. Become the monster they always feared you were.'",
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Merge with the Core", "ch3_darian_ritual", mapOf("Corruption" to 5))
            )
        ),
        "ch3_darian_fight" to Scene(
            id = "ch3_darian_fight",
            speaker = "Narrator",
            text = "The battle is a blur of starlight and shadow within the Sanctum of Sorrows. Darian is strong, but he is tired. \n\n'Why... couldn't you... just save them?' he wheezes. You strike a final blow, and his helmet shatters against the cold floor.",
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Show mercy", "ch3_darian_mercy", mapOf("Humanity" to 5)),
                Choice("End his suffering", "ch3_darian_kill", mapOf("Corruption" to 3))
            )
        ),
        "ch3_darian_mercy" to Scene(
            id = "ch3_darian_mercy",
            speaker = "Darian",
            text = "He looks at you with weary eyes, slumped against the Sanctum walls. 'Mercy? From you? ...Go on then. Finish the ritual. I'll be watching from the shadows with the other ghosts you've made.'",
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.darian,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Humanity" to 2))
            )
        ),
        "ch3_darian_kill" to Scene(
            id = "ch3_darian_kill",
            speaker = "Darian",
            text = " 'About... time...' \n\nHis body dissolves into shadow on the Sanctum floor. You feel a surge of cold power. The core is yours to control, but the silence he leaves behind is deafening.",
            backgroundResId = R.drawable.castle_inside,
            characterResId = 0,
            choices = listOf(
                Choice("Return to Aelric", "ch4_decision", mapOf("Corruption" to 5))
            )
        ),
        "ch3_darian_ritual" to Scene(
            id = "ch3_darian_ritual",
            speaker = "Narrator",
            text = "You reach into the core at the heart of the Sanctum. It burns. Your skin turns to crystal. \n\nDarian watches, a dark grin on his face. 'Welcome home, King. Enjoy your eternity... and the silence of the one you killed.'",
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
            text = "Aelric stands before the Aethelroot’s core. He holds the same mirror you found at the start. 'You've walked the paths. You've seen the echoes. Now, Keeper... look at yourself one last time.'",
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.aelric,
            choices = listOf(
                Choice("Look into the mirror", "ch4_mirror_final", mapOf())
            )
        ),
        "ch4_mirror_final" to Scene(
            id = "ch4_mirror_final",
            speaker = "Player",
            text = "In the heart of the Spire, you stand before the final mirror. \n\nIt shows you... but not just you. Beside your reflection is a blurred figure, their face missing, their hand resting on your shoulder. \n\nThe realization hits you like a physical blow. You don't remember who you failed. Was it a friend? A lover? A child? Their name is gone, but the hole they left is eternal.",
            backgroundResId = R.drawable.castle_inside,
            characterResId = R.drawable.player,
            choices = listOf(
                Choice("Marry Seraphine & Rule", "final_fairytale", mapOf("Humanity" to 15, "Memory" to 15)),
                Choice("Become King of Entropy", "final_doom", mapOf("Corruption" to 15)),
                Choice("The Nameless Wanderer", "final_medium", mapOf("Humanity" to 5)),
                Choice("Take Aelric's Place", "final_keeper", mapOf("Memory" to 10)),
                Choice("Walk Away Forever", "final_mortal", mapOf("Humanity" to 10))
            )
        ),
        
        // ENDING TRIGGERS
        "final_fairytale" to Scene(id="final_fairytale", text="The choice is made. The wedding bells ring once more, but as you stand beside Seraphine, your eyes wander to the empty seat in the front row. You still can't remember their face.", backgroundResId=R.drawable.waterfall, characterResId=R.drawable.player, choices=listOf(Choice("Accept your fate", "final_trigger", mapOf()))),
        "final_doom" to Scene(id="final_doom", text="The core shatters. The void consumes the Obsidian City. As the dark takes you, a single tear falls from your eye for someone whose name you will never know.", backgroundResId=R.drawable.dark_city, characterResId=R.drawable.corrupted_player, choices=listOf(Choice("Embrace the dark", "final_trigger", mapOf()))),
        "final_medium" to Scene(id="final_medium", text="You leave the crown behind in the Gnarled Wilds. You walk away, a silver wing in your hand, followed by a shadow that has no face.", backgroundResId=R.drawable.forest_1, characterResId=R.drawable.player, choices=listOf(Choice("Step into the unknown", "final_trigger", mapOf()))),
        "final_keeper" to Scene(id="final_keeper", text="Aelric hands you his staff. You begin your watch. Eternity is a long time to spend with the ghost of someone you can't remember failing.", backgroundResId=R.drawable.hut, characterResId=R.drawable.player, choices=listOf(Choice("Begin your watch", "final_trigger", mapOf()))),
        "final_mortal" to Scene(id="final_mortal", text="The magic fades. You are just a man again. You walk home, but every mirror you pass shows a blurred figure walking right beside you.", backgroundResId=R.drawable.forest_2, characterResId=R.drawable.player, choices=listOf(Choice("Walk home", "final_trigger", mapOf())))
    )

    val endings = listOf(
        Ending(
            id = "silent_void",
            condition = Condition("Corruption", ">=", 12),
            text = "THE KING OF ENTROPY: You rule a kingdom of shadows in the Obsidian City. You are immortal, powerful, and utterly alone. Somewhere in the void, the person you sacrificed wanders without a face, a silent reminder of the price of your crown.",
            backgroundResId = R.drawable.dark_city
        ),
        Ending(
            id = "true_escape",
            condition = Condition("Memory", ">=", 12),
            text = "THE FAIRYTALE OF THE REBORN CITY: The city is saved, but it is a hollow victory. Every golden leaf on the Aethelroot reminds you of the one life you couldn't buy back. You and Seraphine rule in a paradise built on a forgotten grave.",
            backgroundResId = R.drawable.waterfall
        ),
        Ending(
            id = "eternal_keeper",
            condition = Condition("Memory", ">=", 10),
            text = "THE ETERNAL KEEPER: You guide the lost souls out of the Vale, searching every face for the one you recognize. But you never find them. You are the guardian of memories you will never truly possess.",
            backgroundResId = R.drawable.shattered_battlefield
        ),
        Ending(
            id = "mortal_path",
            condition = Condition("Humanity", ">=", 5),
            text = "THE NAMELESS WANDERER: You wake up under a real sun. You have no magic, no memory, and no crown. But sometimes, when you catch your reflection in a stream, you see a second person smiling back. They forgive you, even if you don't know who they are.",
            backgroundResId = R.drawable.forest_1
        )
    )

    fun getScene(id: String): Scene? = scenes[id]
}
