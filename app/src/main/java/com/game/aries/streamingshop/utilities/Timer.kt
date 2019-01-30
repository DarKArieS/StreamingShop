package com.game.aries.streamingshop.utilities


import android.os.Handler

class ItemTimer(private var timerController: ItemTimer.TimerController){
    interface TimerController{
        fun timerOnUpdate()
        fun timesUp()
    }

    private var secondsCount = 60
    private var isStarted = false
    private var isStopCalled = false

    private lateinit var timerThread: Thread
    private var uiHandler = Handler()
    private lateinit var uiRunnable: Runnable

    fun setTime(seconds: Int): ItemTimer{
        secondsCount = seconds
        return this
    }

    fun start():ItemTimer {
        if(!isStarted){
            isStarted = true

            timerThread = Thread{
                while(secondsCount>=0 && !isStopCalled){
                    Thread.sleep(1000) // 0.01 second
                    secondsCount -= 1
                }
                if(!isStopCalled){
                    // time's up
                    uiHandler.post{
                        timerController.timesUp()
                        stop()
                    }
                }
            }

            uiRunnable = Runnable {
                timerController.timerOnUpdate()
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
        return this
    }
}