package com.josephbubb.recipes.data


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.ResponseBody


@JsonClass(generateAdapter = true)
data class MealResponse(
    @field:Json(name = "meals")
    val meals: List<Meal>?
)