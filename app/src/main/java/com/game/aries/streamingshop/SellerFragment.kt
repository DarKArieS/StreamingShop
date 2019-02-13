package com.game.aries.streamingshop

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.game.aries.streamingshop.adapter.SellerAdapter
import com.game.aries.streamingshop.dialog.EditBroadcastDialog
import com.game.aries.streamingshop.model.MainModel
import com.game.aries.streamingshop.model.SellerItem
import com.game.aries.streamingshop.utilities.MenuInterface
import kotlinx.android.synthetic.main.fragment_seller.view.*
import java.io.File
import java.lang.Exception
import android.os.Build
import android.support.v4.content.FileProvider
import android.widget.Toast
import androidx.navigation.findNavController
import com.game.aries.streamingshop.dialog.AlarmDialog
import com.game.aries.streamingshop.dialog.GetImageDialog
import com.game.aries.streamingshop.model.UploadState
import com.game.aries.streamingshop.utilities.CommunicationManager
import com.game.aries.streamingshop.utilities.generateOrderId

private const val  ACT_INTENT_GET_IMAGE = 100
private const val  ACT_INTENT_CAPTURE_IMAGE = 101
private const val  ACT_INTENT_CORP_IMAGE = 102

class SellerFragment : Fragment(), MenuInterface,
    EditBroadcastDialog.EditBroadcast,
    GetImageDialog.DialogListener,
    SellerAdapter.AdapterListener,
    MainActivity.BackPressedManager {
    lateinit var rootView : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_seller, container, false)

        //read stored information
        MainModel.loadSellerInfo(activity as ContextWrapper)
        checkBroadcastID()

        // setup adapter
        setupAdapter(MainModel.sellerItemList)

        // setup refresh
        rootView.swipeRefreshLayout.setOnRefreshListener{
            val newSellerItemList = mutableListOf<SellerItem>()
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Updating"
            cManager.communication = { p0,p1->
                MainModel.getSellerItemList(p0,p1,newSellerItemList)
            }
            cManager.customCallback = {
                Thread{
                    MainModel.updateOldSellerItemList(newSellerItemList,MainModel.sellerItemList)
                    (activity as MainActivity).runOnUiThread {
                        setupAdapter(MainModel.sellerItemList)
                        rootView.swipeRefreshLayout.isRefreshing=false
                    }
                }.start()
            }
            cManager.commit(activity as MainActivity)
        }

        // setup button
        rootView.addItemFloatingActionButton.setOnClickListener { clickAddItemFloatingActionButton() }
        rootView.startStreamFloatingActionButton.setOnClickListener{ clickStartStreamFloatingActionButton() }

        // debug
/*
        rootView.debugFloatingActionButton.setOnClickListener {
//            println("========================================================")
//            for ( (i,a) in MainModel.sellerItemList.withIndex())
//                println( i.toString() +": " + a.toString())

            for ( (i,a) in viewHolderSubscribers.withIndex())
                println( i.toString() +": " + a.hashCode())

            for (subscriber in viewHolderSubscribers)
            {
                subscriber.subscriberTest()
            }
        }
*/
        return rootView
    }

    override fun onResume() {
        super.onResume()
        setSellerFragmentActionBar(true)
    }

    override fun onStop() {
        super.onStop()
        setSellerFragmentActionBar(false)
        MainModel.saveSellerInfo(activity as ContextWrapper)
    }

    class BackPressAlarmDialog: AlarmDialog(){
        override fun clickEnterButton() {
            (activity as MainActivity).findNavController(R.id.navHost).navigateUp()
        }
    }

    override fun onBackPressed(): Boolean {
        if(!MainModel.isBroadcasting){
            var isChanged = false
            for (a in MainModel.sellerItemList)
                if (a.uploadState != UploadState.UPLOAD_DONE) isChanged = true
            if(isChanged){
                val alarmDialog = BackPressAlarmDialog()
                alarmDialog.setAlarmMessage("將會遺失尚未儲存的資訊，確定嗎？")
                alarmDialog.show(fragmentManager, "BackPressedAlarmDialog")
            }else return false
        }
        return true
    }

    private fun setSellerFragmentActionBar(setTrue:Boolean){
        when(setTrue){
            true->{
                (activity as MainActivity).mSupportActionBar.title = MainModel.sellerBroadcast.broadcastName
                if(MainModel.isBroadcasting){
                    (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = false
                    (activity as MainActivity).menuInterface = null
                    (activity as MainActivity).setExistNavigationDrawer(false)
                }else{
                    (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = true
                    (activity as MainActivity).menuInterface = this
                    (activity as MainActivity).setExistNavigationDrawer(true)
                }
            }
            false->{
                (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = false
                (activity as MainActivity).mSupportActionBar.title = getString(R.string.app_name)
                (activity as MainActivity).menuInterface = null
                (activity as MainActivity).setExistNavigationDrawer(true)
            }
        }
    }

    override fun actionBarEdit(){
        val editNameDialog = EditBroadcastDialog.newInstance(this)
        editNameDialog.show(fragmentManager, "EditNameDialog")
    }

    override fun editBroadcast() {
        (activity as MainActivity).mSupportActionBar.title = MainModel.sellerBroadcast.broadcastName
        checkBroadcastID()
    }

    private fun checkBroadcastID(){
        if (MainModel.sellerBroadcast.broadcastID.length>=15){
//            rootView.startStreamFloatingActionButton.isClickable = true
            rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_start_vector)
        }else{
//            rootView.startStreamFloatingActionButton.isClickable = false
            rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_forbidden_vector)
        }
    }

    private fun setupAdapter(itemList:MutableList<SellerItem>) {
        viewHolderSubscribers = mutableListOf()
        rootView.sellerRecyclerView.adapter = SellerAdapter(this.context!!, itemList, this, activity as MainActivity)
        rootView.sellerRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }

    private var viewHolderSubscribers = mutableListOf<SellerAdapter.ViewHolder>()

    override fun adapterSubscribeViewHolder(viewHolder: SellerAdapter.ViewHolder) {
        viewHolderSubscribers.add(viewHolder)
    }

    private fun clickStartStreamFloatingActionButton(){
        if(MainModel.isBroadcasting){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Stopping"
            cManager.communication = { p0,p1->
                MainModel.endSellerStream(p0,p1)
            }
            cManager.customCallback = {
                rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_start_vector)
                MainModel.isBroadcasting=false
                setSellerFragmentActionBar(true)
                for (subscriber in viewHolderSubscribers)
                    if(subscriber.adapterPosition>=0)
                        subscriber.buttonStateUpdate(MainModel.sellerItemList[subscriber.adapterPosition])
            }
            cManager.commit(activity as MainActivity)
        }
        else if (MainModel.sellerBroadcast.broadcastID.length>=15){
            val cManager = CommunicationManager()
            cManager.loadingMessage = "Starting"
            cManager.communication = { p0,p1->
                MainModel.startSellerStream(p0,p1)
            }
            cManager.customCallback = {
                MainModel.isBroadcasting=true
                setSellerFragmentActionBar(true)
                rootView.startStreamFloatingActionButton.setImageResource(R.drawable.ic_streaming_vector)
                for (subscriber in viewHolderSubscribers)
                    if(subscriber.adapterPosition>=0)
                        subscriber.buttonStateUpdate(MainModel.sellerItemList[subscriber.adapterPosition])
            }
            cManager.commit(activity as MainActivity)
        }else{
            Toast.makeText(activity as MainActivity, "請輸入有效的Facebook 直播ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clickAddItemFloatingActionButton(){
        MainModel.sellerItemList.add(0, SellerItem())
        rootView.sellerRecyclerView.adapter?.notifyItemRangeInserted(0,1)
        rootView.sellerRecyclerView.scrollToPosition(0)
    }

    override fun adapterSetImage(viewHolder: SellerAdapter.ViewHolder) {
        val getImageDialog = GetImageDialog.newInstance(this)
        getImageDialog.show(fragmentManager, "GetImageDialog")
        currentViewHolder = viewHolder
    }

    class DeleteAlarmDialog: AlarmDialog(){
        var currentViewHolder: SellerAdapter.ViewHolder? = null
        override fun clickEnterButton() {
            currentViewHolder!!.deleteItem(MainModel.sellerItemList[currentViewHolder!!.adapterPosition])
        }
    }

    override fun adapterDeleteItem(viewHolder: SellerAdapter.ViewHolder) {
        val alarmDialog = DeleteAlarmDialog()
        alarmDialog.setAlarmMessage("確定要刪除商品嗎？")
        alarmDialog.show(fragmentManager, "DeleteAlarmDialog")
        alarmDialog.currentViewHolder = viewHolder
        currentViewHolder = viewHolder
    }

    override fun chooseImageResource(action: GetImageDialog.Action) {
        when(action){
            GetImageDialog.Action.CAMERA->imageCaptureIntent()
            GetImageDialog.Action.ALBUM->imageGetIntent()
        }
    }

    private var imageUri:Uri? = null
    private var corpedImageUri:Uri? = null
    private var currentViewHolder: SellerAdapter.ViewHolder? = null

    private fun imageCaptureIntent(){
//        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//        this.startActivityForResult(intent, ACT_INTENT_CAPTURE_IMAGE)

        val tmpFile = File(MainModel.tmpExternalFile,"captureImg.png")
        try {
            if(tmpFile.exists()) tmpFile.delete()
            tmpFile.createNewFile()
        }catch (e:Exception){
            println("capture image Error Orz !~~~!!~~")
        }
        imageUri = Uri.fromFile(tmpFile)
        if (Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(
                activity as MainActivity,
                "com.game.aries.streamingshop.fileprovider",
                tmpFile)
        }

        val intent = Intent("android.media.action.IMAGE_CAPTURE")
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, ACT_INTENT_CAPTURE_IMAGE)
    }

    private fun imageGetIntent() {
//        val takePhotoRequest = 0
//        val intent = Intent(Intent.ACTION_GET_CONTENT)
//        intent.type = "image*//**//*"
//        this.startActivityForResult(intent,takePhotoRequest)

        val intent = Intent(Intent.ACTION_PICK)
        intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        startActivityForResult(intent, ACT_INTENT_GET_IMAGE)
    }

    private fun imageCorpIntent(isFromCamera: Boolean = false){
        val intent = Intent("com.android.camera.action.CROP")
        if (isFromCamera && Build.VERSION.SDK_INT >= 24)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        // set cut file
        val cutFile = File(
            //Environment.getExternalStorageDirectory().path,
            MainModel.tmpExternalFile,"localCutImg_" + generateOrderId() + ".png")

        try {
            if(cutFile.exists()) cutFile.delete()
            cutFile.createNewFile()
        }catch (e:Exception){
            println("corp image Error Orz !~~~!!~~")
        }

        val outputUri = Uri.fromFile(cutFile)
        println("cutFile outputUri")
        println(outputUri)

        intent.putExtra("crop",true)
        // set X/Y ratio
        intent.putExtra("aspectX",1)
        intent.putExtra("aspectY",1)

        //set size in px
        intent.putExtra("outputX",250)
        intent.putExtra("outputY",250)

        // return uri
        intent.putExtra("return-data",false)

        if (imageUri != null) {
            intent.setDataAndType(imageUri, "image/*")
        }
        if (outputUri != null) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
        }
        intent.putExtra("noFaceDetection", true)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())

        startActivityForResult(intent, ACT_INTENT_CORP_IMAGE)
        corpedImageUri = outputUri
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == RESULT_CANCELED){
            imageUri = null
        }

        if(requestCode == ACT_INTENT_GET_IMAGE && resultCode == RESULT_OK){
            println("ACT_INTENT_GET_IMAGE")
            imageUri = data?.data
            println(imageUri)
            imageCorpIntent()
        }

        if(requestCode == ACT_INTENT_CAPTURE_IMAGE && resultCode == RESULT_OK){
            println("ACT_INTENT_CAPTURE_IMAGE")
            println(imageUri)
            imageCorpIntent(true)
        }

        if(requestCode == ACT_INTENT_CORP_IMAGE && resultCode == RESULT_OK){
            println("ACT_INTENT_CORP_IMAGE")
            println(data)
            updateAdapterImage(corpedImageUri,currentViewHolder!!.adapterPosition)
        }
    }

    private fun updateAdapterImage(uri:Uri?, index:Int){
        MainModel.sellerItemList[index].picture = uri.toString()
        currentViewHolder!!.updateImage(MainModel.sellerItemList[index])
    }
}
