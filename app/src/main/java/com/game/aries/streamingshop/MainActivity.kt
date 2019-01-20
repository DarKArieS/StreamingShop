package com.game.aries.streamingshop

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.ActionBar
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
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

    //===================================================================================
    //點一下EditTextView外面收起鍵盤

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN){

            val v : View? = this.currentFocus

            if (this.isShouldHideInput(v, ev)) {
                hideSoftInput(v!!.windowToken)
            }

        }

        return super.dispatchTouchEvent(ev)
    }

    private fun isShouldHideInput(v : View?, event: MotionEvent): Boolean{
        if (v!= null && (v is EditText) ){
            val l : IntArray  = intArrayOf( 0, 0 )
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()

            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    private fun hideSoftInput(token: IBinder?) {
        if (token != null) {
            val im: InputMethodManager =  getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
    //===================================================================================

}
