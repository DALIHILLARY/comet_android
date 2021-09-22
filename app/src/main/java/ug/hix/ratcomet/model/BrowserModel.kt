package ug.hix.ratcomet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import ug.hix.ratcomet.util.CometUtil

@Entity
data class BrowserModel(
    @PrimaryKey(autoGenerate = true)
    val id : Int = 0,
    val imei: String = CometUtil.getImei(),
    val modified: String = CometUtil.currentDateTime(),
    val browser : String,
    val search: String
    )