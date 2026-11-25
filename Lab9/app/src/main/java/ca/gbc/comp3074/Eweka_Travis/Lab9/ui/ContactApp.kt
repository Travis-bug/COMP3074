package ca.gbc.comp3074.Eweka_Travis.Lab9.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun ContactApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "contacts"
    ) {
        composable("contacts") {
            ContactListScreen(
                onContactClick = { userId ->
                    navController.navigate(
                        "details/$userId"
                    )
                }
            )
        }

        composable(
            route = "details/{userId}"
        ) { backStackEntry ->

            val userId = backStackEntry.arguments
                ?.getString("userId")
                ?.toIntOrNull()

            ContactDetailsScreen(
                userId = userId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
