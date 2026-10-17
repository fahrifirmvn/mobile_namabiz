package com.namabiz.agrim.core.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.namabiz.agrim.core.database.AppDatabase
import com.namabiz.agrim.core.network.NetworkClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class BrochureDownloadWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val documentId = inputData.getInt("DOCUMENT_ID", -1)
        val downloadUrl = inputData.getString("DOWNLOAD_URL") ?: return@withContext Result.failure()

        if (documentId == -1) return@withContext Result.failure()

        val db = AppDatabase.getInstance(applicationContext)
        val documentDao = db.documentDao()

        try {
            documentDao.updateDownloadStatus(documentId, null, "DOWNLOADING", null)

            val apiService = NetworkClient.getService(applicationContext)
            val response = apiService.downloadDocument(documentId)

            if (!response.isSuccessful || response.body() == null) {
                documentDao.updateDownloadStatus(documentId, null, "FAILED", null)
                return@withContext Result.retry()
            }

            // Save PDF to internal storage /pdf_cache/
            val cacheDir = File(applicationContext.filesDir, "pdf_cache").apply { mkdirs() }
            val outputFile = File(cacheDir, "catalog_${documentId}.pdf")

            response.body()!!.byteStream().use { input ->
                FileOutputStream(outputFile).use { output ->
                    input.copyTo(output)
                }
            }

            documentDao.updateDownloadStatus(
                id = documentId,
                filePath = outputFile.absolutePath,
                status = "DOWNLOADED",
                timestamp = System.currentTimeMillis()
            )

            Result.success()
        } catch (e: Exception) {
            documentDao.updateDownloadStatus(documentId, null, "FAILED", null)
            Result.failure()
        }
    }
}
