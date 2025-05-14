package com.cd.todoarch

interface AppComponent {
    fun rootBuilder(): RootBuilder
}

class AppComponentFactory {
    companion object {
        fun create() : AppComponent {
            return object : AppComponent {
                override fun rootBuilder(): RootBuilder {
                    TODO("Not yet implemented")
                }
            }
        }
    }
}

class TaskApplication {
}