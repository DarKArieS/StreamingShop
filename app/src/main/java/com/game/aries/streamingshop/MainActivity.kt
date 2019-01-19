package com.game.aries.streamingshop

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import com.game.aries.streamingshop.utilities.MenuInterface
import com.game.aries.streamingshop.model.MainModel

class MainActivity : AppCompatActivity() {

    lateinit var customMenu: Menu
    lateinit var mSupportActionBar: ActionBar
    var menuInterface: MenuInterface? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = this.findNavController(R.id.navHost)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        customMenu = menu!!
        mSupportActionBar = supportActionBar!!
        menuInflater.inflate(R.menu.custom_action_bar, customMenu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if(!MainModel.checkIsLoading()){
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_edit->{
                menuInterface?.actionEdit()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
