package com.game.aries.streamingshop.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import com.game.aries.streamingshop.model.Broadcast
import com.game.aries.streamingshop.R
import kotlinx.android.synthetic.main.adapter_broadcastlist.view.*

class BroadcastListAdapter (val context: Context, val broadcastList: List<Broadcast>, val listener: AdapterListener):
    RecyclerView.Adapter<BroadcastListAdapter.ViewHolder>() {

    interface AdapterListener{
        fun clickBroadcast(selectedBroadcast: Broadcast)
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.adapter_broadcastlist, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = this.broadcastList.count()

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.bind(p1, broadcastList[p1], listener)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val broadcastPreviewWebView = itemView.broadcastPreviewWebView
        private val broadcastNameTextView = itemView.broadcastNameTextView
        private val adapterCover = itemView.adapterCover

        fun bind(index:Int, broadcast: Broadcast, listener: AdapterListener) {
            setupWebView(broadcast)
            broadcastNameTextView.text = broadcast.broadcastName
            adapterCover.setOnClickListener { listener.clickBroadcast(broadcast) }
        }

        fun setupWebView(broadcast: Broadcast){
            val streamString =
                "<iframe src=\"https://www.facebook.com/plugins/video.php?href=" +
                    "https://www.facebook.com/facebook/videos/" + broadcast.broadcastID + "/" +
                        "&autoplay=0" +
                        "&allowfullscreen=0" +
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
                    "marginheight=\"0\" " +
                    "allowTransparency=\"true\" " +
                    "allowFullScreen=\"true\"" +
                    ">" +
                "</iframe>"

            broadcastPreviewWebView.settings.apply {
                setJavaScriptEnabled(true)

                setSupportZoom(false)
                setBuiltInZoomControls(false)
                setDisplayZoomControls(false)
                setAllowContentAccess(true)

                setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.134 Safari/537.36")

                setLoadWithOverviewMode(true)
                setUseWideViewPort(true)

                setJavaScriptCanOpenWindowsAutomatically(false)
                setAppCacheEnabled(false)
            }

            broadcastPreviewWebView.isHorizontalScrollBarEnabled = false
            broadcastPreviewWebView.isVerticalScrollBarEnabled = false
            broadcastPreviewWebView.overScrollMode = ScrollView.OVER_SCROLL_NEVER

            broadcastPreviewWebView.setOnTouchListener { v, event ->  true}

            broadcastPreviewWebView.loadData(streamString, "text/html", "utf-8")
        }

    }
}