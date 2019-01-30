package com.game.aries.streamingshop.adapter

import android.content.Context
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

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val sellerProductName = itemView.showProductName
        private val sellerProductPrice = itemView.showTotalProductPrice
        private val sellerProductDescription = itemView.sellerProductDescription
        private val sellerLifeTimeEditText = itemView.sellerLifeTimeEditText
        private val deleteButton = itemView.deleteButton
        private val sellerItemUploadButton = itemView.sellerItemUploadButton
        private val sellerBroadcastItemButton = itemView.sellerBroadcastItemButton
        private val showProductImage = itemView.showProductImage
        var updateViewFlag = false
        var isInitialized = false

        fun bind(sellerItem: SellerItem) {
            initView(sellerItem)
            deleteButton.setOnClickListener{ clickDeleteItem() }
            sellerItemUploadButton.setOnClickListener{ clickSellerItemUploadButton(sellerItem) }
            sellerBroadcastItemButton.setOnClickListener { clickBroadcastItem(sellerItem)}
            showProductImage.setOnClickListener { listener.adapterSetImage(this)}
            // only add listener one time
            if (!isInitialized){
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

        private fun modifyItemInfo(sellerItem:SellerItem){
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE){
                sellerItem.uploadState = UploadState.UPLOAD_MODIFIED
                buttonStateUpdate(sellerItem)
            }
        }

        private fun initView(sellerItem: SellerItem){
            updateViewFlag = true
            sellerProductName.setText(sellerItem.name)
            sellerProductPrice.setText(priceProcessor(sellerItem))
            sellerProductDescription.setText(sellerItem.description)
            sellerLifeTimeEditText.setText(lifeTimeProcessor(sellerItem))
            updateViewFlag = false
            buttonStateUpdate(sellerItem)
        }

        fun buttonStateUpdate(sellerItem: SellerItem){
            showProductImage.setImageResource(R.drawable.ic_add_a_photo_white_24dp)
            sellerItemUploadButton.text = "新增"
            sellerBroadcastItemButton.visibility = View.INVISIBLE
            if(sellerItem.picture!="") updateImage(sellerItem)
            if(sellerItem.uploadState==UploadState.UPLOAD_MODIFIED) sellerItemUploadButton.text = "修改"
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE) {
                sellerItemUploadButton.text = "已儲存"
                if(MainModel.isBroadcasting) sellerBroadcastItemButton.visibility = View.VISIBLE
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
                    initView(sellerItem)
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
                    initView(sellerItem)
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }

        fun clickBroadcastItem(sellerItem: SellerItem){
            if(!MainModel.isBroadcasting) return


        }

        fun subscriberTest(){
            println("I'm subscriber " + this.hashCode() + " " + adapterPosition)
        }
    }
}