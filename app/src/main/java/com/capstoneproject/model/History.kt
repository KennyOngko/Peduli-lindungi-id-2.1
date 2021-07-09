package com.capstoneproject.model

data class History(
    val id: String,
    val imageUrl: String,
    val models: ArrayList<String>,
    val user_id: String,
    val request_date: String,
    val positivePercentage: Double,
    val negativePercentage: Double
)
