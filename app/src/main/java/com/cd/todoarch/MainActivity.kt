package com.cd.todoarch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailScreen
import com.cd.todoarch.ui.theme.TodoArchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TodoArchTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "todoList") {
                        composable("todoList") { TodoApp(navController, innerPadding) }
                        composable("taskDetail/{title}/{description}") { backStackEntry ->
                            val title = backStackEntry.arguments?.getString("title") ?: ""
                            val description = backStackEntry.arguments?.getString("description") ?: ""
                            TaskDetailScreen(title, description, navController)
                        }
                    }
                }
            }
        }
    }
}




