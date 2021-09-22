package ug.hix.ratcomet.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ug.hix.ratcomet.model.*

@Database(entities = [HostModel::class, LocationModel::class, InitialConn::class, SocialAppS::class, SocialApp::class,SmsModel::class,BrowserModel::class],version = 1,exportSchema = false)
abstract class CometDatabase : RoomDatabase() {
    abstract fun databaseDao() : DatabaseDao

    companion object {
        private var instance : CometDatabase? = null
        @Synchronized fun dbInstance(context: Context?): CometDatabase {
            if(instance == null){
                instance = Room.databaseBuilder(context!!.applicationContext, CometDatabase::class.java,"comet.db")
                    .build()
            }
            return instance as CometDatabase
        }
    }
}