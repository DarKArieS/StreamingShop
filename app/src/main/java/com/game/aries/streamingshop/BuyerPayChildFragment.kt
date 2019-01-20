package com.game.aries.streamingshop

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


class BuyerPayChildFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.child_fragment_buyer_pay, container, false)

        return rootView
    }

    interface OnFragmentInteractionListener {
        fun clickCheckPay()
    }

    companion object {
        @JvmStatic
        fun newInstance(setupListener:OnFragmentInteractionListener) =
            BuyerPayChildFragment().apply {
                listener = setupListener
            }
    }
}
