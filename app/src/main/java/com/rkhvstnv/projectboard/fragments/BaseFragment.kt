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

    fun showSnackBarMessage(context: Context, message: String){
        val snackBar = Snackbar.make(activity!!.findViewById(android.R.id.content),
            message, Snackbar.LENGTH_LONG)
        snackBar.view.setBackgroundColor(ContextCompat.getColor(context, R.color.mDarkGreen))
        snackBar.setTextColor(ContextCompat.getColor(context, R.color.mWhite))
        snackBar.show()
    }

    fun replaceToFragment(fragment: Fragment){
        fragmentManager?.beginTransaction()?.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container_container, fragment)
            setReorderingAllowed(true)
            commit()
        }
    }

    fun replaceToFragmentAndBackStack(fragment: Fragment){
        fragmentManager?.beginTransaction()?.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container_container, fragment)
            setReorderingAllowed(true)
            //add to stack for backing to it
            addToBackStack(null)
            commit()
        }
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

    fun onBackPressed(){
        val fm = fragmentManager
        if (fm?.backStackEntryCount!! > 0){
            fm.popBackStack()
        }
    }

    /**
     * Next dialog will be shown, if previously user reject all permissions
     * required to gallery & share features
     */
    fun showRationalPermissionDialog(context: Context){
        val permissionAlertDialog = AlertDialog.Builder(context).setMessage(R.string.st_permission_needed_to_be_granted)
        //positive button
        permissionAlertDialog.setPositiveButton(getString(R.string.st_go_to_settings)){ _: DialogInterface, _: Int ->
            try {
                //move to app settings
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", activity?.packageName, null)
                intent.data = uri
                startActivity(intent)
            }catch (e: ActivityNotFoundException){
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
    /**
     * Dexter wasn't implemented due to fragment implementation
     * */


}