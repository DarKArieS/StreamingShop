package com.game.aries.streamingshop


import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.game.aries.streamingshop.model.MainModel
import kotlinx.android.synthetic.main.fragment_buyer.view.*

private const val ARG_FRAG_BUYER_ITEM = "ARG_FRAG_BUYER_ITEM"
private const val ARG_FRAG_BUYER_PAY = "ARG_FRAG_BUYER_PAY"

class BuyerFragment : Fragment(),
    BuyerItemChildFragment.OnFragmentInteractionListener,
    BuyerPayChildFragment.OnFragmentInteractionListener,
        MainActivity.BackPressedManager
{
    lateinit var rootView : View
    private lateinit var myManager : FragmentManager
    private var childFragmentIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.fragment_buyer, container, false)

        myManager = this.childFragmentManager
//        myManager = (activity as MainActivity).navHost.childFragmentManager

        val buyerItemChildFragment = BuyerItemChildFragment.newInstance(this)
        val buyerPayChildFragment = BuyerPayChildFragment.newInstance(this)
        val transaction = myManager.beginTransaction()
        transaction.add(R.id.buyerFragmentContainer,buyerPayChildFragment, ARG_FRAG_BUYER_PAY)
        transaction.add(R.id.buyerFragmentContainer,buyerItemChildFragment, ARG_FRAG_BUYER_ITEM)
        transaction.hide(buyerPayChildFragment)
        transaction.commit()

        setupWebView ()
        return rootView
    }

    override fun onBackPressed(): Boolean {
        if(childFragmentIndex>0){
            val buyerItemChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_ITEM)
            val buyerPayChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_PAY)

            val transaction = myManager.beginTransaction()
            transaction.setCustomAnimations(
                R.anim.slide_in_left,
                R.anim.slide_out_right,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
            transaction.hide(buyerPayChildFragment!!)
            transaction.show(buyerItemChildFragment!!)
            transaction.commit()
            childFragmentIndex -=1
            return true
        }else{
            return false
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).mSupportActionBar.title = MainModel.broadcastWatching.broadcastName
    }

    override fun onPause() {
        super.onPause()
        println("Buyer Frag onPause")
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).mSupportActionBar.title = getString(R.string.app_name)
    }

    override fun goPayFragment() {
        val buyerItemChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_ITEM)
        val buyerPayChildFragment = myManager.findFragmentByTag(ARG_FRAG_BUYER_PAY)

        val transaction = myManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        transaction.show(buyerPayChildFragment!!)
        transaction.hide(buyerItemChildFragment!!)
//        transaction.addToBackStack(null)
        transaction.commit()

        childFragmentIndex = 1
        //println(myManager.fragments)
    }

    override fun clickCheckPay() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun setupWebView (){
        // https://www.facebook.com/.../videos/.../
        // https://www.facebook.com/video.php?v=10153231379946729
        val webSetting = rootView.streamWebView.settings.apply {
            setJavaScriptEnabled(true)

            setSupportZoom(true)
            setBuiltInZoomControls(true)
            setDisplayZoomControls(false)
            setAllowContentAccess(true)

            setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36")

            setLoadWithOverviewMode(true)
            setUseWideViewPort(true)

//            setAllowUniversalAccessFromFileURLs(true)

            setJavaScriptCanOpenWindowsAutomatically(false)
            setAppCacheEnabled(true)
        }

        rootView.streamWebView.isHorizontalScrollBarEnabled = false
        rootView.streamWebView.isVerticalScrollBarEnabled = false
        rootView.streamWebView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

        //ToDo: 用嵌入式php plugin 要手動開聲音 = =
//        val facebookStreamingSerial = "388355185056271"
        val facebookStreamingSerial = MainModel.broadcastWatching.broadcastID
        val streamString =
            "<script type=\"text/javascript\">\n" +
                    "  function resizeIframe(obj){\n" +
                    "     obj.style.height = 0;\n" +
                    "     obj.style.height = obj.contentWindow.document.body.scrollHeight + 'px';\n" +
                    "  }\n" +
                    "</script>" +

                    "<iframe src=\"https://www.facebook.com/plugins/video.php?href=" +
                    "https://www.facebook.com/facebook/videos/" + facebookStreamingSerial + "/" +
                    "&autoplay=1" +
                    "&allowfullscreen=1" +
                    "&show-text=0" +
                    //"&width=220" +
                    "\"" +
                    "name=\"mainframe\"" +
                    "id=\"mainframe\"" +
                    "width=\"100%\"" +
                    "height=\"95%\" " +
                    "style=\"border:none;overflow:hidden\" " +
                    "scrolling=\"false\" " +
                    "frameborder=\"0\" " +
//                    "marginheight=\"0\" " +
                    "allowTransparency=\"true\" " +
                    "allowFullScreen=\"true\"" +
//                    "onload=\"javascript:resizeIframe(this)\"" +
                    ">" +
                    "</iframe>"

//        val streamString = "<iframe src=\"https://www.facebook.com/plugins/video.php?href=https%3A%2F%2Fwww.facebook.com%2Fsuperloislois%2Fvideos%2F383138968919327%2F&width=500&show_text=true&appId=2244018242588537&height=415&autoplay=1\" width=\"500\" height=\"415\" style=\"border:none;overflow:hidden\" scrolling=\"no\" frameborder=\"0\" allowTransparency=\"true\" allow=\"encrypted-media\" allowFullScreen=\"true\"></iframe>"

//        rootView.streamWebView.webChromeClient = WebChromeClient()

//        val streamString = "<iframe width=\"1148\" height=\"480\" src=\"https://www.youtube.com/embed/M_AHRjmYl_E\" frameborder=\"0\" allow=\"accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture\" allowfullscreen></iframe>"

//        rootView.streamWebView.webViewClient = object:WebViewClient(){
//            override fun onPageFinished(view: WebView?, url: String?) {
//                super.onPageFinished(view, url)
//                println("I'm done!")
//
//                rootView.streamWebView.performClick()
//            }
//        }


        rootView.streamWebView.loadData(streamString, "text/html", "utf-8")
//        rootView.streamWebView.loadUrl("file:///android_asset/JavaScriptTest.html")

//        rootView.streamWebView.loadUrl("https://www.facebook.com/plugins/video.php?href=https://www.facebook.com/facebook/videos/384793132066326/&autoplay=1")
    }


}
