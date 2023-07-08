package com.example.bootcounter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.bootcounter.database.BootEvent
import com.example.bootcounter.database.BootEventDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val timestamp = System.currentTimeMillis()
            CoroutineScope(Dispatchers.IO).launch {
                val bootEvent = BootEvent(timestamp = timestamp)
                BootEventDatabase.getDatabase(context).bootEventDao().insert(bootEvent)
            }
        }
    }
}