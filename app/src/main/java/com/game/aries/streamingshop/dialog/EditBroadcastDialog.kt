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

        rootView.broadcastNameEditText.setText(MainModel.broadcastName)
        rootView.broadcastIDEditText.setText(MainModel.broadcastID)
        rootView.dialogEnterButton.setOnClickListener { clickEnter() }

        return rootView
    }

    var editBroadcast: EditBroadcast? = null
    interface EditBroadcast{
        fun editBroadcast()
    }

    private fun clickEnter(){
        MainModel.broadcastName = rootView.broadcastNameEditText.text.toString()
        MainModel.broadcastID = rootView.broadcastIDEditText.text.toString()
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