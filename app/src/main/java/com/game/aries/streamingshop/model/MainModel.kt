package com.game.aries.streamingshop.model

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import com.facebook.AccessToken
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

object MainModel {
    var tmpExternalFile : File? = null
    private const val backendUrl = "https://livevideoseller.sckao.space/"
//    private const val backendUrl = "https://f3171659.ngrok.io/"

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
// for login page
//===================================================================================================

    var isFirstLogin = false
    var userInfo = UserInfo(0,"","","")

    fun checkIsFirstLogin(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token",AccessToken.getCurrentAccessToken().token)
            .put("expired_time", (AccessToken.getCurrentAccessToken().expires.time/1000).toString())
            .put("device","Android").toString()

        println(myJSON)

        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)

//        val requestBody: RequestBody =
//            MultipartBody.Builder()
//            .setType(MultipartBody.FORM)
//            .addFormDataPart("token", AccessToken.getCurrentAccessToken().token)
//            .addFormDataPart("expired_time", (AccessToken.getCurrentAccessToken().expires.time/1000).toString())
//            .addFormDataPart("device", "Android")
//            .build()

        val request =
            Request.Builder().url(backendUrl + "api/login")
            .addHeader("Content-Type", "application/json")
            .post(myJSONRequestBody)
            .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result") == "True"){
                    val userInfoJSON = JSONObject(readJSON.getString("response"))
                    //println(userInfoJSON.getString("name"))
                    isFirstLogin = (userInfoJSON.getString("phone")=="null")
                    if(!isFirstLogin){
                        userInfo.id = userInfoJSON.getString("id").toInt()
                        userInfo.name = userInfoJSON.getString("name")
                        userInfo.phone = userInfoJSON.getString("phone")
                        userInfo.address = userInfoJSON.getString("address")
                    }else{
                        println("is First Login!")
                    }
                }
                successCallBack()
            }
        })
    }

    fun putUserInfo(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        println(userInfo.name)
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("name", userInfo.name)
            .put("phone", userInfo.phone)
            .put("address", userInfo.address)
            .toString()

        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)

//        val requestBody: RequestBody =
//            MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("token", AccessToken.getCurrentAccessToken().token)
//                .addFormDataPart("name", userInfo.name)
//                .addFormDataPart("phone", userInfo.phone)
//                .addFormDataPart("address", userInfo.address)
//                .build()

        val request =
            Request.Builder().url(backendUrl + "api/user/update")
                .addHeader("Content-Type", "application/json")
//                .put(requestBody)
                .put(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                //val readJSON = JSONObject(response!!.body()!!.string())
                //println(readJSON)
                println(response!!.body()!!.string())
                successCallBack()
            }
        })
    }

//===================================================================================================
// for seller page
//===================================================================================================
    private const val SF_SELLER = "SF_SELLER"
    private const val SF_BROADCAST_NAME = "SF_BROADCAST_NAME"

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

    fun getSellerItemList(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .toString()
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/preparelist")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("get seller item list")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    sellerItemList = mutableListOf()
                    val dataJsonArray = JSONArray(readJSON.getString("response"))
                    for (i in 0 until dataJsonArray.length()-1 ){
                        val dataJson = JSONObject(dataJsonArray[i].toString())
                        sellerItemList.add(0,
                            SellerItem(
                                dataJson.getString("name"),
                                dataJson.getInt("price"),
                                dataJson.getString("description"),
                                dataJson.getString("picture"),
                                true,
                                dataJson.getInt("id")
                            )
                        )
                    }
                }
                successCallBack()
            }
        })
    }

    fun addSellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("name",sellerItem.name)
            .put("description",sellerItem.description)
            .put("price",sellerItem.price.toString())
            .put("picture","123.png")
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("product",productJSON)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/set")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("set seller item list")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    val responseJSON = JSONObject(readJSON.getString("response"))
                    sellerItem.isUploaded = true
                    sellerItem.uploadedID = responseJSON.getInt("id")
                }
                successCallBack()
            }
        })
    }

//===================================================================================================
// for buyer page
//===================================================================================================
    var broadcastWatching = Broadcast("","")



}