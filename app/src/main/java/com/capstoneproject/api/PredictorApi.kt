package com.capstoneproject.api

import com.capstoneproject.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PredictorApi {
    val client = Retrofit.Builder()
        .baseUrl(Constants.API_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PredictorApiInterface::class.java)
}