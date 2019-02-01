package com.game.aries.streamingshop

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController

import com.facebook.login.LoginManager
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager

import kotlinx.android.synthetic.main.fragment_user_info.view.*

class UserInfoFragment : Fragment(), MainActivity.BackPressedManager {
    lateinit var rootView : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_user_info, container, false)

        rootView.cancelButton.setOnClickListener {
            LoginManager.getInstance().logOut()
            (activity as MainActivity).findNavController(R.id.navHost)
                .navigate(UserInfoFragmentDirections.actionUserInfoFragmentToLoginFragment())
        }

        rootView.enterUserInfoButton.setOnClickListener { clickEnterButton() }

        return rootView
    }

    override fun onBackPressed():Boolean {
        LoginManager.getInstance().logOut()
        (activity as MainActivity).findNavController(R.id.navHost)
            .navigate(UserInfoFragmentDirections.actionUserInfoFragmentToLoginFragment())
        return true
    }

    private fun clickEnterButton(){
        MainModel.userInfo.name = rootView.nameEditText.editableText.toString()
        MainModel.userInfo.phone = rootView.phoneEditText.editableText.toString()
        MainModel.userInfo.address = rootView.addressEditText.editableText.toString()

        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.putUserInfo(p0, p1)
//            Thread{p0()}.start()
        }
        cManager.navigation = {
            (activity as MainActivity).findNavController(R.id.navHost).
                navigate(UserInfoFragmentDirections.actionUserInfoFragmentToTitleFragment())
        }
        cManager.commit(activity as MainActivity)
    }
}
