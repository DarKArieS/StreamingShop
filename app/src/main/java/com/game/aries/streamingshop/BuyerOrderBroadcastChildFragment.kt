package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.adapter.BuyerOrderBroadcastAdapter
import com.game.aries.streamingshop.model.Broadcast
import kotlinx.android.synthetic.main.child_fragment_buyer_order_broadcast.view.*


class BuyerOrderBroadcastChildFragment : Fragment(), BuyerOrderBroadcastAdapter.AdapterListener {
    lateinit var rootView :View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.child_fragment_buyer_order_broadcast, container, false)

        val fakeBroadcastList = listOf(Broadcast("XDD","123456789012345"))
        setupAdapter(fakeBroadcastList)

        return rootView
    }

    override fun clickBuyerOrderBroadcast() {
        (this.parentFragment as BuyerOrderFragment).clickBuyerOrderBroadcast()
    }

    private fun setupAdapter(broadcastList:List<Broadcast>) {
        rootView.BuyerOrderBroadcastRecyclerView.adapter = BuyerOrderBroadcastAdapter(this.context!!, broadcastList, this)
        rootView.BuyerOrderBroadcastRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }
}
