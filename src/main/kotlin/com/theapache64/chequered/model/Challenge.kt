package com.theapache64.chequered.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.Date
import kotlin.math.abs
import kotlin.math.ceil

@Serializable
data class Challenge(
    @SerialName("title")
    val title: String,
    @SerialName("started_at")
    var startedAt: Double
) {
    fun getDay(): Double {
        val today = Date()
        val diffTime = abs(today.getTime() - startedAt)
        return ceil(diffTime / (1000 * 60 * 60 * 24))
    }
}