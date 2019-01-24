package com.game.aries.streamingshop.utilities

import android.net.Uri
import android.os.Handler
import android.widget.Toast
import com.game.aries.streamingshop.MainActivity
import com.game.aries.streamingshop.model.MainModel


class CommunicationManager {
    var navigation: (() -> Unit)? = null
    var customCallback: (() -> Unit)? = null
    var communication: ((successCallback: () -> Unit, failureCallback: () -> Unit, arguments: CommunicationArguments?)->Unit)? = null
    var transitionMessage: String = "Loading"

    var arguments : CommunicationArguments? = null

    val handler = Handler()

    fun commit(mainActivity: MainActivity) {
        //TodoDone: Bug: communicating中onStop(例如按下多工鍵)，navigate會被吃掉
        val fullCallBack = {
            var handlerStatus = handler.post {
                if (customCallback != null) customCallback!!()
            }

            //navigation navigate protecting
            while (MainModel.mainActivityStatus == MainModel.MainActivityLifeCycle.PAUSE) {
                Thread.sleep(500)
            }

            handlerStatus = handler.post { if (navigation != null) navigation!!() }
            Thread.sleep(500)

            handlerStatus = handler.post { mainActivity.hideLoadingView() }
        }

        val failureCallback = {
            var handlerStatus = handler.post{
                Toast.makeText(mainActivity, "Connecting failed!", Toast.LENGTH_SHORT).show()
            }
        }

        //mainActivity.runOnUiThread{mainActivity.showLoadingView()}
        mainActivity.showLoadingView(transitionMessage)
        communication!!(fullCallBack, failureCallback, arguments)
    }

    // arguments data class
    interface CommunicationArguments
    data class UploadPhoto(var uri: Uri?, var stream_id: String): CommunicationArguments
}