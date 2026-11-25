package ca.gbc.comp3074.Eweka_Travis.Lab9

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import ca.gbc.comp3074.Eweka_Travis.Lab9.ui.ContactApp
import ca.gbc.comp3074.Eweka_Travis.Lab9.ui.theme.Lab9Theme

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {
            Lab9Theme {
                ContactApp()
            }
        }
    }
}
