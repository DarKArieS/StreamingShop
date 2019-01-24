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
import com.game.aries.streamingshop.dialog.GetImageDialog
import com.game.aries.streamingshop.utilities.CommunicationManager
import com.game.aries.streamingshop.utilities.generateOrderId

private const val  ACT_INTENT_GET_IMAGE = 100
private const val  ACT_INTENT_CAPTURE_IMAGE = 101
private const val  ACT_INTENT_CORP_IMAGE = 102

class SellerFragment : Fragment(), MenuInterface,
    EditBroadcastDialog.EditBroadcast,
    GetImageDialog.DialogListener,
    SellerAdapter.AdapterListener {
    lateinit var rootView : View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_seller, container, false)

        //read stored information
        MainModel.loadSellerInfo(activity as ContextWrapper)

        // setup adapter
        MainModel.sellerItemList = mutableListOf(
            SellerItem()
        )
        setupAdapter(MainModel.sellerItemList)

        // setup refresh
        rootView.swipeRefreshLayout.setOnRefreshListener{
            setupAdapter(MainModel.sellerItemList)
            rootView.swipeRefreshLayout.isRefreshing=false
        }

        // setup button
        rootView.addItemFloatingActionButton.setOnClickListener { clickAddItem() }
        rootView.startStreamFloatingActionButton.setOnClickListener{ clickStartStreamFloatingActionButton() }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        setSellerFragment(true)
    }

    override fun onStop() {
        super.onStop()
        setSellerFragment(false)
        MainModel.saveSellerInfo(activity as ContextWrapper)
    }

    override fun actionEdit(){
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

    private fun clickStartStreamFloatingActionButton(){
        if (MainModel.sellerBroadcast.broadcastID.length>=15){
            val cManager = CommunicationManager()
            cManager.communication = {
                p0,p1,_->
                MainModel.ttnCreateBroadcast(p0, p1)
            }
            cManager.commit(activity as MainActivity)
        }else{
            Toast.makeText(activity as MainActivity, "請輸入Facebook 直播ID", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSellerFragment(setTrue:Boolean){
        when(setTrue){
            true->{
                checkBroadcastID()
                (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = true
                (activity as MainActivity).mSupportActionBar.title = MainModel.sellerBroadcast.broadcastName
                (activity as MainActivity).menuInterface = this
            }
            false->{
                (activity as MainActivity).customMenu.findItem(R.id.action_edit).isVisible = false
                (activity as MainActivity).mSupportActionBar.title = getString(R.string.app_name)
                (activity as MainActivity).menuInterface = null
            }
        }
    }

    private fun setupAdapter(itemList:List<SellerItem>) {
        rootView.sellerRecyclerView.adapter = SellerAdapter(this.context!!, itemList, this, activity as MainActivity)
        rootView.sellerRecyclerView.layoutManager = LinearLayoutManager(this.context!!)
    }

    private fun clickAddItem(){
        MainModel.sellerItemList.add(0, SellerItem())
        setupAdapter(MainModel.sellerItemList)
    }

    override fun deleteItem(index: Int) {
        MainModel.sellerItemList.removeAt(index)
        setupAdapter(MainModel.sellerItemList)
    }

    override fun clickImage(index:Int) {
        val getImageDialog = GetImageDialog.newInstance(this)
        getImageDialog.show(fragmentManager, "GetImageDialog")
        selectedItemIndex = index
    }

    override fun clickButton(action: GetImageDialog.Action) {
        when(action){
            GetImageDialog.Action.CAMERA->imageCaptureIntent()
            GetImageDialog.Action.ALBUM->imageGetIntent()
        }
    }

    private var imageUri:Uri? = null
    private var corpedImageUri:Uri? = null
    private var selectedItemIndex = 0

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
            setImage(corpedImageUri,selectedItemIndex)
        }
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

    private fun setImage(uri:Uri?, index:Int){
        MainModel.sellerItemList[index].picture = uri.toString()
        (rootView.sellerRecyclerView.adapter as SellerAdapter).currentViewHolder?.updateImage(MainModel.sellerItemList[index])
    }
}
