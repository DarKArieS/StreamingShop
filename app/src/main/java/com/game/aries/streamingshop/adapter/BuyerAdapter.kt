package com.game.aries.streamingshop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        p0.bind(p1, buyerItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val showProductName = itemView.showProductName
        val showProductPrice = itemView.showProductPrice
        val showProductDescription = itemView.showProductDescription
        val showTotalProductNumber = itemView.showTotalProductNumber
        val showTotalProductPrice = itemView.showTotalProductPrice
        val minusBuyButton = itemView.minusBuyButton
        val plusBuyButton = itemView.plusBuyButton

        fun bind(index:Int, buyerItem: BuyerItem) {
            showProductName.text = buyerItem.name
            showProductPrice.text = "單價: " + buyerItem.price.toString()
            showProductDescription.text = buyerItem.description
            showTotalProductNumber.setText(buyerItem.number.toString())
            showTotalProductPrice.text = (buyerItem.price * buyerItem.number).toString()

            showTotalProductNumber.addTextChangedListener(object: TextWatcher {
                override fun afterTextChanged(s: Editable?) {

                    if(showTotalProductNumber.editableText.toString()!=""){
                        buyerItem.number = showTotalProductNumber.editableText.toString().toInt()
                        showTotalProductPrice.text = (buyerItem.price * buyerItem.number).toString()
                    }else {
                        buyerItem.number = 0
                        showTotalProductPrice.text = "？"
                    }
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // ToDo clean text when clicking editable text. this does not work well.
            showTotalProductNumber.setOnClickListener { showTotalProductNumber.setText("") }

            minusBuyButton.setOnClickListener { clickMinusButton(buyerItem) }
            plusBuyButton.setOnClickListener { clickPlusButton(buyerItem) }
        }

        fun clickMinusButton(buyerItem: BuyerItem){
            if (buyerItem.number>1){
                buyerItem.number --
                showTotalProductNumber.setText(buyerItem.number.toString())
            }
        }
        fun clickPlusButton(buyerItem: BuyerItem){
            buyerItem.number ++
            showTotalProductNumber.setText(buyerItem.number.toString())
        }
    }
}