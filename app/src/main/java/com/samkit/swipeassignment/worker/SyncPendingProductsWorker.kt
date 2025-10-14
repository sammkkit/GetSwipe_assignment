package com.samkit.swipeassignment.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.samkit.swipeassignment.R
import com.samkit.swipeassignment.domain.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject

class SyncPendingProductsWorker(
    private val appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val repository: ProductRepository by inject(ProductRepository::class.java)
    private val channelId = "sync_channel"
    private val notificationId = 1001

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            createNotificationChannel()
            showNotification("Syncing products...", 0, true)

            repository.syncPendingProducts()

            showNotification("Sync completed ✅", 100, false)
            Result.success()
        } catch (e: Exception) {
            showNotification("Sync failed ❌", 100, false)
            Result.retry()
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showNotification(title: String, progress: Int, isIndeterminate: Boolean) {
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setOngoing(isIndeterminate)
            .setProgress(100, progress, isIndeterminate)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        NotificationManagerCompat.from(appContext).notify(notificationId, builder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Product Sync",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}