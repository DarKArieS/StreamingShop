package com.game.aries.streamingshop.model

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.text.format.DateFormat
import okhttp3.*
import org.json.JSONObject
import java.io.File
import java.io.IOException
import com.game.aries.streamingshop.utilities.CommunicationManager
import java.util.*

object MainModel {
    var tmpExternalFile : File? = null
    const val backendUrl = "http://52.193.236.190/"

    enum class MainActivityLifeCycle {
        RUNNING, PAUSE
    }

    var mainActivityStatus = MainActivityLifeCycle.RUNNING

    var isLoading = false
    fun checkIsLoading():Boolean{
        return (isLoading)
    }

    private lateinit var sharedPreferences: SharedPreferences

//===================================================================================================
// for seller page
//===================================================================================================
    private val SF_SELLER = "SF_SELLER"
    private val SF_BROADCAST_NAME = "SF_BROADCAST_NAME"

    val sellerBroadcast = Broadcast("我的直播拍賣","")

    fun saveSellerInfo(activity: ContextWrapper){
        sharedPreferences = activity.getSharedPreferences(SF_SELLER, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SF_BROADCAST_NAME, sellerBroadcast.broadcastName).apply()
    }

    fun loadSellerInfo(activity: ContextWrapper){
        sharedPreferences = activity.getSharedPreferences(SF_SELLER, Context.MODE_PRIVATE)
        sellerBroadcast.broadcastName = sharedPreferences.getString(SF_BROADCAST_NAME,"我的直播")!!
    }

    lateinit var sellerItemList : MutableList<SellerItem>

    var ttnStreamingID : Int = 0
    fun ttnCreateBroadcast(successCallBack: ()->Unit, failureCallBack: ()->Unit,
                           arguments: CommunicationManager.CommunicationArguments?=null
                           ) {
        val time = Calendar.getInstance().time
        val date = DateFormat.format("yyyy-MM-dd",time).toString()

        val multiBody = MultipartBody.Builder()
        multiBody.setType(MultipartBody.FORM)
        multiBody.addFormDataPart("title", "XD streaming")
        multiBody.addFormDataPart("url", "https://www.facebook.com/facebook/videos/2487357224613465")
        multiBody.addFormDataPart("played_at", date)
        val requestBody = multiBody.build() as RequestBody
//        val url = Resources.getSystem().getString(R.string.URL) + "api/streams"
//        println(url)
        val request = Request.Builder().url(backendUrl + "api/streams")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                successCallBack()
                ttnStreamingID = readJSON.getJSONObject("data").getInt("id")
            }
        })

    }

    fun uploadPhoto(successCallBack: ()->Unit, failureCallBack: ()->Unit,
                    arguments: CommunicationManager.UploadPhoto){
        val file = File(arguments.uri?.path)
        val mediaType = MediaType.parse("image/png")
        val multiBody = MultipartBody.Builder()

        multiBody.setType(MultipartBody.FORM)
        multiBody.addFormDataPart("stream_id", ttnStreamingID.toString())
        multiBody.addFormDataPart("name", "myProduct")
        multiBody.addFormDataPart("price", "100")
        multiBody.addFormDataPart("picture", file.name, RequestBody.create(mediaType, file))

        val requestBody = multiBody.build() as RequestBody

//        val request = Request.Builder().url(R.string.URL.toString() + "api/merchandises")
        val request = Request.Builder().url(backendUrl + "api/merchandises")
            .addHeader("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val client: OkHttpClient = OkHttpClient().newBuilder().build()
        val call = client.newCall(request)
        call.enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }
            override fun onResponse(call: Call?, response: Response?) {
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                successCallBack()
            }
        })
    }

//===================================================================================================
// for buyer page
//===================================================================================================
    var broadcastWatching = Broadcast("","")



}