package com.samkit.swipeassignment.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.samkit.swipeassignment.worker.SyncPendingProductsWorker

object SyncScheduler {
    fun schedulePendingProductSync(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<SyncPendingProductsWorker>()
            .setConstraints(constraints)
            .build()
        //FIX : ExistingWorkPolicy.REPLACE -> Ensures only one work stays, Prevents Duplication.
        WorkManager.getInstance(context).enqueueUniqueWork("Sync Product Work", ExistingWorkPolicy.REPLACE,workRequest)
    }
}
