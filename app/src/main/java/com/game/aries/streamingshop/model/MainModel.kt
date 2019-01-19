package com.game.aries.streamingshop.model

import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.login.LoginManager

object MainModel {

    var isLoading = false
    fun checkIsLoading():Boolean{
        return (isLoading)
    }

}