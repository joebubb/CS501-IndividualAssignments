package com.josephbubb.recipes.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class RecipesViewModel : ViewModel() {
    sealed class RecipesState {
        object Initial : RecipesState()        // Initial state before any action
        object Loading : RecipesState()        // State when data is being fetched
        data class Success(val response: MealResponse) : RecipesState() // State on successful fetch
        data class Error(val message: String) : RecipesState() // State on error
    }

    private val _recipesState = MutableStateFlow<RecipesState>(RecipesState.Initial)
    val recipesState: StateFlow<RecipesState> = _recipesState.asStateFlow()

    // Simplified ViewModel structure (similar to Weather example)
    fun searchRecipes(query: String) {
        val tag = "RecipeSearchVM"
        if (_recipesState.value == RecipesState.Loading || query.isBlank()) { /*...*/ return }

        viewModelScope.launch {
            _recipesState.value = RecipesState.Loading
            Log.d(tag,"Starting search for: '$query'")
            try {
                // Let Retrofit handle parsing automatically again
                val response = ApiClient.apiService.searchMealsByName(query)

                if (response.isSuccessful) {
                    val mealResponse = response.body()
                    if (mealResponse != null) {
                        Log.d(tag, "SUCCESS: Auto-parsed MealResponse. Count: ${mealResponse.meals?.size ?: 0}")
                        _recipesState.value = RecipesState.Success(mealResponse)
                    } else {
                        Log.w(tag, "SUCCESS code but body was null/unparseable by Retrofit.")
                        _recipesState.value = RecipesState.Error("Received empty or invalid success response (Code ${response.code()}).")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(tag, "API ERROR (Code ${response.code()}): ${errorBody ?: response.message()}")
                    _recipesState.value = RecipesState.Error("API Error: ${response.code()} - ${errorBody ?: response.message()}")
                }

            } catch (e: JsonDataException) { // Catch the specific parsing error
                Log.e(tag, "MOSHI PARSING FAILED: ${e.message}", e)
                _recipesState.value = RecipesState.Error("Data format error: ${e.message}")
            } catch (e: IOException) {
                Log.e(tag, "Network Error: ${e.message}", e)
                _recipesState.value = RecipesState.Error("Network Error: ${e.message ?: "Check connection"}")
            } catch (e: Exception) { // Catch any other errors
                Log.e(tag, "Other Exception: ${e.javaClass.simpleName} - ${e.message}", e)
                _recipesState.value = RecipesState.Error("An unexpected error occurred: ${e.message}")
            } finally {
                Log.d(tag,"Search finished for: '$query'")
            }
        }
    }
    fun resetState() {
        _recipesState.value = RecipesState.Initial
    }
}