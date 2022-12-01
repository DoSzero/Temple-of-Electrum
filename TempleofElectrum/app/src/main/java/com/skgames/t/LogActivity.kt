package com.skgames.t

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.appsflyer.AppsFlyerLib
import com.onesignal.OneSignal
import com.orhanobut.hawk.Hawk
import com.skgames.t.MClass.Companion.C1
import com.skgames.t.MClass.Companion.MAIN_ID
import com.skgames.t.MClass.Companion.link
import com.skgames.t.databinding.ActivityItisBinding
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException

class LogActivity : AppCompatActivity() {

    var str: String? = null
    var uricall: ValueCallback<Array<Uri>>? = null
    private val one = 1

    lateinit var view1: WebView
    lateinit var binding: ActivityItisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityItisBinding.inflate(layoutInflater)
        setContentView(binding.root)
        view1 = binding.vwvw

        val cm = CookieManager.getInstance()
        cm.setAcceptCookie(true)
        cm.setAcceptThirdPartyCookies(view1, true)
        webSettings()

        val activity: Activity = this
        view1.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                try {
                    if (URLUtil.isNetworkUrl(url)) {
                        return false
                    }
                    if (appInstalledOrNot(url)) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(url)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, "Application is not installed", Toast.LENGTH_LONG).show()
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=org.telegram.messenger")))
                    }
                    return true
                } catch (e: Exception) {
                    return false
                }
                view.loadUrl(url)
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                saveUrl(url)
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
            }
        }

        view1.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView, filePathCallback:ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams): Boolean {

                uricall?.onReceiveValue(null)
                uricall = filePathCallback

                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", str)
                    } catch (ex: IOException) {
                        // Error occurred while creating the File
                    }
                    if (photoFile != null) {
                        str = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                    } else {
                        takePictureIntent = null
                    }
                }

                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.type = "image/*"

                val intentArray: Array<Intent?> = takePictureIntent?.let { arrayOf(it) } ?: arrayOfNulls(0)
                val chooserIntent = Intent(Intent.ACTION_CHOOSER)

                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, getString(R.string.image_chooser))
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)

                startActivityForResult(chooserIntent, one)
                return true
            }

            @Throws(IOException::class)
            private fun createImageFile(): File {
                var imageStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DirectoryNameHere")
                if (!imageStorageDir.exists()) {
                    imageStorageDir.mkdirs()
                }

                imageStorageDir = File(imageStorageDir.toString() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg")
                return imageStorageDir
            }
        }
        view1.loadUrl(ur())
    }

    private fun pushToOneSignal(string: String) {
        OneSignal.setExternalUserId(string, object : OneSignal.OSExternalUserIdUpdateCompletionHandler {
            override fun onSuccess(results: JSONObject) {
                try {
                    if (results.has("push") && results.getJSONObject("push").has("success")) {
                        val isPushSuccess = results.getJSONObject("push").getBoolean("success")
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for push status: $isPushSuccess")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                try {
                    if (results.has("email") && results.getJSONObject("email").has("success")) {
                        val isEmailSuccess = results.getJSONObject("email").getBoolean("success")
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for email status: $isEmailSuccess")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                try {
                    if (results.has("sms") && results.getJSONObject("sms").has("success")) {
                        val isSmsSuccess = results.getJSONObject("sms").getBoolean("success")
                        OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id for sms status: $isSmsSuccess")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(error: OneSignal.ExternalIdError) {
                OneSignal.onesignalLog(OneSignal.LOG_LEVEL.VERBOSE, "Set external user id done with error: $error")
            }
        })
    }

    private fun webSettings() {
        val view1Set = view1.settings
        view1Set.javaScriptEnabled = true

        view1Set.useWideViewPort = true

        view1Set.loadWithOverviewMode = true
        view1Set.allowFileAccess = true
        view1Set.domStorageEnabled = true
        view1Set.userAgentString = view1Set.userAgentString.replace("; wv", "")

        view1Set.javaScriptCanOpenWindowsAutomatically = true
        view1Set.setSupportMultipleWindows(false)

        view1Set.displayZoomControls = false
        view1Set.builtInZoomControls = true
        view1Set.setSupportZoom(true)

        view1Set.pluginState = WebSettings.PluginState.ON
        view1Set.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        view1Set.setAppCacheEnabled(true)

        view1Set.allowContentAccess = true
    }

    @SuppressLint("SuspiciousIndentation")
    private fun ur(): String {
        val spoon = getSharedPreferences("SP_WEBVIEW_PREFS", MODE_PRIVATE)
        val pack = "com.skgames.t"

        val myTrID: String? = Hawk.get(MClass.myID, "null")
        val myInstId: String? = Hawk.get(MClass.instId, "null")
        val cpOne:String? = Hawk.get(C1)
        val mainId: String? = Hawk.get(MAIN_ID, "null")

        val checkFly: String = Hawk.get(MClass.appsCheck)
        val afId = AppsFlyerLib.getInstance().getAppsFlyerUID(this)

        AppsFlyerLib.getInstance().setCollectAndroidID(true)

        val one = "deviceID="
        val subOne = "sub_id_1="
        val dad = "ad_id="
        val sub4 = "sub_id_4="
        val sub5 = "sub_id_5="
        val sub6 = "sub_id_6="

        val strN = "naming"

        val bvr = Build.VERSION.RELEASE
        val linkAB: String = Hawk.get(link)

        var aft = ""
        if (checkFly == "1"){
        aft = "$linkAB$subOne$cpOne&$one$afId&$dad$mainId&$sub4$pack&$sub5$bvr&$sub6$strN"
            pushToOneSignal(afId.toString())
        } else {
            aft = "$linkAB$one$myTrID&$dad$myInstId&$sub4$pack&$sub5$bvr&$sub6$strN"
            pushToOneSignal(myTrID.toString())
        }
        return spoon.getString("SAVED_URL", aft).toString()
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        try {
            packageManager.getPackageInfo("org.telegram.messenger", PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) { }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != one || uricall == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }
        var results: Array<Uri>? = null
        if (resultCode == RESULT_OK) {
            if (data == null || data.data == null) {
                results = arrayOf(Uri.parse(str))
            } else {
                val dataString = data.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }
        }
        uricall?.onReceiveValue(results)
        uricall = null
    }


    private var exitexitexitexit = false
    override fun onBackPressed() {
        if (view1.canGoBack()) {
            if (exitexitexitexit) {
                view1.stopLoading()
                view1.loadUrl(urlfi)
            }
            this.exitexitexitexit = true
            view1.goBack()
            Handler(Looper.getMainLooper()).postDelayed(Runnable {
                exitexitexitexit = false
            }, 2000)
        } else {
            super.onBackPressed()
        }
    }

    var urlfi = ""
    fun saveUrl(strlurl: String?) {
        if (!strlurl!!.contains("t.me")) {
            if (urlfi == "") {
                urlfi = getSharedPreferences("SP_WEBVIEW_PREFS",MODE_PRIVATE).getString("SAVED_URL", strlurl).toString()
                val sp = getSharedPreferences("SP_WEBVIEW_PREFS", MODE_PRIVATE)
                val et = sp.edit()
                et.putString("SAVED_URL", strlurl)
                et.apply()
            }
        }
    }

}