package com.noteapplication.cs398

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.noteapplication.cs398.database.Folder

class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getSerializableExtra("title") as String
        val content = intent.getSerializableExtra("content") as String
        val notificationHelper = NotificationHelper(context, title, content)
        val nb: NotificationCompat.Builder = notificationHelper.getChannelNotification()
        notificationHelper.getManager().notify(1, nb.build())
    }
}