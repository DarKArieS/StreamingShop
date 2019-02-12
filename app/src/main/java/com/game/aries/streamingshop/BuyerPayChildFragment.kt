package com.game.aries.streamingshop

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.adapter.PayAdapter
import com.game.aries.streamingshop.model.BuyerItem
import com.game.aries.streamingshop.model.MainModel
import kotlinx.android.synthetic.main.child_fragment_buyer_pay.view.*


class BuyerPayChildFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var rootView: View

    private var payItemList = listOf<BuyerItem>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.child_fragment_buyer_pay, container, false)

        rootView.payFloatingActionButton.setOnClickListener { listener?.clickCheckPay(payItemList) }

        return rootView
    }

    interface OnFragmentInteractionListener {
        fun clickCheckPay(itemList:List<BuyerItem>)
    }

    companion object {
        @JvmStatic
        fun newInstance(setupListener:OnFragmentInteractionListener) =
            BuyerPayChildFragment().apply {
                listener = setupListener
            }
    }

    fun setPaymentDetail(itemList:List<BuyerItem>){
        rootView.payUserName.text = "購買人： " + MainModel.userInfo.name
        rootView.payUserPhone.text = "連絡電話： " + MainModel.userInfo.phone
        rootView.payUserAddress.text = "寄送地址： " + MainModel.userInfo.address

        var totalPrice = 0
        for (i in itemList){
            totalPrice += i.price * i.amount
        }

        rootView.payUserTotalPrice.text = "總價： " + totalPrice.toString() + " NTD"

        println(itemList)
        payItemList = itemList

        setupAdapter(itemList)

        if(itemList.isEmpty()){
            rootView.payFloatingActionButton.hide()
        }else{
            rootView.payFloatingActionButton.show()
        }
    }

    private fun setupAdapter(itemList:List<BuyerItem>) {
        rootView.payRecyclerView.adapter = PayAdapter(this.context!!, itemList)
        rootView.payRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }
}
