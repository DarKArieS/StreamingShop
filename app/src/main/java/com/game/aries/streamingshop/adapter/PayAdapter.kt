package com.game.aries.streamingshop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.R
import com.game.aries.streamingshop.model.BuyerItem
import kotlinx.android.synthetic.main.adapter_buyer_paylist.view.*

class PayAdapter (val context: Context, val payItemList: List<BuyerItem>):
    RecyclerView.Adapter<PayAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_buyer_paylist, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.payItemList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(payItemList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val payProductImage = itemView.payProductImage
        private val payProductName = itemView.payProductName
        private val payProductAmount = itemView.payProductAmount
        private val payProductPrice = itemView.payProductPrice
        private val payTotalPrice = itemView.payTotalPrice

        fun bind(buyerItem: BuyerItem) {
            payProductName.text = "商品： " + buyerItem.name
            payProductAmount.text = "數量：" + buyerItem.amount.toString()
            payProductPrice.text = "單價：" + buyerItem.price.toString()
            payTotalPrice.text = (buyerItem.amount * buyerItem.price).toString()
        }
    }
}