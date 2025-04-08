package com.josephbubb.recipes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.* // Import layout components
import androidx.compose.material3.Button // Import Button
import androidx.compose.material3.MaterialTheme // Import MaterialTheme
import androidx.compose.material3.Scaffold // Import Scaffold (optional but good practice)
import androidx.compose.material3.Text // Import Text
import androidx.compose.runtime.* // Import runtime functions like collectAsState
import androidx.compose.ui.Alignment // Import Alignment
import androidx.compose.ui.Modifier // Import Modifier
import androidx.compose.ui.unit.dp // Import dp unit
import androidx.lifecycle.viewmodel.compose.viewModel // Import viewModel composable function
import com.josephbubb.recipes.data.RecipesViewModel // Import your ViewModel
import com.josephbubb.recipes.ui.theme.RecipesTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Handles drawing behind system bars
        setContent {
            RecipesTheme { // Apply your app's theme
                // Call your testing composable
                SimpleRecipeTestScreen()
            }
        }
    }
}

// New Composable specifically for testing the ViewModel call
@Composable
fun SimpleRecipeTestScreen(
    viewModel: RecipesViewModel = viewModel() // Get the ViewModel instance
) {
    // Observe the state just to see basic feedback (optional but helpful)
    val state by viewModel.recipesState.collectAsState()

    // Basic layout: A column to center a button
    Scaffold { innerPadding -> // Use Scaffold for proper padding
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Apply padding from Scaffold
                .padding(16.dp), // Add our own padding
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Button to trigger the search
            Button(onClick = {
                // Call the ViewModel function when the button is clicked
                viewModel.searchRecipes("chicken")
            }) {
                Text("Test Search for 'chicken'")
            }

            Spacer(modifier = Modifier.height(20.dp)) // Add some space

            // Display simple text feedback based on the current state
            when (val currentState = state) {
                is RecipesViewModel.RecipesState.Initial -> Text("State: Initial (Click Button)")
                is RecipesViewModel.RecipesState.Loading -> Text("State: Loading...")
                // Success/Error states will be set by your debug ViewModel logic
                is RecipesViewModel.RecipesState.Success -> Text("State: Success (Check Logs for details!)")
                is RecipesViewModel.RecipesState.Error -> Text("State: Error - ${currentState.message}")
            }
        }
    }
}