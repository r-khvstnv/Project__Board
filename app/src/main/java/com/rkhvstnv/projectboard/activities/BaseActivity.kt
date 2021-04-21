package com.rkhvstnv.projectboard.activities

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.view.Gravity
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.rkhvstnv.projectboard.R

open class BaseActivity : AppCompatActivity() {
    private lateinit var progressDialog: Dialog
    fun showSnackBarMessage(context: Context, message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.mDarkGreen))
        snackBar.setTextColor(ContextCompat.getColor(context, R.color.mWhite))
        snackBar.show()
    }

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
}