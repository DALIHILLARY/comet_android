package ug.hix.ratcomet.util

import android.annotation.SuppressLint
import android.content.Context
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson
import ug.hix.ratcomet.Accessibility
import ug.hix.ratcomet.Phone
import ug.hix.ratcomet.Repository
import ug.hix.ratcomet.model.InitialConn
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CometUtil {
    companion object {
        @SuppressLint("SimpleDateFormat")
        fun currentDateTime(): String{
            val calender = Calendar.getInstance()
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return format.format(calender.time)
        }
        @SuppressLint("SimpleDateFormat")
        fun dateTime(time : Long) : String {
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            return format.format(Date(time))
        }

        fun randomUUID() : String {
            return UUID.randomUUID().toString()
        }
        fun sanitizePhone(phone: String) : String {
            val number = phone.replace(" ","")
            return if( number.startsWith("0")){
                number.replaceFirst("0","+256")
            }else number
        }
        private fun isConnected() : Boolean {
            var result = false
            try {
                val url = URL("https://ratcomet.com")
                val connection  = url.openConnection()
                connection.connect()
                result = true
            } catch (e: Exception) {
//                Log.e("Comet","FAILED TO CONNECT TO SERVER",e)
            }
//            Log.e("Connection", "$result")

            return result
        }
        fun isActiveToken(repo : Repository) : String {
            var result = "Not Connected"
            if(isConnected()) {
                val token = repo.getInitialConn()
                token?.let {
                    val request = "https://ratcomet.com/api/token_active".httpPost()
                        .jsonBody(Gson().toJson(it))
                        .responseString()
                    result = String(request.second.data)
//                    Log.d("Comet","token active: $result")
                }
            }
            return result

        }
        fun isValidToken(repo : Repository) : String {
            var result = "Not Connected"
            if(isConnected()) {
                val token = repo.getInitialConn()
                token?.let {
                    val request = "https://ratcomet.com/api/token_valid".httpPost()
                        .jsonBody(Gson().toJson(it))
                        .responseString()

                    result = String(request.second.data)
//                    Log.d("Comet","token validity:  $result")
                }
//                Log.d("InitialConn", "$token")
            }
            return result

        }
        fun checkValidity(token : String, context: Context) : String {
            var result = "Not Connected"
            val phoneDetails = Phone(context).getPhoneDetails()
            if(isConnected()) {
                val details = InitialConn(imei = phoneDetails.first, token = token)
                details.let {
                    val request = "https://ratcomet.com/api/token_valid".httpPost()
                        .jsonBody(Gson().toJson(it))
                        .responseString()

                    result = String(request.second.data)
//                    Log.d("Comet","token validity:  $result")
                }
//                Log.d("InitialConn", "$details")
            }
            return result
        }
        fun getImei() : String {
            var imei = Accessibility.phoneImei as String?
            if(imei == null) {
                imei = Repository(null).getInitialConn()!!.imei
            }
            return imei
        }
    }
}