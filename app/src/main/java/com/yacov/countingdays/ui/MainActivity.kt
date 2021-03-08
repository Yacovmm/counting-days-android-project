package com.yacov.countingdays.ui

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.yacov.countingdays.R
import com.yacov.countingdays.data.local.LocalSharedPreferences
import com.yacov.countingdays.utils.Constants.SP_KEY

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//
//        val toolbar: Toolbar = findViewById(R.id.toolbar)
//
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayShowTitleEnabled(false)

        IntentFilter(Intent.ACTION_PROCESS_TEXT)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        subscribeToFirebaseTopic()
    }

    private fun subscribeToFirebaseTopic() {
        LocalSharedPreferences(this).getSharedPreferences(SP_KEY)?.let {
            FirebaseMessaging.getInstance().subscribeToTopic(it)
        }
    }
}
