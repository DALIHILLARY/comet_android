package ug.hix.ratcomet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HostModel(
    @PrimaryKey val id : String = "c2c",
    val address : String,
    val port : Int
)