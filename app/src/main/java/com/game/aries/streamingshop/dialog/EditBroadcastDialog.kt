package com.game.aries.streamingshop.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.R
import com.game.aries.streamingshop.model.MainModel
import kotlinx.android.synthetic.main.dialog_edit_broadcast.view.*


class EditBroadcastDialog : DialogFragment() {
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.dialog_edit_broadcast, container, false)

        rootView.broadcastNameEditText.setText(MainModel.sellerBroadcast.broadcastName)
        rootView.broadcastIDEditText.setText(MainModel.sellerBroadcast.broadcastID)
        rootView.dialogEnterButton.setOnClickListener { clickEnter() }

        return rootView
    }

    var editBroadcast: EditBroadcast? = null
    interface EditBroadcast{
        fun editBroadcast()
    }

    private fun clickEnter(){
        MainModel.sellerBroadcast.broadcastName = rootView.broadcastNameEditText.text.toString()
        MainModel.sellerBroadcast.broadcastID = rootView.broadcastIDEditText.text.toString()
        // ToDoDone: Do parsing here
        if(MainModel.sellerBroadcast.broadcastID.contains("http")){
            val splitStrings = MainModel.sellerBroadcast.broadcastID.split("/")
            //println(splitStrings)
            if(MainModel.sellerBroadcast.broadcastID.endsWith("/"))MainModel.sellerBroadcast.broadcastID = splitStrings[splitStrings.size-2]
            else MainModel.sellerBroadcast.broadcastID = splitStrings[splitStrings.size-1]
        }
        editBroadcast!!.editBroadcast()
        this.dismiss()
    }

    companion object {
        //@JvmStatic
        fun newInstance(listener: EditBroadcast) =
            EditBroadcastDialog().apply {
                editBroadcast = listener
            }
    }
}
