package com.rkhvstnv.projectboard.activities

import android.Manifest
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.MediaStore
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.rkhvstnv.projectboard.Constants
import com.rkhvstnv.projectboard.R

open class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog: Dialog
    /** Next method replace container to corresponding fragment
     *      based on activity which it's called*/
    fun replaceToFragment(activity: BaseActivity, fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            when(activity){
                is ContainerActivity -> replace(R.id.ll_container_container, fragment)
                is MainActivity -> replace(R.id.ll_container_main, fragment)
            }
            setReorderingAllowed(true)
            commit()
        }

    }

    /** Next method replace container to corresponding fragment
     *      based on activity which it's called. Also add fragment to BackStack*/
    fun replaceToFragmentAndBackStack(activity: BaseActivity, fragment: Fragment){
        supportFragmentManager.beginTransaction().apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            when(activity){
                is ContainerActivity -> replace(R.id.ll_container_container, fragment)
                is MainActivity -> replace(R.id.ll_container_main, fragment)
            }
            setReorderingAllowed(true)

            addToBackStack(null)
            commit()
        }
    }

    /** Next method initialize action bar with received title*/
    open fun setupActionBar(mTitle: String){
        val toolbar = findViewById<Toolbar>(R.id.tool_bar)
        toolbar.title = mTitle
        setSupportActionBar(toolbar)
    }

    /** Next method show snack bar*/
    fun showSnackBarMessage(context: Context, message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.mDarkGreen))
        snackBar.setTextColor(ContextCompat.getColor(context, R.color.mWhite))
        snackBar.show()
    }

    /** Next two methods show/hide progress dialog of background process*/
    fun showProgressDialog(context: Context){
        progressDialog = Dialog(context)
        progressDialog.setContentView(R.layout.progress_dialog)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.window?.setGravity(Gravity.TOP)
        progressDialog.setCanceledOnTouchOutside(false)
        progressDialog.show()
    }
    fun hideProgressDialog(){
        progressDialog.dismiss()
    }

    /**
     * Next dialog will be shown, if previously user reject all permissions offers
     *      Required for gallery features
     */
    fun showRationalPermissionDialog(context: Context){
        val permissionAlertDialog = AlertDialog.Builder(context).setMessage(R.string.st_permission_needed_to_be_granted)
        //positive button
        permissionAlertDialog.setPositiveButton(getString(R.string.st_go_to_settings)){ _: DialogInterface, _: Int ->
            try {
                /** Move to app settings*/
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
            }
        }
        //negative button
        permissionAlertDialog.setNegativeButton(R.string.st_cancel){ dialogInterface: DialogInterface, _: Int ->
            dialogInterface.dismiss()
        }

        //show
        permissionAlertDialog.show()
    }

    /** Next fun check that permission for gallery reading has been granted.
     *      If it's not, will request permissions*/
    fun getImageWithPermissionCheck(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            //get image
            getImageFromGallery()
        } else{
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Constants.READ_STORAGE_PERMISSION_CODE)
        }
    }

    /** Next method start intent of Gallery */
    fun getImageFromGallery(){
        val pickImageIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickImageIntent, Constants.GALLERY_REQUEST_CODE)
    }

    /** Next method get file extension
     *      Required for image saving in storage, due to before uploading
     *      will generate unique name of file*/
    //file extension (example .png)
    fun getFileExtension(uri: Uri?): String?{
        return MimeTypeMap.getSingleton()
            .getExtensionFromMimeType(contentResolver.getType(uri!!))
    }





}