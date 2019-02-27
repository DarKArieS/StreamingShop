package com.game.aries.streamingshop.adapter

import android.content.Context
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.game.aries.streamingshop.model.BuyerItem
import com.game.aries.streamingshop.R
import kotlinx.android.synthetic.main.adapter_buyer_item.view.*


class BuyerAdapter (val context: Context, val buyerItemList: List<BuyerItem>, val listener: AdapterListener):
    RecyclerView.Adapter<BuyerAdapter.ViewHolder>() {

    interface AdapterListener{
        fun nothingToDo()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_buyer_item, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.buyerItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(buyerItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val showProductName = itemView.showProductName
        val showProductPrice = itemView.showProductPrice
        val showProductDescription = itemView.showProductDescription
        val showTotalProductNumber = itemView.showTotalProductNumber
        val showTotalProductPrice = itemView.showTotalProductPrice
        val minusBuyButton = itemView.minusBuyButton
        val plusBuyButton = itemView.plusBuyButton
        val showProductImage = itemView.showProductImage

        private var updateViewFlag = false

        init{
            showTotalProductNumber.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if(updateViewFlag) return
                    if(showTotalProductNumber.editableText.toString().length>3){
                        buyerItemList[adapterPosition].amount = 999
                        showTotalProductNumber.setText(buyerItemList[adapterPosition].amount.toString())
                        showTotalProductPrice.text = (buyerItemList[adapterPosition].price * buyerItemList[adapterPosition].amount).toString()
                    }else if(showTotalProductNumber.editableText.toString()!=""){
                        buyerItemList[adapterPosition].amount = showTotalProductNumber.editableText.toString().toInt()
                        showTotalProductPrice.text = (buyerItemList[adapterPosition].price * buyerItemList[adapterPosition].amount).toString()
                    }
                    else {
                        buyerItemList[adapterPosition].amount = 0
                        showTotalProductPrice.text = "？"
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }

        fun bind(buyerItem: BuyerItem) {
            updateViewFlag = true
            showProductName.text = buyerItem.name
            showProductPrice.text = "單價: " + buyerItem.price.toString()
            showProductDescription.text = buyerItem.description
            showTotalProductNumber.setText(buyerItem.amount.toString())
            showTotalProductPrice.text = (buyerItem.price * buyerItem.amount).toString()
            updateViewFlag = false

            Glide.with(context)
                .load(Uri.parse(buyerItem.picture))
                .placeholder(R.drawable.ic_add_a_photo_white_24dp)
                .into(showProductImage)

            // ToDo clean text when clicking editable text. this does not work well.
            showTotalProductNumber.setOnClickListener { showTotalProductNumber.setText("") }

            minusBuyButton.setOnClickListener { clickMinusButton(buyerItem) }
            plusBuyButton.setOnClickListener { clickPlusButton(buyerItem) }
        }

        fun clickMinusButton(buyerItem: BuyerItem){
            if (buyerItem.amount>0){
                buyerItem.amount --
                showTotalProductNumber.setText(buyerItem.amount.toString())
            }
        }
        fun clickPlusButton(buyerItem: BuyerItem){
            buyerItem.amount ++
            showTotalProductNumber.setText(buyerItem.amount.toString())
        }
    }
}