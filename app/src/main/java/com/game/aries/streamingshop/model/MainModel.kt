package com.game.aries.streamingshop.model

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences

object MainModel {

    var isLoading = false
    fun checkIsLoading():Boolean{
        return (isLoading)
    }

    private lateinit var sharedPreferences: SharedPreferences

    // for seller page
    private val SF_SELLER = "SF_SELLER"
    private val SF_BROADCAST_NAME = "SF_BROADCAST_NAME"

    var broadcastName = "我的直播"
    var broadcastID = ""
    fun saveSellerInfo(activity: ContextWrapper){
        sharedPreferences = activity.getSharedPreferences(SF_SELLER, Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(SF_BROADCAST_NAME, broadcastName).apply()
    }

    fun loadSellerInfo(activity: ContextWrapper){
        sharedPreferences = activity.getSharedPreferences(SF_SELLER, Context.MODE_PRIVATE)
        broadcastName = sharedPreferences.getString(SF_BROADCAST_NAME,"我的直播")!!
    }



}