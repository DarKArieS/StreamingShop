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
        fun adapterSetImage(viewHolder: SellerAdapter.ViewHolder)
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
        private val deleteButton = itemView.deleteButton
        private val sellerItemUploadButton = itemView.sellerItemUploadButton
        private val showProductImage = itemView.showProductImage

        fun bind(sellerItem: SellerItem) {
            initEditText(sellerItem)
            deleteButton.setOnClickListener{ clickDeleteItem(sellerItem) }
            sellerItemUploadButton.setOnClickListener{ clickSellerItemUploadButton(sellerItem) }
            showProductImage.setOnClickListener { listener.adapterSetImage(this)}
            sellerProductName.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sellerItem.name = sellerProductName.editableText.toString()
                    modifyItemInfo(sellerItem)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            sellerProductPrice.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(sellerProductPrice.editableText.toString()!="")
                        sellerItem.price= sellerProductPrice.editableText.toString().toInt()
                    else sellerItem.price = 0
                    modifyItemInfo(sellerItem)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
            sellerProductDescription.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    sellerItem.description= sellerProductDescription.editableText.toString()
                    modifyItemInfo(sellerItem)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        private fun modifyItemInfo(sellerItem:SellerItem){
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE){
                sellerItem.uploadState = UploadState.UPLOAD_MODIFIED
                sellerItemUploadButton.text = "修改"
                sellerItemUploadButton.isClickable = true
            }
        }

        private fun initEditText(sellerItem: SellerItem){
            val price = when(sellerItem.price!=0){
                 true->sellerItem.price.toString()
                 false->""
            }
            sellerProductName.setText(sellerItem.name)
            sellerProductPrice.setText(price)
            sellerProductDescription.setText(sellerItem.description)

            showProductImage.setImageResource(R.drawable.ic_add_a_photo_white_24dp)
            sellerItemUploadButton.text = "新增"
            sellerItemUploadButton.isClickable = true
            if(sellerItem.picture!="") updateImage(sellerItem)
            if(sellerItem.uploadState==UploadState.UPLOAD_MODIFIED) sellerItemUploadButton.text = "修改"
            if(sellerItem.uploadState==UploadState.UPLOAD_DONE) {
                sellerItemUploadButton.text = "已上傳"
                sellerItemUploadButton.isClickable = false
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

        private fun clickDeleteItem(sellerItem: SellerItem){
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
                    sellerItemUploadButton.text = "已上傳"
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
                    sellerItemUploadButton.text = "已上傳"
                }else{
                    Toast.makeText(currentActivity, sellerItem.uploadMessage, Toast.LENGTH_SHORT).show()
                }
            }
            cManager.commit(currentActivity)
        }
    }
}