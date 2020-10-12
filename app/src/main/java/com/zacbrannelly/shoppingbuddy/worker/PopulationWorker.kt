package com.zacbrannelly.shoppingbuddy.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.zacbrannelly.shoppingbuddy.data.AppDatabase
import com.zacbrannelly.shoppingbuddy.data.InitialDataDto
import kotlinx.coroutines.coroutineScope
import java.lang.Exception

const val INITIAL_DATA_FILE = "data.json"

class PopulationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        try {
            applicationContext.assets.open(INITIAL_DATA_FILE).use { inputStream ->
                val data = Gson().fromJson(inputStream.reader(), InitialDataDto::class.java)
                val database = AppDatabase.getInstance(applicationContext)

                // Insert the recipes into the DB.
                database.recipeDao().insertAll(data.recipes)

                Result.success()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error populating DB: ", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "LoaderWorker"
    }
}