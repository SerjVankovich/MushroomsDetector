package com.example.mushroomsdetector.common

import com.example.mushroomsdetector.BuildConfig
import com.example.mushroomsdetector.service.RetrofitClient
import com.example.mushroomsdetector.service.RetrofitService

object Common {
    private const val BASE_URL = BuildConfig.API_URL
    val retrofitService: RetrofitService
        get() = RetrofitClient.getClient(BASE_URL).create(RetrofitService::class.java)
}