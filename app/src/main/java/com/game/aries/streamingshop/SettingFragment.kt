package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_main.*

class SettingFragment : Fragment()//, MainActivity.BackPressedManager
{

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

//    override fun onBackPressed(): Boolean {
//        println((activity as MainActivity).navHost.childFragmentManager.fragments)
//        (activity as MainActivity).navHost.childFragmentManager!!.popBackStack()
//        println((activity as MainActivity).navHost.childFragmentManager.fragments)
//        return true
//    }

}
