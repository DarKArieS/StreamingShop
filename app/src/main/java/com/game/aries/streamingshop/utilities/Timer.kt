package com.game.aries.streamingshop.utilities


import android.os.Handler
import com.game.aries.streamingshop.model.SellerItem
import com.game.aries.streamingshop.model.UploadState

class ItemTimer{
//    enum class State{NEW, STARTED, DONE}

//    var state = ItemTimer.State.NEW

    interface TimerListener{
        fun timerOnUpdate(time:Int)
        fun timesUp(sellerItem:SellerItem)
    }

    private var timerListener: ItemTimer.TimerListener? = null
    private var itemListener: SellerItem? = null

    var secondsCount = 60
    private var isStarted = false
    private var isStopCalled = false

    private lateinit var timerThread: Thread
    private var uiHandler = Handler()
    private lateinit var uiRunnable: Runnable

    fun setListener(l:ItemTimer.TimerListener): ItemTimer{
        timerListener = l
        return this
    }

    fun setItemListener(l:SellerItem): ItemTimer{
        itemListener = l
        return this
    }

    fun removeListener(){
        timerListener = null
    }

    fun setTime(seconds: Int): ItemTimer{
        secondsCount = seconds
        return this
    }

    fun start():ItemTimer {
        if(!isStarted){
//            state = ItemTimer.State.STARTED
            isStarted = true

            timerThread = Thread{
                while(secondsCount>=0 && !isStopCalled){
                    Thread.sleep(1000) // 0.01 second
                    secondsCount -= 1
                }
                if(!isStopCalled){
                    // time's up
                    uiHandler.post{
                        stop()
                    }
                }
            }

            uiRunnable = Runnable {
                timerListener?.timerOnUpdate(secondsCount)
                uiHandler.postDelayed(uiRunnable, 1000)
            }

            isStopCalled = false
            timerThread.start()
            uiHandler.postDelayed(uiRunnable, 1000)
        }
        return this
    }

    fun stop():ItemTimer {
        isStopCalled = true
        isStarted = false
        uiHandler.removeCallbacks(uiRunnable)
//        state = ItemTimer.State.DONE
        itemListener?.uploadState = UploadState.ITEM_SELLING_END
        timerListener?.timesUp(itemListener!!)
        return this
    }
}