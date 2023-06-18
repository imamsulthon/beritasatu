package com.imams.beritasatu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.imams.topnews.ui.everything.EverythingNewsActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        gotoHome()
    }

    private fun gotoHome() {
        lifecycleScope.launch {
            delay(1000)
//            startActivity(Intent(this@MainActivity, HomeTopNewsActivity::class.java))
            startActivity(Intent(this@MainActivity, EverythingNewsActivity::class.java))
            finish()
        }
    }
}