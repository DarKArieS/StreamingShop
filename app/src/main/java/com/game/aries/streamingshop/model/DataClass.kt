package com.game.aries.streamingshop.model

import android.net.Uri

data class SellerItem(var name:String="", var price:Int=0, var description:String="", var picture: String="", val isUploaded: Boolean=false)

//data class uploadSellerItem()

data class BuyerItem(var name:String, var price:Int, var description:String, var number: Int)

data class Broadcast(var broadcastName:String, var broadcastID:String)