package com.game.aries.streamingshop.utilities

import android.net.Uri
import android.os.Handler
import android.widget.Toast
import com.game.aries.streamingshop.MainActivity
import com.game.aries.streamingshop.model.MainModel

class CommunicationManager {
    var navigation: (() -> Unit)? = null
    var customCallback: (() -> Unit)? = null
    var afterCallback: (() -> Unit)? = null

    // this callback needs to run on other thread
    var communication: ((successCallback: () -> Unit, failureCallback: () -> Unit)->Unit)? = null
    var loadingMessage: String = "Loading"
    var failMessage = "Connecting failed!"

    val handler = Handler()

    fun commit(mainActivity: MainActivity){
        if(communication==null) return

        //TodoDone: Bug: communicating中onStop(例如按下多工鍵)，navigate會被吃掉
        val fullCallBack = {
            var handlerStatus = false
            if (customCallback != null) handlerStatus = handler.post { customCallback!!() }

            //navigation navigate protecting
            while (MainModel.mainActivityStatus == MainModel.MainActivityLifeCycle.PAUSE) {
                Thread.sleep(500)
            }

            if (navigation != null) handlerStatus = handler.post {  navigation!!() }
            Thread.sleep(200)

            handlerStatus = handler.post { mainActivity.hideLoadingView(afterCallback) }
        }

        val failureCallback = {
            var handlerStatus = handler.post{
                Toast.makeText(mainActivity, failMessage, Toast.LENGTH_SHORT).show()
                mainActivity.hideLoadingView()
            }
        }

        //mainActivity.runOnUiThread{mainActivity.showLoadingView()}
        mainActivity.showLoadingView(loadingMessage)
        communication!!(fullCallBack, failureCallback)
    }
}