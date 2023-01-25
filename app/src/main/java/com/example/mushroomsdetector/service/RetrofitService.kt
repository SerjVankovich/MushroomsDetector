package com.example.mushroomsdetector.service

import com.example.mushroomsdetector.model.Picture
import com.example.mushroomsdetector.model.Prediction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {
    @POST("/recognize/")
    fun sendPictureForRecognize(@Body picture: Picture): Call<Prediction>

    @GET("/mushroom/sample/{mushroomName}")
    fun getMushroomSamples(@Path("mushroomName") mushroomName: String, @Query("num") num: Int): Call<List<String>>
}