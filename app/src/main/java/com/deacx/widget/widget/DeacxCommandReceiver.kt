package com.deacx.widget.widget

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeacxCommandReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "com.deacx.widget.RELOAD") {

            CoroutineScope(Dispatchers.IO).launch {
                DeacxWidgetProvider.refreshAll(context)
            }

        }

    }
}
