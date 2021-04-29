package com.rkhvstnv.projectboard.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import com.bumptech.glide.Glide
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.ActivityProfileBinding
import com.rkhvstnv.projectboard.models.UserDataClass
import java.lang.Exception

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private var userImageURL: String? = null
    private lateinit var obtainedUserData: UserDataClass
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar(getString(R.string.st_profile))
        updateUserDataInView()
        binding.civUserImage.setOnClickListener {
            getImageWithPermissionCheck()
        }
        binding.llSaveChanges.setOnClickListener {
            requestUserDataUpdate()
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



    private fun requestUserImageUploading(uri: Uri){
        showProgressDialog(this)
        val pathString = Constants.USER_IMAGE + System.currentTimeMillis() +
                "." + getFileExtension(uri)

        MyFirebaseClass().uploadImage(uri, pathString, object : MyCallBack{
            override fun onCallbackSuccess(any: Any) {
                userImageURL = any as String
                hideProgressDialog()
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@ProfileActivity, message)
            }

        })
    }

    private fun updateUserDataInView(){
        showProgressDialog(this)
        MyFirebaseClass().getSignedInUserData(object : MyCallBack {
            override fun onCallbackSuccess(any: Any) {
                //save data to check further changes
                obtainedUserData = any as UserDataClass
                Glide.with(this@ProfileActivity)
                    .load(obtainedUserData.imageProfile).fitCenter()
                    .placeholder(R.drawable.ic_profile).into(binding.civUserImage)

                binding.tvTitleUsername.text = obtainedUserData.name
                binding.tvTitleEmail.text = obtainedUserData.email

                binding.etName.setText(obtainedUserData.name)
                binding.etPhone.setText(obtainedUserData.phone)
                hideProgressDialog()
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(this@ProfileActivity, message)
            }
        })
    }

    //updates
    private fun requestUserDataUpdate(){
        //initialize hashMap
        val userHashMap = HashMap<String, Any>()
        //start from top of layout
        if (userImageURL != null && userImageURL != obtainedUserData.imageProfile){
            userHashMap[Constants.HASH_MAP_IMAGES] = userImageURL!!
        }

        if (binding.etName.text.toString() != obtainedUserData.name){
            userHashMap[Constants.HASH_MAP_NAME] = binding.etName.text.toString()
        }

        if (binding.etPhone.text.toString() != obtainedUserData.phone){
            userHashMap[Constants.HASH_MAP_PHONE] = binding.etPhone.text.toString()
        }

        showProgressDialog(this)

        if (binding.etNewPassword.text!!.isNotEmpty()){
            if (binding.etNewPassword.text.toString()
                == binding.etNewPasswordConfirm.text.toString()){
                requestUserPasswordUpdate()
            } else{
                showSnackBarMessage(this, getString(R.string.st_password_mismatch))
            }
        }

        MyFirebaseClass().updateUserData(userHashMap, object : MyCallBack {
            override fun onCallbackSuccess(any: Any) {}

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                if (message.isEmpty()){
                    setResult(Activity.RESULT_OK)
                    finish()
                    //set request key for confirmation of profile changes
                    //todo result ok

                } else{
                    showSnackBarMessage(this@ProfileActivity, message)
                }
            }
        })
    }


    private fun requestUserPasswordUpdate(){
        MyFirebaseClass().updateUserPassword(
            binding.etNewPassword.text.toString(), object : MyCallBack {
                override fun onCallbackSuccess(any: Any) {}

                override fun onCallbackErrorMessage(message: String) {
                    if (message.isEmpty()) {
                        showSnackBarMessage(
                            this@ProfileActivity,
                            getString(R.string.st_password_changed)
                        )
                    } else {
                        showSnackBarMessage(this@ProfileActivity, message)
                    }
                }

            })
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
                            .placeholder(R.drawable.ic_profile).into(binding.civUserImage)
                        //store new user image uri
                        requestUserImageUploading(data.data!!)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}