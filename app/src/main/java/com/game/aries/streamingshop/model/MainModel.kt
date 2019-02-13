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

    var sellerItemList = mutableListOf<SellerItem>()

    fun updateSellerItemList(successCallBack: ()->Unit, failureCallBack: ()->Unit){
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
                println("updateSellerItemList")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    sellerItemList = mutableListOf()
                    val dataJsonArray = JSONArray(readJSON.getString("response"))
                    for (i in 0 until dataJsonArray.length()){
                        val dataJson = JSONObject(dataJsonArray[i].toString())
                        sellerItemList.add(0,
                            SellerItem(
                                dataJson.getString("name"),
                                dataJson.getInt("price"),
                                dataJson.getString("description"),
                                dataJson.getString("picture"),
                                dataJson.getInt("life_time"),
                                UploadState.UPLOAD_DONE,
                                dataJson.getInt("id"),
                                ""
                            )
                        )
                    }
                    println(sellerItemList)
                }
                successCallBack()
            }
        })
    }

    fun getSellerItemList(successCallBack: ()->Unit, failureCallBack: ()->Unit, newSellerItemList:MutableList<SellerItem>)
    {
        newSellerItemList.clear()
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
//                    val newSellerItemList = mutableListOf<SellerItem>()
                    val dataJsonArray = JSONArray(readJSON.getString("response"))
                    for (i in 0 until dataJsonArray.length()){
                        val dataJson = JSONObject(dataJsonArray[i].toString())
                        newSellerItemList.add(0,
                            SellerItem(
                                dataJson.getString("name"),
                                dataJson.getInt("price"),
                                dataJson.getString("description"),
                                dataJson.getString("picture"),
                                dataJson.getInt("life_time"),
                                UploadState.UPLOAD_DONE,
                                dataJson.getInt("id"),
                                ""
                            )
                        )
                    }
                }
                successCallBack()
            }
        })
    }

    fun updateOldSellerItemList(newList: MutableList<SellerItem>, oldList: MutableList<SellerItem>){
        if(newList.size==0) return
        var oldIndex = 0
        while(true){
            if (oldList.size != 0 &&
                oldList[oldIndex].uploadState != UploadState.UPLOAD_NOT_YET &&
                oldList[oldIndex].uploadState != UploadState.ITEM_SELLING_START &&
                oldList[oldIndex].uploadState != UploadState.ITEM_SELLING_END){
                var updatedFlag = false
                for ((newIndex,new) in newList.withIndex()){
                    if (oldList[oldIndex].uploadedID == new.uploadedID){
                        if(oldList[oldIndex].uploadState==UploadState.UPLOAD_DONE){
                            oldList[oldIndex].name = new.name
                            oldList[oldIndex].description = new.description
                            oldList[oldIndex].picture = new.picture
                            oldList[oldIndex].price = new.price
                            oldList[oldIndex].life_time = new.life_time
                        }
                        newList.removeAt(newIndex)
                        updatedFlag = true
                        break
                    }
                }
                if (!updatedFlag){
                    oldList.removeAt(oldIndex)
                    oldIndex--
                }
            }
            oldIndex++
            if(oldIndex>=oldList.size) break
        }

        if(newList.size>0){
            println("update seller list: add some seller items back from server ...")
            for(new in newList) oldList.add(new)
        }
    }

    fun addSellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("name",sellerItem.name)
            .put("description",sellerItem.description)
            .put("price",sellerItem.price.toString())
            .put("picture","123.png")
            .put("life_time",sellerItem.life_time)
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
                println("set seller item")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    val responseJSON = JSONObject(readJSON.getString("response"))
                    sellerItem.uploadState = UploadState.UPLOAD_DONE
                    sellerItem.uploadedID = responseJSON.getInt("id")
                    sellerItem.uploadMessage = ""
                }else{
                    sellerItem.uploadState = UploadState.UPLOAD_NOT_YET
                    sellerItem.uploadMessage = readJSON.getString("response")
                }
                successCallBack()
            }
        })
    }

    fun modifySellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("id",sellerItem.uploadedID)
            .put("name",sellerItem.name)
            .put("description",sellerItem.description)
            .put("price",sellerItem.price.toString())
            .put("picture","123.png")
            .put("life_time",sellerItem.life_time)
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("product",productJSON)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/update")
                .addHeader("Content-Type", "application/json")
                .put(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("modify seller item list")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    sellerItem.uploadState = UploadState.UPLOAD_DONE
                    sellerItem.uploadMessage = ""
                }else{
                    sellerItem.uploadState = UploadState.UPLOAD_MODIFIED
                    sellerItem.uploadMessage = readJSON.getString("response")
                }
                successCallBack()
            }
        })
    }

    fun deleteSellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("id",sellerItem.uploadedID)
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("product",productJSON)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/delete")
                .addHeader("Content-Type", "application/json")
                .delete(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("delete seller item")
//                println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    successCallBack()
                }else{
                    failureCallBack()
                }
            }
        })
    }

    fun startSellerStream(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("live_video_id", sellerBroadcast.broadcastID)
            .put("title", sellerBroadcast.broadcastName)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/live_video/start")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("start stream")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    successCallBack()
                }else{
                    failureCallBack()
                }
            }
        })
    }

    fun endSellerStream(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("live_video_id", sellerBroadcast.broadcastID)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/live_video/stop")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("stop stream")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    successCallBack()
                }else{
                    failureCallBack()
                }
            }
        })
    }

    fun startSellingSellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("id",sellerItem.uploadedID)
            .put("life_time",sellerItem.life_time)
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("live_video_id", sellerBroadcast.broadcastID)
            .put("product",productJSON)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/sell")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("start selling seller item")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    sellerItem.uploadState = UploadState.ITEM_SELLING_START
                    sellerItem.sellingLiveVideoID = sellerBroadcast.broadcastID
                    sellerItem.uploadMessage = ""
                }else{
                    sellerItem.uploadMessage = readJSON.getString("response")
                }
                successCallBack()
            }
        })
    }

    fun stopSellingSellerItem(successCallBack: ()->Unit, failureCallBack: ()->Unit, sellerItem:SellerItem){
        val productJSON = JSONObject()
            .put("id",sellerItem.uploadedID)
            .put("life_time",sellerItem.life_time)
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("live_video_id", sellerItem.sellingLiveVideoID)
            .put("product",productJSON)
            .toString()
        println(myJSON)
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)
        val request =
            Request.Builder().url(backendUrl + "api/product/stopsell")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("start selling seller item")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                if(readJSON.getString("result")=="true"){
                    sellerItem.uploadState = UploadState.ITEM_SELLING_END
                    sellerItem.uploadMessage = ""
                }else{
                    sellerItem.uploadMessage = readJSON.getString("response")
                }
                successCallBack()
            }
        })
    }

    fun getSellerOrderList(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .toString()
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)

        val request =
            Request.Builder().url(backendUrl + "api/seller/orders")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("get seller order list")
                println(response!!.body()!!.string())
//                val readJSON = JSONObject(response!!.body()!!.string())
//                println(readJSON)
                successCallBack()
            }
        })
    }

    var isBroadcasting = false

//===================================================================================================
// for buyer page
//===================================================================================================
    var broadcastWatching = Broadcast("","")
    var broadcastList = mutableListOf<Broadcast>()
    var buyerItemList = mutableListOf<BuyerItem>()

    fun updateBroadcastList(successCallBack: ()->Unit, failureCallBack: ()->Unit)
    {
        val request =
            Request.Builder().url(backendUrl + "api/live_video/lists")
                .addHeader("Content-Type", "application/json")
                .get()
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("get broadcast list")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                // Note: if there is no one streaming, the result of response will be false!
                broadcastList = mutableListOf()
                if(readJSON.getString("result")=="True"){
                    val dataJsonArray = JSONArray(readJSON.getString("response"))
                    for (i in 0 until dataJsonArray.length()){
                        val dataJson = JSONObject(dataJsonArray[i].toString())
                        broadcastList.add(0,
                            Broadcast(
                                dataJson.getString("title"),
                                dataJson.getString("live_video_id")
                            )
                        )
                    }
                    println(broadcastList)
                }else{
                    // empty list
                }
                successCallBack()
            }
        })
    }

    fun updateBuyerItemList(successCallBack: ()->Unit, failureCallBack: ()->Unit)
    {
        // ToDo: if broadcastWatching is removed?
        val request =
            Request.Builder().url(backendUrl + "api/products/list/" + broadcastWatching.broadcastID)
                .addHeader("Content-Type", "application/json")
                .get()
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("get buyer item list")
                //println(response!!.body()!!.string())
                val readJSON = JSONObject(response!!.body()!!.string())
                println(readJSON)
                // Note: if there is no one streaming, the result of response will be false!
                buyerItemList = mutableListOf()
                if(readJSON.getString("result")=="True"){
                    val responseJson = JSONObject(readJSON.getString("response"))
                    val dataJsonArray = JSONArray(responseJson.getString("products"))
                    for (i in 0 until dataJsonArray.length()){
                        val dataJson = JSONObject(dataJsonArray[i].toString())
                        buyerItemList.add(0,
                            BuyerItem(
                                dataJson.getInt("id"),
                                dataJson.getString("name"),
                                dataJson.getInt("price"),
                                dataJson.getString("description"),
                                dataJson.getString("picture")
                            )
                        )
                    }
                }else{
                    // empty list
                }
                successCallBack()
            }
        })
    }

    fun sendBuyerOrder(successCallBack: ()->Unit, failureCallBack: ()->Unit, itemList:List<BuyerItem>)
    {
        val productJSONArray = JSONArray()
        for (i in itemList){
            val itemJSON = JSONObject()
                .put("id",i.id)
                .put("quantity",i.amount)
            productJSONArray.put(itemJSON)
        }

        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .put("live_video_id", broadcastWatching.broadcastID)
            .put("products", productJSONArray)
            .toString()
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)

        val request =
            Request.Builder().url(backendUrl + "api/orders/create")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("send buyer item list and pay")
                println(response!!.body()!!.string())
//                val readJSON = JSONObject(response!!.body()!!.string())
//                println(readJSON)
                successCallBack()
            }
        })
    }

    fun getBuyerOrderList(successCallBack: ()->Unit, failureCallBack: ()->Unit){
        val myJSON = JSONObject()
            .put("token", AccessToken.getCurrentAccessToken().token)
            .toString()
        val myJSONRequestBody = RequestBody.create(MediaType.get("application/json"),myJSON)

        val request =
            Request.Builder().url(backendUrl + "api/buyer/orders")
                .addHeader("Content-Type", "application/json")
                .post(myJSONRequestBody)
                .build()

        OkHttpClient().newBuilder().build().newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                failureCallBack()
            }

            override fun onResponse(call: Call?, response: Response?) {
                println("get buyer order list")
                println(response!!.body()!!.string())
//                val readJSON = JSONObject(response!!.body()!!.string())
//                println(readJSON)
                successCallBack()
            }
        })
    }
}