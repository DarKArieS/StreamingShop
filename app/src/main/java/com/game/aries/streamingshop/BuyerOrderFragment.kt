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


private const val ARG_FRAG_BUYER_ORDER_ITEM = "ARG_FRAG_BUYER_ORDER_ITEM"
private const val ARG_FRAG_BUYER_ORDER_BROADCAST = "ARG_FRAG_BUYER_ORDER_BROADCAST"

class BuyerOrderFragment : Fragment(){
    lateinit var rootView : View

    companion object {
        @JvmStatic
        fun newInstance()
                = BuyerOrderFragment()//.apply{}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as MainActivity).setNavDrawerItemCheck()
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_buyer_order, container, false)

        val buyerOrderBroadcastChildFragment = BuyerOrderBroadcastChildFragment()
        val buyerOrderItemChildFragment = BuyerOrderItemChildFragment()

        val transaction = this.childFragmentManager.beginTransaction()
        transaction.add(R.id.buyerOrderFragmentContainer,buyerOrderBroadcastChildFragment, ARG_FRAG_BUYER_ORDER_BROADCAST)
        transaction.add(R.id.buyerOrderFragmentContainer,buyerOrderItemChildFragment, ARG_FRAG_BUYER_ORDER_ITEM)
        transaction.hide(buyerOrderItemChildFragment)
        transaction.commit()

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

    fun clickBuyerOrderBroadcast(){
        val myManager = this.childFragmentManager
        val buyerOrderBroadcastChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_ORDER_BROADCAST)
        val buyerOrderItemChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_ORDER_ITEM)

        val transaction = myManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        transaction.show(buyerOrderItemChildFragment!!)
        transaction.hide(buyerOrderBroadcastChildFragment!!)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
