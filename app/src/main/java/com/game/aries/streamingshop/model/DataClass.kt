package com.game.aries.streamingshop.model

import android.net.Uri
import com.game.aries.streamingshop.utilities.ItemTimer

data class SellerItem(var name:String="", var price:Int=0, var description:String="", var picture: String="empty", var life_time: Int = 0,
                      var uploadState: UploadState=UploadState.UPLOAD_NOT_YET,
                      var uploadedID : Int=-1, var uploadMessage : String = "", var sellingLiveVideoID :String = "", var modifyTmp:SellerItem?=null,
                      var itemTimer: ItemTimer? = null)



enum class UploadState{
    UPLOAD_NOT_YET,
    UPLOAD_DONE,
    UPLOAD_MODIFIED,
    ITEM_SELLING_START,
    ITEM_SELLING_END
}


//data class uploadSellerItem()

data class BuyerItem(var id: Int, var name:String, var price:Int, var description:String, var picture:String = "", var amount: Int = 0)

data class BuyerOrderItem(var id: Int, var broadcast:Broadcast, var name:String, var price:Int, var description:String, var picture:String, var amount: Int )

data class Broadcast(var broadcastName:String, var broadcastID:String)

data class UserInfo(var id: Int, var name:String, var phone:String, var address:String)