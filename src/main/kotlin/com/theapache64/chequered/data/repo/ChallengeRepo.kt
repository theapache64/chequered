package com.theapache64.chequered.data.repo

import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import org.w3c.dom.get

object ChallengeRepo {

    private const val KEY_CHALLENGES = "chequered_challenges"

    fun getChallenges(): Set<String> {
        val challenges = localStorage.getItem(KEY_CHALLENGES) ?: "[]"
        return Json.decodeFromString(challenges)
    }

    fun addChallenge(challenge: String) {
        val updatedChallenges = getChallenges().toMutableSet().apply {
            add(challenge)
        }
        localStorage.setItem(KEY_CHALLENGES, JSON.stringify(updatedChallenges))
    }
}