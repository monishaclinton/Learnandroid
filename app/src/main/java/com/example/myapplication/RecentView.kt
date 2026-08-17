package com.example.myapplication

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity

class RecentView : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            RecentViewScreen(
                onBackClick = {
                    finish()
                }
            )
        }
    }
}