package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.CropCareViewModel
import com.example.ui.navigation.NavGraph
import com.example.ui.theme.CropCareTheme

class MainActivity : ComponentActivity() {
    private val viewModel: CropCareViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            CropCareTheme(darkTheme = isDarkMode) {
                NavGraph(viewModel = viewModel)
            }
        }
    }
}

