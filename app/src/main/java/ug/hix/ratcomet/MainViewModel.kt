package ug.hix.ratcomet

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ug.hix.ratcomet.model.InitialConn
import ug.hix.ratcomet.util.CometUtil

class MainViewModel : ViewModel() {
    val isTokenPhoneValid = MutableLiveData("")

    fun saveToken(token: String, name: String, context: Context) {
        val repo = Repository(context.applicationContext)
        val phoneDetails = Phone(context).getPhoneDetails()
        repo.insertInitialConn(InitialConn(token = token,name = name,imei = phoneDetails.first,model = phoneDetails.third))
    }
    fun checkValidity(token: String, context: Context) {
        viewModelScope.launch{
            val result = withContext(Dispatchers.IO) {
                try {
                    CometUtil.checkValidity(token,context)
                }catch (e: Exception){
                    ""
                }

            }
            isTokenPhoneValid.value = result
        }
    }

}