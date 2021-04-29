package com.rkhvstnv.projectboard.fragments


import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.provider.Settings
import android.view.Gravity
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.rkhvstnv.projectboard.R


open class BaseFragment : Fragment() {
    private lateinit var progressDialog: Dialog

    /** Next method show snack bar*/
    fun showSnackBarMessage(context: Context, message: String){
        val snackBar = Snackbar.make(requireActivity().findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.mDarkGreen))
        snackBar.setTextColor(ContextCompat.getColor(context, R.color.mWhite))
        snackBar.show()
    }

    /** Next method replace container to corresponding fragment */
    fun replaceToFragment(fragment: Fragment){
        fragmentManager?.beginTransaction()?.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container_container, fragment)
            setReorderingAllowed(true)
            commit()
        }
    }

    /** Next method replace container to corresponding fragment.
     *  Also add fragment to BackStack*/
    fun replaceToFragmentAndBackStack(fragment: Fragment){
        fragmentManager?.beginTransaction()?.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container_container, fragment)
            setReorderingAllowed(true)

            addToBackStack(null)
            commit()
        }
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






}