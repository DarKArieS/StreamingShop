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
        setSellerActionBar(true)

        return rootView
    }

    override fun onStop() {
        super.onStop()
        setSellerActionBar(false)
        MainModel.saveSellerInfo(activity as ContextWrapper)
    }

    override fun actionEdit(){
        val editNameDialog = EditBroadcastDialogFragment.newInstance(this)
        editNameDialog.show(fragmentManager, "EditNameDialog")
    }

    override fun editBroadcast() {
        (activity as MainActivity).mSupportActionBar.title = MainModel.broadcastName
    }

    private fun setSellerActionBar(setTrue:Boolean){
        when(setTrue){
            true->{
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
