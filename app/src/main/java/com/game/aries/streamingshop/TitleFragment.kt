package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import kotlinx.android.synthetic.main.fragment_title.view.*


class TitleFragment : Fragment() {
    lateinit var rootView : View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_title, container, false)

        rootView.buyerButton.setOnClickListener {clickBuyerButton()}
        rootView.logoutButton.setOnClickListener { clickLogoutButton() }
        rootView.sellerButton.setOnClickListener { clickSellerButton() }
        rootView.settingButton.setOnClickListener{testButton()}

        println("token: " + AccessToken.getCurrentAccessToken().token)
        println("userId: " + AccessToken.getCurrentAccessToken().userId)
        println("expires: " + AccessToken.getCurrentAccessToken().expires)
        println("isExpired: " + AccessToken.getCurrentAccessToken().isExpired)

        println(AccessToken.getCurrentAccessToken().expires.time) // unit: ms

        return rootView
    }

    private fun testButton(){
//        val request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(),
//            "785762085090409"
//        ) {
//            println(it.jsonObject)
//        }
//
//        val parameters = Bundle()
//        parameters.putString("fields", "ingest_streams")
//        request.parameters = parameters
//        request.executeAsync()

    }

    private fun clickBuyerButton(){
        (activity as MainActivity).findNavController(R.id.navHost)
            .navigate(TitleFragmentDirections.actionTitleFragmentToBroadcastListFragment())
    }

    private fun clickSellerButton(){
        (activity as MainActivity).findNavController(R.id.navHost)
            .navigate(TitleFragmentDirections.actionTitleFragmentToSellerFragment())
    }

    private fun clickLogoutButton(){
        LoginManager.getInstance().logOut()
        (activity as MainActivity).findNavController(R.id.navHost)
            .navigate(TitleFragmentDirections.actionTitleFragmentToLoginFragment())
    }

}
