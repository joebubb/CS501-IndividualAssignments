package com.josephbubb.recipes.data // Make sure package matches yours

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true) // Tells Moshi to generate an efficient adapter
data class Meal(
    @field:Json(name = "idMeal")
    val id: String?,

    @field:Json(name = "strMeal")
    val name: String?,

    @field:Json(name = "strDrinkAlternate") // Note: API actually has strMealAlternate
    val alternateName: String?, // Corrected based on common API patterns, but verify your exact API if needed

    @field:Json(name = "strCategory")
    val category: String?,

    @field:Json(name = "strArea")
    val area: String?,

    @field:Json(name = "strInstructions")
    val instructions: String?,

    @field:Json(name = "strMealThumb")
    val thumbnailUrl: String?, // Image URL

    @field:Json(name = "strTags")
    val tags: String?, // Comma-separated tags or null

    @field:Json(name = "strYoutube")
    val youtubeUrl: String?, // YouTube video URL

    // --- Ingredients (Nullable Strings) ---
    @field:Json(name = "strIngredient1") val ingredient1: String?,
    @field:Json(name = "strIngredient2") val ingredient2: String?,
    @field:Json(name = "strIngredient3") val ingredient3: String?,
    @field:Json(name = "strIngredient4") val ingredient4: String?,
    @field:Json(name = "strIngredient5") val ingredient5: String?,
    @field:Json(name = "strIngredient6") val ingredient6: String?,
    @field:Json(name = "strIngredient7") val ingredient7: String?,
    @field:Json(name = "strIngredient8") val ingredient8: String?,
    @field:Json(name = "strIngredient9") val ingredient9: String?,
    @field:Json(name = "strIngredient10") val ingredient10: String?,
    @field:Json(name = "strIngredient11") val ingredient11: String?,
    @field:Json(name = "strIngredient12") val ingredient12: String?,
    @field:Json(name = "strIngredient13") val ingredient13: String?,
    @field:Json(name = "strIngredient14") val ingredient14: String?,
    @field:Json(name = "strIngredient15") val ingredient15: String?,
    @field:Json(name = "strIngredient16") val ingredient16: String?,
    @field:Json(name = "strIngredient17") val ingredient17: String?,
    @field:Json(name = "strIngredient18") val ingredient18: String?,
    @field:Json(name = "strIngredient19") val ingredient19: String?,
    @field:Json(name = "strIngredient20") val ingredient20: String?,

    // --- Measures (Nullable Strings) ---
    @field:Json(name = "strMeasure1") val measure1: String?,
    @field:Json(name = "strMeasure2") val measure2: String?,
    @field:Json(name = "strMeasure3") val measure3: String?,
    @field:Json(name = "strMeasure4") val measure4: String?,
    @field:Json(name = "strMeasure5") val measure5: String?,
    @field:Json(name = "strMeasure6") val measure6: String?,
    @field:Json(name = "strMeasure7") val measure7: String?,
    @field:Json(name = "strMeasure8") val measure8: String?,
    @field:Json(name = "strMeasure9") val measure9: String?,
    @field:Json(name = "strMeasure10") val measure10: String?,
    @field:Json(name = "strMeasure11") val measure11: String?,
    @field:Json(name = "strMeasure12") val measure12: String?,
    @field:Json(name = "strMeasure13") val measure13: String?,
    @field:Json(name = "strMeasure14") val measure14: String?,
    @field:Json(name = "strMeasure15") val measure15: String?,
    @field:Json(name = "strMeasure16") val measure16: String?,
    @field:Json(name = "strMeasure17") val measure17: String?,
    @field:Json(name = "strMeasure18") val measure18: String?,
    @field:Json(name = "strMeasure19") val measure19: String?,
    @field:Json(name = "strMeasure20") val measure20: String?,

    // --- Source and Metadata ---
    @field:Json(name = "strSource")
    val sourceUrl: String?, // URL to original recipe source

    @field:Json(name = "strImageSource")
    val imageSource: String?, // Usually null in this API

    @field:Json(name = "strCreativeCommonsConfirmed")
    val creativeCommonsConfirmed: String?, // Usually null

    @field:Json(name = "dateModified")
    val dateModified: String? // Usually null
)