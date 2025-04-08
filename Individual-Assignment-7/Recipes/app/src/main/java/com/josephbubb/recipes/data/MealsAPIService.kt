package com.josephbubb.recipes.data

import com.josephbubb.recipes.data.MealResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

interface MealsAPIService {
    @GET("search.php") // use search endpoint
    suspend fun searchMealsByName(
        @Query("s") query: String // s parameter for the user's search query
    ): Response<ResponseBody>
}

object ApiClient {
    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory()) // Explicitly add KotlinJsonAdapterFactory
        .build() // Build the Moshi instance


    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val apiService: MealsAPIService = retrofit.create(MealsAPIService::class.java)
}