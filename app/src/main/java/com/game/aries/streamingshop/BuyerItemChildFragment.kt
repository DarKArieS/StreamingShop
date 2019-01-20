package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.adapter.BuyerAdapter
import com.game.aries.streamingshop.model.BuyerItem
import kotlinx.android.synthetic.main.child_fragment_buyer_item.view.*


class BuyerItemChildFragment : Fragment(),
    BuyerAdapter.AdapterListener{
    private lateinit var rootView : View
    private var listener: OnFragmentInteractionListener?=null

    interface OnFragmentInteractionListener{
        fun goPayFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.child_fragment_buyer_item, container, false)

        rootView.payFloatingActionButton.setOnClickListener { listener!!.goPayFragment() }

        val fakeList = listOf(
            BuyerItem("物品一號",10, "一號物品", 0),
            BuyerItem("物品二號",20, "二號物品", 0),
            BuyerItem("物品三號",30, "三號物品", 0),
            BuyerItem("物品四號",40, "四號物品", 0)
        )

        setupAdapter(fakeList)

        return rootView
    }

    // this method seems only work for activity->fragment
//    override fun onAttach(context: Context?) {
//        super.onAttach(context)
//        if (context is OnFragmentInteractionListener){
//            listener = context
//        }else
//            throw RuntimeException("BuyerItemChild Attach error")
//    }


    companion object {
        @JvmStatic
        fun newInstance(setupListener: OnFragmentInteractionListener)
                = BuyerItemChildFragment().apply{
            listener = setupListener
        }
    }

    override fun nothingToDo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setupAdapter(itemList:List<BuyerItem>) {
        rootView.buyerRecyclerView.adapter = BuyerAdapter(this.context!!, itemList, this)
        rootView.buyerRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }

}
