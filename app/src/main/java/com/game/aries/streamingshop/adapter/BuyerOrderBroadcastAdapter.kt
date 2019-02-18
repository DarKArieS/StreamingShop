package com.game.aries.streamingshop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.R
import com.game.aries.streamingshop.model.Broadcast

class BuyerOrderBroadcastAdapter (val context: Context, val broadcastList: List<Broadcast>, val adapterListener:AdapterListener):
    RecyclerView.Adapter<BuyerOrderBroadcastAdapter.ViewHolder>() {

    interface AdapterListener{
        fun clickBuyerOrderBroadcast()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_buyer_order_broadcast, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.broadcastList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(broadcastList[p1])
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(buyerItem: Broadcast) {

        }
    }
}