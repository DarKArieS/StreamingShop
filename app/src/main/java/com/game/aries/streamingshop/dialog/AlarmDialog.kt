package com.game.aries.streamingshop.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.R
import kotlinx.android.synthetic.main.dialog_alarm.view.*

abstract class AlarmDialog : DialogFragment() {
    private lateinit var rootView: View
    private var mAlarmString : String? = null
    abstract fun clickEnterButton()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.dialog_alarm, container, false)

        if (mAlarmString != null) rootView.alarmMessageTextView.text = mAlarmString

        rootView.alarmOKbutton.setOnClickListener {
//            dialogListener!!.alarmDialogEnter()
            clickEnterButton()
            this.dismiss()
        }
        rootView.alarmCancelbutton.setOnClickListener { this.dismiss() }

        return rootView
    }

    fun setAlarmMessage(message:String){
        mAlarmString = message
    }

//    var dialogListener: DialogListener? = null
//    interface DialogListener{
//        fun alarmDialogEnter()
//    }
//
//    companion object {
//        //@JvmStatic
//        fun newInstance(listener: DialogListener, alarmMessage:String?=null) =
//            AlarmDialog().apply {
//                dialogListener = listener
//                mAlarmString = alarmMessage
//            }
//    }
}