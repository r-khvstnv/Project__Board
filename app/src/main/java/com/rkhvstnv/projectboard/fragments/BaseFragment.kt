package com.rkhvstnv.projectboard.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Message
import android.text.TextUtils.replace
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.constraintlayout.widget.Constraints
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.rkhvstnv.projectboard.MainActivity
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.databinding.FragmentBaseBinding


open class BaseFragment : Fragment() {
    private var _binding: FragmentBaseBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressDialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBaseBinding.inflate(inflater, container, false)
        return binding.root
    }



    //get user unique id from firebase
    fun getCurrentUserID(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

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
            replace(R.id.ll_container, fragment)
            setReorderingAllowed(true)
            commit()
        }
    }

    fun replaceToFragmentAndBackStack(fragment: Fragment){
        fragmentManager?.beginTransaction()?.apply {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.ll_container, fragment)
            setReorderingAllowed(true)
            //add to stack for backing to it
            addToBackStack(null)
            commit()
        }
    }

    fun startMainActivity(){
        val intent = Intent(activity, MainActivity::class.java)
        activity?.startActivity(intent)
        activity?.finish()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}