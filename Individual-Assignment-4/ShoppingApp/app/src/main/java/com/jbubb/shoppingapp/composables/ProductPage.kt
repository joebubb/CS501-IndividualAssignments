package com.jbubb.shoppingapp.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbubb.shoppingapp.model.Product

@Composable
fun LandscapeView(products: List<Product>) {
    var selectedProduct by remember { mutableStateOf<Product?>(null) } // keep track of which product is selected
    Row { // row of two columns. one is the list of products and one is the details for one product
        // column for the list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.4f).background(Color.Magenta)
        ) {
            items(products) { product -> // display the text of each item in a box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (product == selectedProduct) Color.White else Color(210, 210, 210)) // make all gray except the selected one
                        .clickable {
                            selectedProduct = product
                        },
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(product.name, fontSize = 24.sp, modifier = Modifier.padding(30.dp)) // product name
                    if (product == selectedProduct) {
                        Box(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Blue))
                    }
                }

            }
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=landscape")
@Composable
fun LandscapePreview() {
    val productList = listOf(
        Product("Wireless Headphones", 79.99, "Noise-cancelling over-ear headphones with 30-hour battery life."),
        Product("Smart Watch", 149.99, "Fitness tracking, heart rate monitor, and message notifications."),
        Product("Bluetooth Speaker", 49.99, "Portable speaker with deep bass and waterproof design."),
        Product("Laptop Stand", 29.99, "Adjustable stand for laptops up to 17 inches."),
        Product("USB-C Hub", 39.99, "Multi-port hub with HDMI, USB 3.0, and SD card reader."),
        Product("Wireless Mouse", 24.99, "Ergonomic mouse with adjustable DPI and silent clicks."),
        Product("4K Monitor", 299.99, "27-inch UHD monitor with ultra-thin bezels and HDR support."),
        Product("Gaming Keyboard", 89.99, "Mechanical keyboard with customizable RGB lighting."),
        Product("Portable Charger", 19.99, "10,000mAh power bank with fast charging capability."),
        Product("Smart Light Bulb", 14.99, "Wi-Fi-enabled bulb with voice control and color options.")
    )

    LandscapeView(productList)
}