package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.game.aries.streamingshop.adapter.BroadcastListAdapter
import com.game.aries.streamingshop.model.Broadcast
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager
import kotlinx.android.synthetic.main.fragment_broadcast_list.view.*

class BroadcastListFragment : Fragment(), BroadcastListAdapter.AdapterListener {
    private lateinit var rootView : View
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_broadcast_list, container, false)
        navController = (activity as MainActivity).findNavController(R.id.navHost)

        // reset now watching
        MainModel.broadcastWatching = Broadcast("","")

        // setup adapter
        val fakeList = mutableListOf(
            Broadcast("我是直播","388355185056271"),
            Broadcast("Sen","395791191178138")
        )
//        MainModel.broadcastList = fakeList
        setupAdapter(MainModel.broadcastList)

        return rootView
    }

    override fun clickBroadcast(selectedBroadcast: Broadcast) {
        MainModel.broadcastWatching = selectedBroadcast
//        navController.navigate(BroadcastListFragmentDirections.actionBroadcastListFragmentToBuyerFragment())

        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.updateBuyerItemList(p0, p1)
        }
        cManager.navigation = {
            navController.navigate(BroadcastListFragmentDirections.actionBroadcastListFragmentToBuyerFragment())
        }
        cManager.commit(activity as MainActivity)
    }


    private fun setupAdapter(itemList:List<Broadcast>) {
        rootView.broadcastListRecyclerView.adapter = BroadcastListAdapter(this.context!!, itemList, this)
        rootView.broadcastListRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }
}
