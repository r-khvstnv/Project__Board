package com.rkhvstnv.projectboard.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.bumptech.glide.Glide
import com.rkhvstnv.projectboard.Constants
import com.rkhvstnv.projectboard.MyCallBack
import com.rkhvstnv.projectboard.MyFirebaseClass
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.databinding.ActivityNewBoardBinding
import com.rkhvstnv.projectboard.models.BoardData
import java.lang.Exception

class NewBoardActivity : BaseActivity() {
    private lateinit var binding: ActivityNewBoardBinding
    private lateinit var beenAttachedToBoards: ArrayList<String>
    private lateinit var currentUserId: String
    private var boardImageURL: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(getString(R.string.st_new_board))

        //get attached boards list
        if (intent.hasExtra(Constants.INTENT_EXTRA_BEEN_ATTACHED_TO_BOARDS_LIST)){
            currentUserId = intent.getStringExtra(Constants.INTENT_EXTRA_CURRENT_USER_ID)!!
            beenAttachedToBoards = intent.getStringArrayListExtra(
                Constants.INTENT_EXTRA_BEEN_ATTACHED_TO_BOARDS_LIST)!!
        }

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
                        //store board image with uri
                        requestBoardImageUploading(data.data!!)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    private fun requestBoardImageUploading(uri: Uri){
        showProgressDialog(this)
        val pathString = Constants.BOARD_IMAGE + System.currentTimeMillis() +
                "." + getFileExtension(uri)

        MyFirebaseClass().uploadImage(uri, pathString, object : MyCallBack {
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

    private fun requestBoardCreation(){
        showProgressDialog(this)

        val boardTitle = binding.etBoardName.text.toString()
        val assignedUsersList: ArrayList<String> = ArrayList()
        assignedUsersList.add(currentUserId)

        val boardData = BoardData(currentUserId, boardTitle, boardImageURL, assignedUsersList)
        //create new document in firebase
        MyFirebaseClass().createNewBoard(boardData, object : MyCallBack{
            override fun onCallbackSuccess(any: Any) {
                //get document ID of new board
                val boardId: String = any as String

                //save it's id in user object and update user data in fb
                beenAttachedToBoards.add(boardId)
                val userHashMap = HashMap<String, Any>()
                userHashMap[Constants.HASH_MAP_BEEN_ATTACHED_TO_BOARDS] = beenAttachedToBoards

                MyFirebaseClass().updateUserData(userHashMap, object : MyCallBack{
                    override fun onCallbackSuccess(any: Any) {
                        hideProgressDialog()
                        finish()//todo add resultOk
                    }

                    override fun onCallbackErrorMessage(message: String) {
                        hideProgressDialog()
                        showSnackBarMessage(this@NewBoardActivity, message)
                    }

                })

            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@NewBoardActivity, message)
            }

        })
    }
}