package com.example.happyplacesapp.database.Models

import android.media.Image

data class HappyPlaceModel(
    val id: Int,
    val title: String,
    val image: String,
    val description:String,
    val date :String,
    val location:String,
    val latitude: Double,
    val longitude:Double
)
