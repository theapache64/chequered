package com.theapache64.chequered

import com.theapache64.chequered.data.repo.ChallengeRepo
import com.theapache64.chequered.model.Challenge
import com.theapache64.chequered.utils.KeyCode
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.dom.clear
import org.w3c.dom.*
import org.w3c.dom.url.URL
import kotlin.js.Date


// Ref views
val uiInputNewClg by lazy { document.getElementById("input_new_challenge")!! as HTMLInputElement }
val uiSelectChallenges by lazy { document.getElementById("select_challenges")!! as HTMLSelectElement }
val uiTextAreaTweet by lazy { document.getElementById("textarea_tweet")!! as HTMLTextAreaElement }
val uiButtonTweet by lazy { document.getElementById("button_tweet")!! as HTMLButtonElement }

fun main() {

    println("Init Chequered!")

    // Fill challenges
    refreshUiSelectChallenges(ChallengeRepo.getLastSelectedChallenge())

    // Add enter listener for input
    uiInputNewClg.onkeyup = { event ->
        if (event.keyCode == KeyCode.KEY_CODE_ENTER) {
            val addedClg = addNewChallenge(uiInputNewClg)
            if (addedClg != null) {
                refreshUiSelectChallenges(addedClg)
            }
        }
    }

    // Add select box on change listener
    uiSelectChallenges.onchange = {
        val selClg = ChallengeRepo.getChallenges().find { it.title == uiSelectChallenges.value }!!
        onChallengeChanged(selClg)
    }

    // Listener for F2
    window.onkeyup = {

        if (it.keyCode == KeyCode.KEY_CODE_F2) {
            promptEditChallenge()
        }

        // Delete active challenge
        if (document.activeElement?.id != uiTextAreaTweet.id) {
            if (it.keyCode == KeyCode.KEY_CODE_DELETE) {
                val selectedChannel = ChallengeRepo.getLastSelectedChallenge()
                if (selectedChannel != null) {
                    confirmDelete(selectedChannel)
                }
            }
        }
    }

    // Listening for button click
    uiButtonTweet.onclick = {
        window.alert("Clicked!")

        window.open(
            URL("https://twitter.com/intent/tweet?text=${uiTextAreaTweet.value}").href,
            "_blank"
        )
    }

}

private fun promptEditChallenge() {
    val currentChallenge = ChallengeRepo.getLastSelectedChallenge()

    if (currentChallenge != null) {

        val input = window.prompt(
            "Enter current day. 0 to delete the challenge",
            currentChallenge.getDay().toString()
        )

        if (input != null) {
            try {
                val numInput = input.toInt()
                if (numInput in 0..100) {
                    if (numInput == 0) {
                        // Delete challenge
                        confirmDelete(currentChallenge)
                    } else {
                        // Number is between 1 and 100. so edit the list
                        val today = Date().getTime()
                        val dateOffset: Double = (24 * 60 * 60 * 1000) * (numInput.toDouble() - 1);
                        currentChallenge.startedAt = (today - dateOffset)
                        ChallengeRepo.updateChallenge(currentChallenge)
                        refreshUiSelectChallenges()
                    }
                } else {
                    window.alert("Day must shouldn't be less than 0 and greater than 100. $numInput is invalid.")
                }
            } catch (e: NumberFormatException) {
                window.alert("$input is not a number")
            }
        }
    }
}

private fun confirmDelete(currentChallenge: Challenge) {
    val isYes = window.confirm("Are you sure you want to delete ${currentChallenge.title}?")
    if (isYes) {
        // Delete confirmed
        val isDeleted = ChallengeRepo.deleteChallenge(currentChallenge)
        if (isDeleted) {
            refreshUiSelectChallenges()
        } else {
            window.alert("Uh ho!! Sorry. You should have at least one challenge in your life.")
        }
    }
}

fun onChallengeChanged(selectedClg: Challenge) {
    console.log("Selected: -> $selectedClg")

    // Save on db
    ChallengeRepo.setLastSelectedChallenge(selectedClg)

    // Modify text
    uiTextAreaTweet.value = """
        Day ${selectedClg.getDay()} of ${selectedClg.title}
        
        TYPE_YOUR_PROGRESS_HERE
        
        #100DaysOfX 
    """.trimIndent()

}

private fun preselect(uiSelectChallenges: HTMLSelectElement, addedClg: Challenge) {
    uiSelectChallenges.selectedIndex =
        ChallengeRepo.getChallenges().indexOfFirst { it.title == addedClg.title }.let { index ->
            if (index == -1) {
                0
            } else {
                index
            }
        }

    onChallengeChanged(addedClg)
}

private fun refreshUiSelectChallenges(preSelectedClg: Challenge? = null) {
    uiSelectChallenges.clear()
    val challenges = ChallengeRepo.getChallenges()
    for (clg in challenges) {
        val clgOpt = Option(
            text = "${clg.title} (Day ${clg.getDay()})",
            value = clg.title
        )
        uiSelectChallenges.options.add(clgOpt)
    }

    preselect(uiSelectChallenges, preSelectedClg ?: challenges.first())
}

private fun addNewChallenge(inputNewClg: HTMLInputElement): Challenge? {

    var newClgTitle = inputNewClg.value
    return if (newClgTitle.isNotBlank()) {

        newClgTitle = newClgTitle
            .trim()
            // Removing #esh
            .replace("#", "")
            // Replace spaces with underscore
            .replace(" ", "_")
            .capitalize()

        if (!newClgTitle.startsWith("100DaysOf", true)) {
            newClgTitle = "100DaysOf$newClgTitle"
        }

        // Prefix with hash
        newClgTitle = "#$newClgTitle"

        val newClg = Challenge(newClgTitle, Date().getTime())
        ChallengeRepo.addChallenge(newClg)
        uiInputNewClg.value = ""
        newClg
    } else {
        window.alert("Challenge name can't be empty")
        null
    }
}