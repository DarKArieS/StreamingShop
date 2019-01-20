package com.game.aries.streamingshop.model

data class SellerItem(var name:String, var price:Int, var description:String)

data class BuyerItem(var name:String, var price:Int, var description:String, var number: Int)

data class Broadcast(var broadcastName:String, var broadcastID:String)