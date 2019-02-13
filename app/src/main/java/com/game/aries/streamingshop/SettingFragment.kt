package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup

class SettingFragment : Fragment(), MainActivity.BackPressedManager
{
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as MainActivity).setNavDrawerItemCheck()

        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onBackPressed(): Boolean {
        //navMenuItem?.isChecked = false
        return false
    }

    companion object{
        @JvmStatic
        fun newInstance()
                = SettingFragment().apply{
        }
    }

}
