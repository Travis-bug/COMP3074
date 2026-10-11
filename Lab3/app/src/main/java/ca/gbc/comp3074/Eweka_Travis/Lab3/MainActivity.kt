package ca.gbc.comp3074.Eweka_Travis.Lab3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.gbc.comp3074.Eweka_Travis.Lab3.data.AppDatabase
import ca.gbc.comp3074.Eweka_Travis.Lab3.ui.CustomerApp
import ca.gbc.comp3074.Eweka_Travis.Lab3.ui.CustomerViewModel
import ca.gbc.comp3074.Eweka_Travis.Lab3.ui.CustomerViewModelFactory
import ca.gbc.comp3074.Eweka_Travis.Lab3.ui.theme.Lab3Theme

/**
 * Gets the Room database, creates the ViewModel through its factory, and starts the Compose UI.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        setContent {
            Lab3Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val viewModel: CustomerViewModel = viewModel(
                        factory = CustomerViewModelFactory(database.customerDao())
                    )
                    CustomerApp(
                        viewModel = viewModel,
                        modifier = Modifier
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .imePadding()
                    )
                }
            }
        }
    }
}
