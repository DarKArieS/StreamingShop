package com.game.aries.streamingshop.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.MainActivity
import com.game.aries.streamingshop.model.SellerItem
import kotlinx.android.synthetic.main.adapter_seller.view.*
import com.game.aries.streamingshop.R
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager

class SellerAdapter (val context: Context, var sellerItemList: MutableList<SellerItem>, val listener: AdapterListener, var currentActivity: MainActivity):
    RecyclerView.Adapter<SellerAdapter.ViewHolder>() {

    interface AdapterListener{
        fun adapterSetImage(index:Int)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_seller, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.sellerItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(sellerItemList[p1])
    }

    var currentViewHolder: ViewHolder? = null

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        private val sellerProductName = itemView.showProductName
        private val sellerProductPrice = itemView.showTotalProductPrice
        private val sellerProductDescription = itemView.sellerProductDescription
        private val deleteButton = itemView.deleteButton
        private val sellerItemUploadButton = itemView.sellerItemUploadButton
        private val showProductImage = itemView.showProductImage
        private val debug_showIndex = itemView.debug_showIndex

        fun bind(sellerItem: SellerItem) {

            debug_showIndex.text = adapterPosition.toString()

            initEditText(sellerItem)
            if(sellerItem.picture!="") updateImage(sellerItem)
            if(sellerItem.isUploaded) sellerItemUploadButton.text = "修改"

            deleteButton.setOnClickListener{
                clickDeleteItem()
            }

            sellerItemUploadButton.setOnClickListener{
                clickSellerItemUploadButton(sellerItem)
                // ToDo check item position
            }

            showProductImage.setOnClickListener {
                // ToDo check item position
                currentViewHolder = this
                listener.adapterSetImage(adapterPosition)
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
        }

        fun updateImage(sellerItem: SellerItem){
//            println("set viewholder image!")
//            println(sellerItem)
            if(!sellerItem.isUploaded){
                val imageUri = Uri.parse(sellerItem.picture)
            showProductImage.setImageURI(imageUri)
            }
        }

        private fun clickSellerItemUploadButton(sellerItem: SellerItem){
            if(!sellerItem.isUploaded) uploadItem(sellerItem)
        }

        private fun clickDeleteItem(){
            println("delete")
            println("adapterPosition"+adapterPosition.toString())
            println("layoutPosition"+ layoutPosition.toString())
            println("layoutPosition"+ oldPosition.toString())
            sellerItemList.removeAt(adapterPosition)
            notifyItemRemoved(adapterPosition)
        }

        fun uploadItem(sellerItem: SellerItem){
            sellerItem.name = sellerProductName.editableText.toString()
            if(sellerProductPrice.editableText.toString()!="")
                sellerItem.price= sellerProductPrice.editableText.toString().toInt()
            else sellerItem.price = 0
            sellerItem.description= sellerProductDescription.editableText.toString()

            val cManager = CommunicationManager()
            cManager.showMessage = "Uploading"
            val imageUri = Uri.parse(sellerItem.picture)
            cManager.communication = { p0,p1->
                MainModel.addSellerItem(p0,p1,sellerItem)
            }
            cManager.customCallback = {
                sellerItemUploadButton.text = "修改"
            }
            cManager.commit(currentActivity)
        }
    }
}