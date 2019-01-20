package com.game.aries.streamingshop

import android.content.ContextWrapper
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.adapter.SellerAdapter
import com.game.aries.streamingshop.dialog.EditBroadcastDialogFragment
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.model.SellerAdapterData
import com.game.aries.streamingshop.utilities.MenuInterface
import kotlinx.android.synthetic.main.fragment_seller.view.*

class SellerFragment : Fragment(), MenuInterface,
    EditBroadcastDialogFragment.EditBroadcast,
    SellerAdapter.AdapterListener {
    lateinit var rootView : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_seller, container, false)

        //read stored information
        MainModel.loadSellerInfo(activity as ContextWrapper)

        // set action bar
        setSellerFragment(true)

        // setup adapter
        MainModel.sellerItemList = mutableListOf(
            SellerAdapterData("", 0, "")
        )
        setupAdapter(MainModel.sellerItemList)

        // setup refresh
        rootView.swipeRefreshLayout.setOnRefreshListener{
            setupAdapter(MainModel.sellerItemList)
            rootView.swipeRefreshLayout.isRefreshing=false
        }

        // setup button
        rootView.addItemFloatingActionButton.setOnClickListener { clickAddItem() }

        return rootView
    }

    override fun onStop() {
        super.onStop()
        setSellerFragment(false)
        MainModel.saveSellerInfo(activity as ContextWrapper)
    }

    override fun actionEdit(){
        val editNameDialog = EditBroadcastDialogFragment.newInstance(this)
        editNameDialog.show(fragmentManager, "EditNameDialog")
    }

    override fun editBroadcast() {
        (activity as MainActivity).mSupportActionBar.title = MainModel.broadcastName
        checkBroadcastID()
    }

    private fun checkBroadcastID(){
        if (MainModel.broadcastID.length>=15){
            rootView.startStreamFloatingActionButton.isClickable = true
            rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_start_vector)
        }else{
            rootView.startStreamFloatingActionButton.isClickable = false
            rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_forbidden_vector)
        }
    }

    private fun setSellerFragment(setTrue:Boolean){
        when(setTrue){
            true->{
                checkBroadcastID()
                (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = true
                (activity as MainActivity).mSupportActionBar.title = MainModel.broadcastName
                (activity as MainActivity).menuInterface = this
            }
            false->{
                (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = false
                (activity as MainActivity).mSupportActionBar.title = getString(R.string.app_name)
                (activity as MainActivity).menuInterface = null
            }
        }
    }

    private fun setupAdapter(itemList:List<SellerAdapterData>) {
        rootView.sellerRecyclerView.adapter = SellerAdapter(this.context!!, itemList, this)
        rootView.sellerRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }

    private fun clickAddItem(){
        MainModel.sellerItemList.add(0, SellerAdapterData("", 0, ""))
        setupAdapter(MainModel.sellerItemList)
    }

    override fun deleteItem(index: Int) {
        MainModel.sellerItemList.removeAt(index)
        setupAdapter(MainModel.sellerItemList)
    }
}
