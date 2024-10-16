package com.dicoding.restaurantreview.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dicoding.restaurantreview.R
import com.dicoding.restaurantreview.data.Resource
import com.dicoding.restaurantreview.data.remote.retrofit.ApiConfig
import com.dicoding.restaurantreview.data.repository.EventRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DailyReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val repository = EventRepository.getInstance(ApiConfig.getApiService())
            repository.getEvents(active = -1, query = null, limit = 1).collect { resource ->
                if (resource is Resource.Success && resource.data?.isNotEmpty() == true) {
                    val event = resource.data[0]
                    showNotification(event.name, event.beginTime, event.link)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in DailyReminderWorker", e)
            Result.failure()
        }
    }

    private fun showNotification(eventName: String?, beginTime: String?, eventLink: String?) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(eventLink))
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val timeText = getTimeText(beginTime)
        val subText = "Reminder • $timeText"

        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Upcoming Event")
            .setContentText(eventName)
            .setSubText(subText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun getTimeText(beginTime: String?): String {
        if (beginTime == null) return "Now"

        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
            val eventTime = inputFormat.parse(beginTime)
            val currentTime = Date()

            if (eventTime != null && eventTime.after(currentTime)) {
                val outputFormat = SimpleDateFormat("HH:mm", Locale.US)
                outputFormat.format(eventTime)
            } else {
                "Now"
            }
        } catch (e: ParseException) {
            Log.e(TAG, "Error parsing date: $beginTime", e)
            "Now" // Default to "Now" if parsing fails
        }
    }

    companion object {
        private const val TAG = "DailyReminderWorker"
        private const val CHANNEL_ID = "daily_reminder_channel"
        private const val CHANNEL_NAME = "Daily Reminder"
        private const val NOTIFICATION_ID = 1
    }
}