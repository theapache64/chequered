package com.theapache64.chequered

import com.theapache64.chequered.data.repo.ChallengeRepo
import com.theapache64.chequered.utils.KeyCode
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent

fun main() {
    // Add enter listener for input
    val inputNewClg = document.getElementById("input_new_challenge")!! as HTMLInputElement
    inputNewClg.addEventListener("keyup", { event ->
        event as KeyboardEvent
        if (event.keyCode == KeyCode.KEY_CODE_ENTER) {
            addNewChallenge(inputNewClg)
        }
    })


}

private fun addNewChallenge(inputNewClg: HTMLInputElement) {

    var newClg = inputNewClg.value.trim()
    if (newClg.isNotEmpty()) {

        if (newClg.contains("#")) {
            // Removing #esh
            newClg = newClg.replace("#", "")
        }

        // Prefix with hash
        newClg = "#$newClg"

        console.log("New clg is $newClg")
        ChallengeRepo.addChallenge(newClg)

        console.log("Total Challenges: ${ChallengeRepo.getChallenges()}")
    } else {
        window.alert("Challenge name can't be empty")
    }
}