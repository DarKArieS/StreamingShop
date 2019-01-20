package com.game.aries.streamingshop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.game.aries.streamingshop.model.SellerAdapterData
import kotlinx.android.synthetic.main.adapter_seller.view.*
import com.game.aries.streamingshop.R

class SellerAdapter (val context: Context, val sellerItemList: List<SellerAdapterData>, val listener: AdapterListener):
    RecyclerView.Adapter<SellerAdapter.ViewHolder>() {

    interface AdapterListener{
        fun deleteItem(index:Int)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_seller, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.sellerItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(p1, sellerItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val sellerProductName = itemView.sellerProductName
        private val sellerProductPrice = itemView.sellerProductPrice
        private val sellerProductDescription = itemView.sellerProductDescription


        fun bind(index:Int, sellerItem: SellerAdapterData) {
            initEditText(sellerItem)

            itemView.deleteButton.setOnClickListener{
                listener.deleteItem(index)
            }

            itemView.sellerItemOKButton.setOnClickListener{
                sellerItem.name = sellerProductName.editableText.toString()
                if(sellerProductPrice.editableText.toString()!="")
                    sellerItem.price= sellerProductPrice.editableText.toString().toInt()
                else sellerItem.price = 0
                sellerItem.description= sellerProductDescription.editableText.toString()
            }
        }

        fun initEditText(sellerItem: SellerAdapterData){
            val price = when(sellerItem.price!=0){
                 true->sellerItem.price.toString()
                 false->""
            }
            sellerProductName.setText(sellerItem.name)
            sellerProductPrice.setText(price)
            sellerProductDescription.setText(sellerItem.description)
        }

    }
}