package ug.hix.ratcomet.model

import androidx.room.Entity

@Entity(primaryKeys = ["command","time"])
class Command(
    val command: String,
    val time : String
)