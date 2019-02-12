package com.game.aries.streamingshop

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.drawable.AnimationDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.support.constraint.ConstraintLayout
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.navigation.NavDirections
import androidx.navigation.createGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.game.aries.streamingshop.utilities.MenuInterface
import com.game.aries.streamingshop.model.MainModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.loading_layout.view.*

class MainActivity : AppCompatActivity() {
    lateinit var rootViewGroup : ViewGroup

    lateinit var customMenu: Menu
    lateinit var mSupportActionBar: ActionBar
    lateinit var drawerToggle: ActionBarDrawerToggle
    var menuInterface: MenuInterface? = null

    lateinit var loadingView: View
    private lateinit var transitionAnimEnter: ObjectAnimator
    private lateinit var transitionAnimExit: ObjectAnimator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val navController = this.findNavController(R.id.navHost)
//        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
//        NavigationUI.setupWithNavController(navDrawer_left, navController)

        initAnimator()
        initNavigationDrawer()

        MainModel.tmpExternalFile = this.externalCacheDir
        rootViewGroup = window.decorView.rootView as ViewGroup

        println("MainActivity onCreate")
    }

    private fun initNavigationDrawer(){
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,navDrawer_right)

        navDrawer_left.setNavigationItemSelectedListener { menuItem ->
            drawerLayout.closeDrawers()

            when(menuItem.itemId){
                R.id.drawer_setting->{
                    println("click drawer setting")
                    val transaction =
                        navHost.childFragmentManager!!.beginTransaction()
                    val frag = SettingFragment()

                    transaction.setCustomAnimations(
                        R.anim.slide_in_right,
                        R.anim.slide_out_left,
                        R.anim.slide_in_left,
                        R.anim.slide_out_right
                    )
                    transaction.hide(navHost.childFragmentManager.fragments.last())
                    transaction.add(R.id.navHost, frag)
                    transaction.addToBackStack(null)
                    transaction.commit()
//                    println(navHost.childFragmentManager.fragments)
                }
            }
            true
        }
    }

    fun isExistsNavigationDrawer(isExists:Boolean){
        if(isExists){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED,navDrawer_left)
            mSupportActionBar.setDisplayHomeAsUpEnabled(true)
            mSupportActionBar.setHomeButtonEnabled(true)
            drawerToggle.syncState()
        }else{
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED,navDrawer_left)
            mSupportActionBar.setDisplayHomeAsUpEnabled(false)
            mSupportActionBar.setHomeButtonEnabled(false)
            drawerToggle.syncState()
        }
    }

    private fun initAnimator(){
        loadingView = this.layoutInflater.inflate(R.layout.loading_layout, null)
        loadingView.setOnTouchListener { _,_ ->  true} // block touch event

        transitionAnimEnter = ObjectAnimator
            .ofFloat(loadingView, "alpha", 0f, 0.5f)
            .setDuration(200)

        transitionAnimEnter.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
                MainModel.isLoading = true
                val params = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.MATCH_PARENT,
                    ConstraintLayout.LayoutParams.MATCH_PARENT
                )
                rootViewGroup.addView(loadingView,params)
                (rootViewGroup.loadingAnimator.background as AnimationDrawable).start()
            }
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        transitionAnimExit = ObjectAnimator
            .ofFloat(loadingView, "alpha", 0.5f, 0f)
            .setDuration(200)

        transitionAnimExit.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                (rootViewGroup.loadingAnimator.background as AnimationDrawable).stop()
                rootViewGroup.removeView(loadingView)
                MainModel.isLoading = false
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    fun showLoadingView(loadingString:String="Connecting"){
        loadingView.loadingTextView.text = loadingString
        transitionAnimEnter.start()
    }

    fun hideLoadingView(){
        transitionAnimExit.start()
    }

    override fun onPause() {
        MainModel.mainActivityStatus = MainModel.MainActivityLifeCycle.PAUSE
        super.onPause()
        println("onPause")
    }

    override fun onResume() {
        MainModel.mainActivityStatus = MainModel.MainActivityLifeCycle.RUNNING
        super.onResume()
        println("onResume")
    }

    // Solved(?) To Do: sometimes customMenu will be null
    // switch using "onPrepareOptionsMenu" may be better than "onCreateOptionsMenu"...
    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        customMenu = menu
        mSupportActionBar = supportActionBar!!
        menuInflater.inflate(R.menu.custom_action_bar, customMenu)

        drawerToggle = ActionBarDrawerToggle(
            this, drawerLayout, R.string.app_name, R.string.app_name)

        drawerLayout.addDrawerListener(drawerToggle)
        isExistsNavigationDrawer(false)

        println("MainActivity onPrepareOptionsMenu")
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_edit->{
                menuInterface?.actionBarEdit()
            }
        }
        if (drawerToggle.onOptionsItemSelected(item)) {
            // 防止兩邊的Drawer都被打開
//            if (drawerLayout.isDrawerOpen(Gravity.END)){
//                drawerLayout.closeDrawer(Gravity.END)
//            }
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    interface BackPressedManager{
        fun onBackPressed(): Boolean
    }

    override fun onBackPressed() {
        if(!MainModel.checkIsLoading()){
            if (drawerLayout.isDrawerOpen(navDrawer_left)){
                drawerLayout.closeDrawer(navDrawer_left)
                return
            }

            val currentFragment= navHost.childFragmentManager.fragments.last()
            if (currentFragment is BackPressedManager){
                if(!(currentFragment as BackPressedManager).onBackPressed()){
                    super.onBackPressed()
                }
            }else{
                super.onBackPressed()
            }
        }
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
