package com.game.aries.streamingshop.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.R
import kotlinx.android.synthetic.main.dialog_get_image.view.*


class GetImageDialog : DialogFragment() {
    private lateinit var rootView: View

    enum class Action {ALBUM, CAMERA}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.dialog_get_image, container, false)

        rootView.albumButton.setOnClickListener {
            dialogListener!!.chooseImageResource(Action.ALBUM)
            this.dismiss()
        }
        rootView.cameraButton.setOnClickListener {
            dialogListener!!.chooseImageResource(Action.CAMERA)
            this.dismiss()
        }

        return rootView
    }

    var dialogListener: DialogListener? = null
    interface DialogListener{
        fun chooseImageResource(action:Action)
    }

    companion object {
        //@JvmStatic
        fun newInstance(listener: DialogListener) =
            GetImageDialog().apply {
                dialogListener = listener
            }
    }
}