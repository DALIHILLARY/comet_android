package ug.hix.ratcomet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ug.hix.ratcomet.util.CometUtil

@Entity
data class LocationModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val imei: String = CometUtil.getImei(),
    val latitude: Double,
    val longitude: Double,
    val date : String = CometUtil.currentDateTime()
)