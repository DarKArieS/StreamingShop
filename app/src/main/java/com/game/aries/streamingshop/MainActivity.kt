package com.game.aries.streamingshop

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import com.game.aries.streamingshop.model.MainModel

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onBackPressed() {
        if(!MainModel.checkIsLoading()){

//            val navController = this.findNavController(R.id.navHost)
//            if(){
//
//            }
            super.onBackPressed()
        }


    }
}
