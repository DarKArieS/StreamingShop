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

class SellerAdapter (val context: Context, var sellerItemList: List<SellerItem>, val listener: AdapterListener, var currentActivity: MainActivity):
    RecyclerView.Adapter<SellerAdapter.ViewHolder>() {

    interface AdapterListener{
        fun deleteItem(index:Int)
        fun clickImage(index:Int)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_seller, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.sellerItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(p1, sellerItemList[p1])
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

        fun bind(index:Int, sellerItem: SellerItem) {
            initEditText(sellerItem)
            if(sellerItem.picture!="") updateImage(sellerItem)

            deleteButton.setOnClickListener{
                listener.deleteItem(index)
            }

            sellerItemUploadButton.setOnClickListener{
                uploadItem(sellerItem)
            }

            showProductImage.setOnClickListener {
                listener.clickImage(index)
                currentViewHolder = this
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
            println("set viewholder image!")
            if(!sellerItem.isUploaded){
                val imageUri = Uri.parse(sellerItem.picture)
            showProductImage.setImageURI(imageUri)
            }
        }

        fun uploadItem(sellerItem: SellerItem){
            sellerItem.name = sellerProductName.editableText.toString()
            if(sellerProductPrice.editableText.toString()!="")
                sellerItem.price= sellerProductPrice.editableText.toString().toInt()
            else sellerItem.price = 0
            sellerItem.description= sellerProductDescription.editableText.toString()

            val cManager = CommunicationManager()
            val imageUri = Uri.parse(sellerItem.picture)

        }
    }
}