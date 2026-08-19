package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.MainScreen
import com.example.ui.theme.SasaTheme
import com.example.ui.viewmodel.SasaViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: SasaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SasaTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}
