package com.skgames.t

import android.app.Application
import com.my.tracker.MyTracker
import com.onesignal.OneSignal
import com.orhanobut.hawk.Hawk
import java.util.*


class MClass : Application() {

    companion object {
        const val strtr = "01686395101357038059"
        const val os = "afac0527-c946-4d4b-91e8-a0bdaaeebccd"

        var appsCheck = "appsChecker"
        var C1: String? = "c11"
        var myID: String? = "myID"
        var instId: String? = "instID"
        var link = "link"
        var MAIN_ID: String? = ""
    }

    override fun onCreate() {
        super.onCreate()

        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(this)
        OneSignal.setAppId(os)

        Hawk.init(this).build()

        val settings = getSharedPreferences("PREFS_NAME", 0)
        val trackerParams = MyTracker.getTrackerParams()
        val trackerConfig = MyTracker.getTrackerConfig()

        val instID = MyTracker.getInstanceId(this)
        trackerConfig.isTrackingLaunchEnabled = true

        if (settings.getBoolean("my_first_time", true)) {
            val IDIN = UUID.randomUUID().toString()
            trackerParams.setCustomUserId(IDIN)
            Hawk.put(myID, IDIN)
            Hawk.put(instId, instID)
            settings.edit().putBoolean("my_first_time", false).apply()

        } else {
            val IDIN = Hawk.get(myID, "null")
            trackerParams.setCustomUserId(IDIN)
        }
        MyTracker.initTracker(strtr, this)
    }
}
