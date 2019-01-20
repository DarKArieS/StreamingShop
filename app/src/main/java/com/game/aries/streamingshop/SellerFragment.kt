package com.game.aries.streamingshop

import android.content.ContextWrapper
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.dialog.EditBroadcastDialogFragment
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.MenuInterface
import kotlinx.android.synthetic.main.fragment_seller.view.*

class SellerFragment : Fragment(), MenuInterface, EditBroadcastDialogFragment.EditBroadcast {
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
        if (MainModel.broadcastID.length==15){
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
}
