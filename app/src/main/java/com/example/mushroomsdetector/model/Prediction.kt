package com.example.mushroomsdetector.model

data class Prediction(
    var mushroomName: String? = null,
    var probability: Int? = null,
    var description: String? = null
)
