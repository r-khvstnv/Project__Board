package com.rkhvstnv.projectboard.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.FragmentProfileBinding
import java.lang.Exception

class ProfileFragment : BaseFragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private var userImageURL: String? = null
    private lateinit var obtainedUserData: UserDataClass
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)

        updateUserDataInView()

        binding.civUserImage.setOnClickListener {
            getImageWithPermissionCheck()
        }
        binding.llSaveChanges.setOnClickListener {
            requestUserDataUpdate()
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        userImageURL = null
    }

    private fun getImageWithPermissionCheck(){
        when{
            ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED ->{

                //get image
                getImageFromGallery()
            }
            else -> {
                //request permissions
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    Constants.READ_STORAGE_PERMISSION_CODE)
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
                showRationalPermissionDialog(requireContext())
            }
        }
    }

    private fun getImageFromGallery(){
        val pickImageIntent = Intent(Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, Constants.GALLERY_REQUEST_CODE)
    }
    //file extension (example .png)
    private fun getFileExtension(uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(activity?.contentResolver?.getType(uri!!))
    }

    private fun uploadUserImage(uri: Uri){
        showProgressDialog(requireContext())
        val pathString = Constants.USER_IMAGE + System.currentTimeMillis() +
                "." + getFileExtension(uri)
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().reference.child(pathString)
        storageReference.putFile(uri).addOnSuccessListener { taskSnapshot ->
            //todo
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                userImageURL = it.toString()
            }
            hideProgressDialog()
        }.addOnFailureListener { exception ->
            showSnackBarMessage(requireContext(), exception.message!!)
            hideProgressDialog()
        }
    }

    private fun updateUserDataInView(){
        showProgressDialog(requireContext())
        MyFirebaseClass().getSignedInUserData(object : MyCallBack{
            override fun onCallbackObject(userData: UserDataClass) {
                //save data to check further changes
                obtainedUserData = userData
                Glide.with(requireActivity())
                    .load(userData.imageProfile).fitCenter()
                    .placeholder(R.drawable.ic_profile).into(binding.civUserImage)

                binding.tvTitleUsername.text = userData.name
                binding.tvTitleEmail.text = userData.email

                binding.etName.setText(userData.name)
                binding.etPhone.setText(userData.phone)
                hideProgressDialog()
            }

            override fun onCallbackErrorMessage(message: String) {
                hideProgressDialog()
                showSnackBarMessage(requireContext(), message)
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

        if (binding.etNewPassword.text!!.isNotEmpty()){
            if (binding.etNewPassword.text.toString()
                == binding.etNewPasswordConfirm.text.toString()){
                requestUserPasswordUpdate()
            } else{
                showSnackBarMessage(requireContext(), getString(R.string.st_password_mismatch))
            }
        }

        MyFirebaseClass().updateUserData(userHashMap, object : MyCallBack{
            override fun onCallbackObject(userData: UserDataClass) {}

            override fun onCallbackErrorMessage(message: String) {
                if (message.isEmpty()){
                    onBackPressed()
                    //set request key for confirmation of profile changes
                    setFragmentResult(Constants.PROFILE_CHANGES_KEY,
                        bundleOf(Constants.PROFILE_BUNDLE_KEY to true))

                } else{
                    showSnackBarMessage(requireContext(), message)
                }
            }
        })
    }


    private fun requestUserPasswordUpdate(){
        MyFirebaseClass().updateUserPassword(
            binding.etNewPassword.text.toString(), object : MyCallBack {
                override fun onCallbackObject(userData: UserDataClass) {}

                override fun onCallbackErrorMessage(message: String) {
                    if (message.isEmpty()) {
                        showSnackBarMessage(
                            requireContext(),
                            getString(R.string.st_password_changed)
                        )
                    } else {
                        showSnackBarMessage(requireContext(), message)
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
                        Glide.with(requireActivity())
                            .load(data.data).fitCenter()
                            .placeholder(R.drawable.ic_profile).into(binding.civUserImage)
                        //store new user image uri
                        uploadUserImage(data.data!!)
                    }
                }
                catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }

}