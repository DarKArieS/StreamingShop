package com.game.aries.streamingshop.model

import android.net.Uri

data class SellerItem(var name:String="", var price:Int=0, var description:String="", var picture: String="", var isUploaded: Boolean=false, var uploadedID : Int=0)

//data class uploadSellerItem()

data class BuyerItem(var name:String, var price:Int, var description:String, var number: Int)

data class Broadcast(var broadcastName:String, var broadcastID:String)

data class UserInfo(var id: Int, var name:String, var phone:String, var address:String)