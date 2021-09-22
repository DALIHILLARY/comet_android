package ug.hix.ratcomet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InitialConn(
    @PrimaryKey
    val name : String = "",
    val imei : String = "",
    val model : String = "",
    val token : String = "",
    val token_valid : Boolean = false,
    val version : String = "0.1"
)