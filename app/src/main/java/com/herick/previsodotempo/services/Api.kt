package com.herick.previsodotempo.services

import com.herick.previsodotempo.model.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

//https://api.openweathermap.org/data/2.5/weather?q={city name}&appid={API key}
//key = 6b9f60a1f9b3ecea6198a469b529863c

interface Api {
    @GET("weather")

    fun weatherMap(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String

    ): Call<Main>
}