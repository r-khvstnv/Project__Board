package com.rkhvstnv.projectboard.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.activities.MainActivity
import com.rkhvstnv.projectboard.databinding.FragmentLogInBinding

class LogInFragment : BaseFragment() {
    private var _binding: FragmentLogInBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLogInBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.llLogIn.setOnClickListener {
            signInUser()
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun signInUser(){
        if (!binding.etEmail.text.isNullOrEmpty() && !binding.etPassword.text.isNullOrEmpty()){
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()

            //dialog will be closed in appropriate method
            showProgressDialog(requireContext())

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){
                            FireStoreClass().getSignedInUserData(object : MyCallBack{
                                override fun onCallbackObject(userData: UserDataClass) {
                                    hideProgressDialog()
                                    val intent = Intent(activity, MainActivity::class.java)
                                    activity?.startActivity(intent)
                                    activity?.finish()
                                }

                                override fun onCallbackErrorMessage(message: String) {
                                    hideProgressDialog()
                                    showSnackBarMessage(requireContext(), message)
                                }
                            })
                        }else{
                            hideProgressDialog()
                            showSnackBarMessage(requireContext(), task.exception?.message!!)
                        }

                    }
        }else{
            showSnackBarMessage(requireContext(), getString(R.string.st_enter_data))
        }
    }


}