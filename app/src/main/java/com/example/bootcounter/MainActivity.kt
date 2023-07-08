package com.example.bootcounter

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.bootcounter.database.BootEventDao
import com.example.bootcounter.database.BootEventDatabase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var bootEventDao: BootEventDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        bootEventDao = BootEventDatabase.getDatabase(this).bootEventDao()

        lifecycleScope.launch {
            bootEventDao.getAllBootEvents().collect { bootEvents ->
                val text = if (bootEvents.isEmpty()) {
                    "No boots detected"
                } else {
                    "${bootEvents.size} - ${bootEvents.last().timestamp}"
                }
                findViewById<TextView>(R.id.textView).text = text
            }
        }
    }

}