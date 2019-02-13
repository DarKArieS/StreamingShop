package com.game.aries.streamingshop


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.navOptions
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager
import kotlinx.android.synthetic.main.fragment_login.view.*
import org.json.JSONObject


class LoginFragment : Fragment() {
    private lateinit var rootView : View
    private lateinit var navController : NavController

    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_login, container, false)
        navController = (activity as MainActivity).findNavController(R.id.navHost)

        val isFBLoggedIn = AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired
        if(isFBLoggedIn) login()

        rootView.login_button.setReadPermissions("email")
//        rootView.login_button.setPublishPermissions("publish_video")
        rootView.login_button.fragment = this
        rootView.login_button.registerCallback(callbackManager, object: FacebookCallback<LoginResult>{
            override fun onCancel() {
                println("cancel")
                //ToDo: show login canceled
            }

            override fun onError(error: FacebookException?) {
                //ToDo: show login error
                println(error)
            }

            override fun onSuccess(result: LoginResult?) {
                //Live Video API Test
                //POST
//                val request = GraphRequest.newPostRequest(
//                    AccessToken.getCurrentAccessToken(),
//                    "/" +
//                            AccessToken.getCurrentAccessToken().userId.toString() +
//                            "/live_videos",
//                    JSONObject("{\"title\":\"Today's Live Video\",\"description\":\"This is the live video for today.\"}"))
//                {
//                    println("Post video test")
//                    println(it)
//                }
//                request.executeAsync()
                // GET
//                val request2 = GraphRequest.newGraphPathRequest(
//                    AccessToken.getCurrentAccessToken(),
//                    "2132121343476981"
//                ) { response ->
//                    println("test live")
//                    println(response) }
//
//                val parameters = Bundle()
//                parameters.putString("fields", "ingest_streams")
//                request2.parameters = parameters
//                request2.executeAsync()

                println("result")
                println(result)
                println(result?.accessToken)
                println(result?.recentlyDeniedPermissions)
                println(result?.recentlyGrantedPermissions)

                println("accessToken: ")
                println("accessToken: " + AccessToken.getCurrentAccessToken())
                println("applicationId: " + AccessToken.getCurrentAccessToken().applicationId)
                println("declinedPermissions: " + AccessToken.getCurrentAccessToken().declinedPermissions)
                println("permissions: " + AccessToken.getCurrentAccessToken().permissions)
                println("expires: " + AccessToken.getCurrentAccessToken().expires)
                println("isExpired: " + AccessToken.getCurrentAccessToken().isExpired)
                println("source: " + AccessToken.getCurrentAccessToken().source)
                println("token: " + AccessToken.getCurrentAccessToken().token)
                println("userId: " + AccessToken.getCurrentAccessToken().userId)
                println("lastRefresh: " + AccessToken.getCurrentAccessToken().lastRefresh)

                login()
            }
        })

        return rootView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun login(){
        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.checkIsFirstLogin(p0, p1)
//            Thread{
//                Thread.sleep(1000)
//                p0.invoke()
//            }.start()
        }
        cManager.navigation = {
            if (MainModel.isFirstLogin){
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToUserInfoFragment())
            }else{
                navController.navigate(LoginFragmentDirections.actionLoginFragmentToTitleFragment())
            }

//            val navOpt = NavOptions.Builder().apply{println("XDD")}
//                .setPopEnterAnim(R.anim.slide_in_left)
//                .setPopExitAnim(R.anim.slide_out_right)
//                .build()
//            navController.navigate(LoginFragmentDirections.actionLoginFragmentToUserInfoFragment())

        }
        cManager.commit(activity as MainActivity)
    }
}
