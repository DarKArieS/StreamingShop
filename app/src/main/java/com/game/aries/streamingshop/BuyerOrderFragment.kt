package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager
import kotlinx.android.synthetic.main.fragment_buyer_order.view.*


class BuyerOrderFragment : Fragment() {
    lateinit var rootView : View

    companion object {
        @JvmStatic
        fun newInstance()
                = BuyerOrderFragment().apply{
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).setNavDrawerItemCheck()
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buyer_order, container, false)

        rootView.buyerOrderTestButton.setOnClickListener { clickTestButton() }

        return rootView
    }

    private fun clickTestButton(){
        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.getBuyerOrderList(p0, p1)
            MainModel.getSellerOrderList(p0, p1)
        }
        cManager.commit(activity as MainActivity)
    }
}
