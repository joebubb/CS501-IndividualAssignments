package com.jbubb.shoppingapp.composables

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jbubb.shoppingapp.model.Product

@Composable
fun ShoppingApp(products: List<Product>) {
    val orientation = LocalConfiguration.current.orientation
    var selectedProductId by rememberSaveable { mutableStateOf<Int?>(null) }
    // save a unique id for each product instead of the product itself
    // this is because when the orientation changes the old objects are destroyed and
    // referencing them will make the app crash. a unique id allows the object to be found in the new list
    val setSelected  = { new: Product? ->
        selectedProductId = new?.id
    }

    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
        PortraitView(products, selectedProductId, setSelected)
    } else {
        LandscapeView(products, selectedProductId, setSelected)
    }
}

@Composable
fun LandscapeView(products: List<Product>, selectedProductId: Int?, setter: (Product?) -> Unit) {
    var state = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }
    Row { // row of two columns. one is the list of products and one is the details for one product
        // column for the list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(0.4f).background(Color.Magenta),
            state = state
        ) {
            items(products) { product -> // display the text of each item in a box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (product.id == selectedProductId) Color.White else Color(210, 210, 210)) // make all gray except the selected one
                        .clickable {
                            setter(product) // select the product when clicked
                        },
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(product.name, fontSize = 24.sp, modifier = Modifier.padding(30.dp)) // product name
                    if (product.id == selectedProductId) {
                        Box(modifier = Modifier.fillMaxWidth().height(5.dp).background(Color.Blue)) // put a blue line under the selected product
                    }
                }

            }
        }
        // an area to display the product details
        if (selectedProductId == null) { // if nothing is selected display a message
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight().fillMaxWidth()
            ) {
                Text("Select a product to see its details.", fontSize = 24.sp)
            }
        } else {
            // if a product is selected show its details
            val selectedProduct = products.find { it.id == selectedProductId }
            Column(modifier = Modifier.padding(start = 15.dp)) {
                Text(selectedProduct!!.name, fontSize = 50.sp, modifier = Modifier.padding(vertical = 10.dp)) // title
                Text("$${selectedProduct.price}", fontSize = 40.sp, color = Color(39, 82, 41), modifier = Modifier.padding(bottom = 25.dp)) // price
                Text(selectedProduct.description, fontSize = 30.sp)
            }
        }
    }
}

@Composable
fun PortraitView(products: List<Product>, selectedProductId: Int?, setter: (Product?) -> Unit) {
    var state = rememberSaveable(saver = LazyListState.Saver) { LazyListState() }

    if (selectedProductId == null) { // if nothing is selected then show the list
        LazyColumn(state = state) {
            items(products) { product ->
                Box(
                    contentAlignment = Alignment.BottomCenter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            setter(product)
                        }
                        .background(Color(210, 210, 210))
                ) {
                    Text(
                        product.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 30.dp),
                        fontSize = 30.sp
                    )
                    HorizontalDivider(thickness = 1.dp, color = Color.Black)
                }
            }
        }
    } else {
        // if something is selected then show all details
        val selectedProduct = products.find { it.id == selectedProductId }
        Column {
            Button(onClick = {
                setter(null)
            }) {
                Text("Go Back")
            }
            Text(selectedProduct!!.name, fontSize = 40.sp)
            Text("$${selectedProduct.price}", fontSize = 30.sp, color = Color(39, 82, 41))
            Text(selectedProduct.description, fontSize = 24.sp)
        }
    }
}