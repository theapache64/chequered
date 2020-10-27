package com.theapache64.chequered.data.repo

import com.theapache64.chequered.model.Challenge
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlin.js.Date

object ChallengeRepo {

    private const val KEY_CHALLENGES = "ongoing_challenges"
    private const val KEY_LAST_CHALLENGE = "last_challenge"
    private val defaultClg by lazy {
        Challenge(
            "#100DaysOfCode",
            Date().getTime()
        )
    }

    /**
     * To get challenges added. The list will always have at least one item.
     */
    fun getChallenges(): List<Challenge> {
        val challenges = localStorage.getItem(KEY_CHALLENGES)
        if (challenges == null || challenges.isEmpty()) {
            // Adding default challenge
            val challengesFirst = listOf(defaultClg)
            localStorage.setItem(KEY_CHALLENGES, Json.encodeToString(challengesFirst))
            return challengesFirst
        }
        return Json.decodeFromString(challenges)
    }

    /**
     * To add new challenge. If the challenge already exist, the startDate will be updated to current date.
     */
    fun addChallenge(challenge: Challenge) {
        val challenges = getChallenges()
        val oldChallenge = challenges.find { it.title.equals(challenge.title, true) }
        val jsonToSave = if (oldChallenge == null) {

            val updatedChallenges = challenges.toMutableSet().apply {
                add(challenge)
            }
            Json.encodeToString(updatedChallenges)
        } else {
            // already exist one challenge with same title. so don't do anything
            Json.encodeToString(challenges)
        }
        localStorage.setItem(KEY_CHALLENGES, jsonToSave)
    }

    fun getLastSelectedChallenge(): Challenge? {
        val lastClgJson = localStorage.getItem(KEY_LAST_CHALLENGE)
        return if (lastClgJson != null && lastClgJson.isNotBlank()) {
            Json.decodeFromString(lastClgJson)
        } else {
            null
        }
    }

    fun setLastSelectedChallenge(clg: Challenge) {
        localStorage.setItem(KEY_LAST_CHALLENGE, Json.encodeToString(clg))
    }

    fun deleteChallenge(remClg: Challenge): Boolean {
        val challenges = getChallenges()
        if (challenges.size == 1) {
            // can't delete
            return false
        }
        val updatedChallenges = challenges.toMutableList().apply {
            removeAll { it.title == remClg.title }
        }
        localStorage.setItem(KEY_CHALLENGES, Json.encodeToString(updatedChallenges))
        return true
    }

    fun updateChallenge(currentChallenge: Challenge) {
        val challenges = getChallenges()
        val updatedChallenges = challenges.toMutableList().apply {
            // Remove old one
            removeAll { it.title == currentChallenge.title }

            // Update
            add(0, currentChallenge)
        }
        console.log("Dupae $updatedChallenges", )
        localStorage.setItem(KEY_CHALLENGES, Json.encodeToString(updatedChallenges))
    }
}