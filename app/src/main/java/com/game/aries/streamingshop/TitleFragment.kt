package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.facebook.AccessToken
import com.facebook.login.LoginManager
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.utilities.CommunicationManager
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.fragment_title.view.*


class TitleFragment : Fragment() {
    lateinit var rootView : View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_title, container, false)
        (activity as MainActivity).setExistNavigationDrawer(true)

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

//========================================================================================
        /*
        // socket test
        Thread{
            println("socket test")
            val socketIP = "192.168.56.216"
            val mySocket = Socket(socketIP, 487)

            val bw = BufferedWriter(OutputStreamWriter(mySocket.getOutputStream()))
            val br = BufferedReader(InputStreamReader(mySocket.getInputStream()))

            println("isConnected? :" + mySocket.isConnected.toString())

            var connected = mySocket.isConnected
            while(connected){
                println("try to get string from server!")
                var tmp = br.readLine()
                println(tmp)
                if(tmp == null){
                    println("disconnect!")
                    bw.close()
                    br.close()
                    mySocket.close()
                    println("isConnected? :" + mySocket.isConnected.toString())
                    connected = false
                }
                //Thread.sleep(2000)
            }
            println("End test")
        }.start()
        */
//========================================================================================
        //socket io
        println("test socket io")
        val opt = IO.Options()
        opt.path = "tasks"
//        val socket = IO.socket("http://ac78b079.ngrok.io")
        val socket = IO.socket("http://192.168.55.226:3000")
//        val socket = IO.socket("http://192.168.56.216:487")

        socket.on(Socket.EVENT_CONNECT, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("connected!")
                println(args)
            }
        })
        socket.on(Socket.EVENT_PING, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("ping!")
                println(args)
            }
        })

        socket.on(Socket.EVENT_MESSAGE, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("message!")
                println(args)
            }
        })

        socket.on(Socket.EVENT_ERROR, object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("error!")
                println(args)
            }
        })

//        socket.on("TaskCreated:tasks", object: Emitter.Listener{
//            override fun call(vararg args: Any?) {
//                println("tasks")
//                println(args)
//            }
//        })


        socket.on("test-channel:UserSignedUp", object: Emitter.Listener{
            override fun call(vararg args: Any?) {
                println("get a message")
                println(args[0])
            }
        })

        socket.connect()
        //socket.emit("/tasks", "XDD")
        //socket.disconnect()
    }

    private fun clickBuyerButton(){
        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.updateBroadcastList(p0, p1)
        }
        cManager.navigation = {
            (activity as MainActivity).findNavController(R.id.navHost)
                .navigate(TitleFragmentDirections.actionTitleFragmentToBroadcastListFragment())
        }
        cManager.commit(activity as MainActivity)
    }

    private fun clickSellerButton(){
        val cManager = CommunicationManager()
        cManager.communication = { p0,p1->
            MainModel.updateSellerItemList(p0, p1)
//            Thread{p0.invoke()}.start()
        }
        cManager.navigation = {
            (activity as MainActivity).findNavController(R.id.navHost)
                .navigate(TitleFragmentDirections.actionTitleFragmentToSellerFragment())
        }
        cManager.commit(activity as MainActivity)
    }

    private fun clickLogoutButton(){
        LoginManager.getInstance().logOut()
        (activity as MainActivity).findNavController(R.id.navHost)
            .navigate(TitleFragmentDirections.actionTitleFragmentToLoginFragment())
//            .navigate(TitleFragmentDirections.actionTitleFragmentToLoginFragmentNoAnim())
    }

}
