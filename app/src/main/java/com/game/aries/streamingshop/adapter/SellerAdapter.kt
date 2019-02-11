package com.game.aries.streamingshop.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.game.aries.streamingshop.MainActivity
import com.game.aries.streamingshop.model.SellerItem
import kotlinx.android.synthetic.main.adapter_seller.view.*
import com.game.aries.streamingshop.R
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.model.UploadState
import com.game.aries.streamingshop.utilities.CommunicationManager
import com.game.aries.streamingshop.utilities.ItemTimer

class SellerAdapter (val context: Context, var sellerItemList: MutableList<SellerItem>, val listener: AdapterListener, var currentActivity: MainActivity):
    RecyclerView.Adapter<SellerAdapter.ViewHolder>() {

    interface AdapterListener{
        fun adapterSubscribeViewHolder(viewHolder: SellerAdapter.ViewHolder)
        fun adapterSetImage(viewHolder: SellerAdapter.ViewHolder)
        fun adapterDeleteItem(viewHolder: SellerAdapter.ViewHolder)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_seller, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.sellerItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(sellerItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), ItemTimer.TimerListener
    {
        private val sellerProductName = itemView.showProductName
        private val sellerProductPrice = itemView.showTotalProductPrice
        private val sellerProductDescription = itemView.sellerProductDescription
        private val sellerLifeTimeEditText = itemView.sellerLifeTimeEditText
        private val deleteButton = itemView.deleteButton
        private val sellerItemUploadButton = itemView.sellerItemUploadButton
        private val sellerBroadcastItemButton = itemView.sellerBroadcastItemButton
        private val showProductImage = itemView.showProductImage
        private val showRemainingTime = itemView.showRemainingTime

        private var nameEditableBackgroundDrawable : Drawable? = null
        private var priceEditableBackgroundDrawable : Drawable? = null
        private var descriptionEditableBackgroundDrawable : Drawable? = null
        private var lifetimeEditableBackgroundDrawable : Drawable? = null

        private var updateViewFlag = false
        private var isInitialized = false
        private var timerHolder : ItemTimer? = null

        fun bind(sellerItem: SellerItem) {
            // ToDo: need to refactor itemTimer :(
            if(timerHolder!= null){
                timerHolder?.removeListener()
                println("timer: remove listener")
            }

            timerHolder = sellerItem.itemTimer
            initViewHolder()
            refreshView(sellerItem)
            deleteButton.setOnClickListener{ clickDeleteItem() }
            sellerItemUploadButton.setOnClickListener{ clickSellerItemUploadButton(sellerItem) }
            sellerBroadcastItemButton.setOnClickListener { clickBroadcastItem(sellerItem)}
            showProductImage.setOnClickListener { listener.adapterSetImage(this)}
        }

        private fun modifyItemInfo(sellerItem:SellerItem){
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE){
                sellerItem.uploadState = UploadState.UPLOAD_MODIFIED
                buttonStateUpdate(sellerItem)
            }
        }

        private fun initViewHolder(){
            if (!isInitialized){
//                sellerProductName.setOnEditorActionListener { v, actionId, event ->  true}
                sellerProductPrice.setOnEditorActionListener { v, actionId, event ->  true}
//                sellerProductDescription.setOnEditorActionListener { v, actionId, event ->  true}
//                sellerLifeTimeEditText.setOnEditorActionListener { v, actionId, event ->  true}

                nameEditableBackgroundDrawable = sellerProductName.background
                priceEditableBackgroundDrawable = sellerProductPrice.background
                descriptionEditableBackgroundDrawable = sellerProductDescription.background
                lifetimeEditableBackgroundDrawable = sellerLifeTimeEditText.background
                // println("add one viewHolder: " + this.hashCode())
                sellerProductName.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if(!updateViewFlag) {
                            sellerItemList[adapterPosition].name = sellerProductName.editableText.toString()
                            modifyItemInfo(sellerItemList[adapterPosition])
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                sellerProductPrice.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if(!updateViewFlag) {
                            if (sellerProductPrice.editableText.toString() != "")
                                sellerItemList[adapterPosition].price = sellerProductPrice.editableText.toString().toInt()
                            else sellerItemList[adapterPosition].price = 0
                            modifyItemInfo(sellerItemList[adapterPosition])
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                sellerProductDescription.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if(!updateViewFlag) {
                            sellerItemList[adapterPosition].description = sellerProductDescription.editableText.toString()
                            modifyItemInfo(sellerItemList[adapterPosition])
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                sellerLifeTimeEditText.addTextChangedListener(object: TextWatcher {
                    override fun afterTextChanged(s: Editable?) {
                        if(!updateViewFlag) {
                            if (sellerLifeTimeEditText.editableText.toString() != "")
                                sellerItemList[adapterPosition].life_time = sellerLifeTimeEditText.editableText.toString().toInt() * 60
                            else sellerItemList[adapterPosition].life_time = 0

                            modifyItemInfo(sellerItemList[adapterPosition])
                        }
                    }
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                })
                listener.adapterSubscribeViewHolder(this)
                isInitialized = true
            }
        }

        private fun refreshView(sellerItem: SellerItem){
            updateViewFlag = true
            sellerProductName.setText(sellerItem.name)
            sellerProductPrice.setText(priceProcessor(sellerItem))
            sellerProductDescription.setText(sellerItem.description)
            sellerLifeTimeEditText.setText(lifeTimeProcessor(sellerItem))
            updateViewFlag = false
            buttonStateUpdate(sellerItem)
        }

        fun buttonStateUpdate(sellerItem: SellerItem){
            lockEditableText(false)
            //lockEditableText(false)
            showProductImage.setImageResource(R.drawable.ic_add_a_photo_white_24dp)
            sellerItemUploadButton.text = "新增"
            sellerBroadcastItemButton.text = "開賣"

            sellerBroadcastItemButton.visibility = View.INVISIBLE
            deleteButton.visibility = View.VISIBLE
            sellerItemUploadButton.visibility = View.VISIBLE
            if(sellerItem.picture!="") updateImage(sellerItem)
            if(sellerItem.uploadState != UploadState.ITEM_SELLING_START){
                showRemainingTime.text = "分鐘"
                sellerLifeTimeEditText.visibility = View.VISIBLE
            }

            when(sellerItem.uploadState){
                UploadState.UPLOAD_MODIFIED->sellerItemUploadButton.text = "修改"
                UploadState.UPLOAD_DONE->{
                    sellerItemUploadButton.text = "已儲存"
                    if(MainModel.isBroadcasting) sellerBroadcastItemButton.visibility = View.VISIBLE
                }
                UploadState.ITEM_SELLING_START->{
                    lockEditableText(true)
                    deleteButton.visibility = View.INVISIBLE
                    sellerItemUploadButton.visibility = View.INVISIBLE
                    sellerBroadcastItemButton.visibility = View.VISIBLE
                    sellerBroadcastItemButton.text = "停賣"

                    sellerLifeTimeEditText.visibility = View.INVISIBLE

                    if(sellerItem.itemTimer!=null){
                        showItemTime(sellerItem.itemTimer!!.secondsCount)
                        sellerItem.itemTimer!!.setListener(this)
                        timerHolder = sellerItem.itemTimer
                        println("timer: set listener")
                    }else{
                        showRemainingTime.text = "∞"
                    }
                }
                UploadState.ITEM_SELLING_END->{
                    lockEditableText(true)
                    deleteButton.visibility = View.INVISIBLE
                    sellerItemUploadButton.visibility = View.INVISIBLE
                    sellerBroadcastItemButton.visibility = View.INVISIBLE
                    sellerLifeTimeEditText.visibility = View.INVISIBLE
                    showRemainingTime.text = "已到期"
                    sellerItem.itemTimer?.removeListener()
                    sellerItem.itemTimer = null
                }
            }
        }

        private fun showItemTime(time:Int){
            if(time<60){
                showRemainingTime.text = time.toString() + "秒"
            }else{
                showRemainingTime.text = (time/60).toString() + "分" + (time%60).toString() + "秒"
            }
        }

        private fun lockEditableText(isLocked:Boolean){
            if(isLocked){
                sellerProductName.setOnTouchListener { _,_ ->  true}
                sellerProductPrice.setOnTouchListener { _,_ ->  true}
                sellerProductDescription.setOnTouchListener { _,_ ->  true}
                sellerLifeTimeEditText.setOnTouchListener { _,_ ->  true}
                sellerProductName.background = null
                sellerProductPrice.background = null
                sellerProductDescription.background = null
                sellerLifeTimeEditText.background = null

//                sellerProductName.isEnabled =false
//                sellerProductPrice.isEnabled =false
//                sellerProductDescription.isEnabled =false
//                sellerLifeTimeEditText.isEnabled =false

                sellerProductName.isFocusable = false
                sellerProductName.isFocusableInTouchMode = false
                sellerProductPrice.isFocusable = false
                sellerProductPrice.isFocusableInTouchMode = false
                sellerProductDescription.isFocusable = false
                sellerProductDescription.isFocusableInTouchMode = false
                sellerLifeTimeEditText.isFocusable = false
                sellerLifeTimeEditText.isFocusableInTouchMode = false

                sellerProductName.clearFocus()
                sellerProductPrice.clearFocus()
                sellerProductDescription.clearFocus()
                sellerLifeTimeEditText.clearFocus()
            }
            else{
                sellerProductName.setOnTouchListener { _,_ ->  false}
                sellerProductPrice.setOnTouchListener { _,_ ->  false}
                sellerProductDescription.setOnTouchListener { _,_ ->  false}
                sellerLifeTimeEditText.setOnTouchListener { _,_ ->  false}

                sellerProductName.background = nameEditableBackgroundDrawable
                sellerProductPrice.background = priceEditableBackgroundDrawable
                sellerProductDescription.background = descriptionEditableBackgroundDrawable
                sellerLifeTimeEditText.background = lifetimeEditableBackgroundDrawable

//                sellerProductName.isEnabled =true
//                sellerProductPrice.isEnabled =true
//                sellerProductDescription.isEnabled =true
//                sellerLifeTimeEditText.isEnabled =true

                sellerProductName.isFocusable = true
                sellerProductName.isFocusableInTouchMode = true
                sellerProductPrice.isFocusable = true
                sellerProductPrice.isFocusableInTouchMode = true
                sellerProductDescription.isFocusable = true
                sellerProductDescription.isFocusableInTouchMode = true
                sellerLifeTimeEditText.isFocusable = true
                sellerLifeTimeEditText.isFocusableInTouchMode = true


            }
        }

        private fun priceProcessor(sellerItem: SellerItem): String{
            return when(sellerItem.price!=0){
                true->sellerItem.price.toString()
                false->{
                    if (sellerItem.uploadState==UploadState.UPLOAD_DONE)  "0"
                    else  ""
                }
            }
        }

        private fun lifeTimeProcessor(sellerItem: SellerItem): String{
            return when(sellerItem.life_time!=0){
                true->(sellerItem.life_time/60).toString()
                false->{
                    if (sellerItem.uploadState==UploadState.UPLOAD_DONE)  "0"
                    else  ""
                }
            }
        }

        fun updateImage(sellerItem: SellerItem){
//            println("set viewholder image!")
//            println(sellerItem)
            if(sellerItem.uploadState == UploadState.UPLOAD_NOT_YET){
                val imageUri = Uri.parse(sellerItem.picture)
                showProductImage.setImageURI(imageUri)
            }
        }

        private fun clickSellerItemUploadButton(sellerItem: SellerItem){
            when(sellerItem.uploadState){
                UploadState.UPLOAD_NOT_YET->uploadNewItem(sellerItem)
                UploadState.UPLOAD_MODIFIED->uploadModifiedItem(sellerItem)
                else->return
            }
        }

        private fun clickDeleteItem(){
            listener.adapterDeleteItem(this)
        }

        fun deleteItem(sellerItem: SellerItem){
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE ||
                sellerItem.uploadState==UploadState.UPLOAD_MODIFIED){
                val cManager = CommunicationManager()
                cManager.loadingMessage = "Deleting"
                cManager.communication = { p0,p1->
                    MainModel.deleteSellerItem(p0,p1,sellerItem)
                }
                cManager.customCallback={
                    sellerItemList.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                }
                cManager.commit(currentActivity)
            }else{
                sellerItemList.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }
        }

        fun uploadNewItem(sellerItem: SellerItem){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Uploading"
            val imageUri = Uri.parse(sellerItem.picture)
            cManager.communication = { p0,p1->
                MainModel.addSellerItem(p0,p1,sellerItem)
            }
            cManager.customCallback = {
                if(sellerItem.uploadState==UploadState.UPLOAD_DONE){
                    refreshView(sellerItem)
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }

        fun uploadModifiedItem(sellerItem: SellerItem){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Uploading"
            val imageUri = Uri.parse(sellerItem.picture)
            cManager.communication = { p0,p1->
                MainModel.modifySellerItem(p0,p1,sellerItem)
            }
            cManager.customCallback = {
                if(sellerItem.uploadState == UploadState.UPLOAD_DONE){
                    refreshView(sellerItem)
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }

        private fun clickBroadcastItem(sellerItem: SellerItem){

            when(sellerItem.uploadState){
                UploadState.UPLOAD_DONE->if(MainModel.isBroadcasting)startBroadcastItem(sellerItem)
                UploadState.ITEM_SELLING_START->stopBroadcastItem(sellerItem)
                else->return
            }
        }

        fun startBroadcastItem(sellerItem: SellerItem){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Uploading"
            cManager.communication = { p0,p1->
                MainModel.startSellingSellerItem(p0,p1,sellerItem)
            }
            cManager.customCallback = {
                if(sellerItem.uploadState == UploadState.ITEM_SELLING_START){
                    if(sellerItem.life_time>0){
                        sellerItem.itemTimer = ItemTimer()
                        sellerItem.itemTimer!!.setTime(sellerItem.life_time)
                            .setListener(this)
                            .setItemListener(sellerItem)
                            .start()
                    }
                    refreshView(sellerItem)
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }

        override fun timerOnUpdate(time: Int) {
            showItemTime(time)
        }

        override fun timesUp(sellerItem:SellerItem) {
            refreshView(sellerItem)
        }

        fun stopBroadcastItem(sellerItem: SellerItem){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Uploading"
            cManager.communication = { p0,p1->
                MainModel.stopSellingSellerItem(p0,p1,sellerItem)
            }
            cManager.customCallback = {
                if(sellerItem.uploadState == UploadState.ITEM_SELLING_END){
                    if(sellerItem.itemTimer!=null){
                        sellerItem.itemTimer?.stop()
                    }
                    refreshView(sellerItem)
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }

        fun subscriberTest(){
            println("I'm subscriber " + this.hashCode() + " " + adapterPosition)
        }
    }
}