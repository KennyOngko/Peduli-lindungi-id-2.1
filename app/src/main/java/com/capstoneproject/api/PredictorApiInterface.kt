package com.capstoneproject.api

import com.capstoneproject.model.PredictionRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface PredictorApiInterface {
    @POST("predict")
    fun requestPrediction(@Body request: PredictionRequest): Call<HashMap<String, Boolean>>
}