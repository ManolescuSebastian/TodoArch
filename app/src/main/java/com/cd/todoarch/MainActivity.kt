package com.cd.todoarch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cd.todoarch.data.Task
import com.cd.todoarch.ribs.root.RootRouter
import com.cd.todoarch.ribs.taskDetailRib.TaskDetailScreen
import com.cd.todoarch.ui.theme.TodoArchTheme

class MainActivity : ComponentActivity() {

    private lateinit var rootRouter: RootRouter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (application as TaskApplication).appComponent
        rootRouter = appComponent.rootBuilder().build()
        rootRouter.dispatchAttach()
        enableEdgeToEdge()
        setContent {
            TodoArchTheme {
                rootRouter.View()
            }
        }

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (!rootRouter.handleBackPress()) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    } else {
                        println("MainActivity: Back press handled by RIBs.")
                    }
                }
            }
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        rootRouter.dispatchDetach()
    }
}




