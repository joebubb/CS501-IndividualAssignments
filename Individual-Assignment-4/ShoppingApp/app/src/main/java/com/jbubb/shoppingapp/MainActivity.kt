package com.jbubb.shoppingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.jbubb.shoppingapp.composables.LandscapeView
import com.jbubb.shoppingapp.composables.ShoppingApp
import com.jbubb.shoppingapp.model.Product
import com.jbubb.shoppingapp.ui.theme.ShoppingAppTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val productList = listOf(
            Product("Wireless Headphones", 79.99, "Noise-cancelling over-ear headphones with 30-hour battery life.", 0),
            Product("Smart Watch", 149.99, "Fitness tracking, heart rate monitor, and message notifications.", 1),
            Product("Bluetooth Speaker", 49.99, "Portable speaker with deep bass and waterproof design.", 2),
            Product("Laptop Stand", 29.99, "Adjustable stand for laptops up to 17 inches.", 3),
            Product("USB-C Hub", 39.99, "Multi-port hub with HDMI, USB 3.0, and SD card reader.", 4),
            Product("Wireless Mouse", 24.99, "Ergonomic mouse with adjustable DPI and silent clicks.", 5),
            Product("4K Monitor", 299.99, "27-inch UHD monitor with ultra-thin bezels and HDR support.", 6),
            Product("Gaming Keyboard", 89.99, "Mechanical keyboard with customizable RGB lighting.", 7),
            Product("Portable Charger", 19.99, "10,000mAh power bank with fast charging capability.", 8),
            Product("Smart Light Bulb", 14.99, "Wi-Fi-enabled bulb with voice control and color options.", 9)
        )


        enableEdgeToEdge()
        setContent {
            ShoppingAppTheme {
                ShoppingApp(productList)
            }
        }
    }
}
