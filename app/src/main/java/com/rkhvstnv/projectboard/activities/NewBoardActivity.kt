package com.rkhvstnv.projectboard.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rkhvstnv.projectboard.Constants
import com.rkhvstnv.projectboard.MyCallBack
import com.rkhvstnv.projectboard.FirebaseClass
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.databinding.ActivityNewBoardBinding
import com.rkhvstnv.projectboard.models.BoardData
import java.lang.Exception

class NewBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityNewBoardBinding
    private lateinit var currentUserId: String
    private var boardImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(getString(R.string.st_new_board))

        /** Get all extra data for activity*/
        if (intent.hasExtra(Constants.EXTRA_CURRENT_USER_ID)){
            currentUserId = intent.getStringExtra(Constants.EXTRA_CURRENT_USER_ID)!!
        }

        /**Listeners */
        binding.civBoardImage.setOnClickListener {
            getImageWithPermissionCheck()
        }
        binding.llCreateBoard.setOnClickListener {
            if (binding.etBoardName.text.isNullOrEmpty()){
                showSnackBarMessage(this, getString(R.string.st_enter_data))
            } else{
                requestBoardCreation()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE){
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getImageFromGallery()
            } else{
                showRationalPermissionDialog(this)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == Constants.GALLERY_REQUEST_CODE){
                try {
                    if (data!!.data != null){
                        //substitute image
                        Glide.with(this)
                            .load(data.data).fitCenter()
                            .placeholder(R.drawable.ic_profile).into(binding.civBoardImage)
                        /** Store new image in fireStore*/
                        requestBoardImageUploading(data.data!!)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    /** Next method request image storing in fireStore, having prepared new fileName.
     *      After successful execution will store it's URL in boardImageURL for further
     *      implementation
     *    */
    private fun requestBoardImageUploading(uri: Uri){
        showProgressDialog(this)
        val pathString = Constants.BOARD_IMAGE + System.currentTimeMillis() +
                "." + getFileExtension(uri)

        FirebaseClass().uploadImage(uri, pathString, object : MyCallBack {
            override fun onCallbackSuccess(any: Any) {
                boardImageURL = any as String
                hideProgressDialog()
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@NewBoardActivity, message)
            }

        })
    }

    /** Next method prepare all data for uploading
     *      and after that will call corresponding method*/
    private fun requestBoardCreation(){
        showProgressDialog(this)
        /** Set id for document (document path)*/
        val documentPath: String = currentUserId + System.currentTimeMillis()
        val boardTitle = binding.etBoardName.text.toString()
        /** By default add creator to assigned user's.
         *  Later, new users can be added in board details itself*/
        val assignedUsersList: ArrayList<String> = ArrayList()
        assignedUsersList.add(currentUserId)
        /** Create full-fledged object*/
        val boardData = BoardData(documentPath, currentUserId,
            boardTitle, boardImageURL, assignedUsersList)
        /** Create new document in firebase*/
        FirebaseClass().createNewBoard(documentPath, boardData, object : MyCallBack{
            override fun onCallbackSuccess(any: Any) {
                hideProgressDialog()
                setResult(Activity.RESULT_OK)
                finish()//todo result ok
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@NewBoardActivity, message)
            }
        })
    }
}