package com.rkhvstnv.projectboard.fragments

import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.rkhvstnv.projectboard.R
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

            showProgressDialog(requireContext())

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        hideProgressDialog()

                        if (task.isSuccessful){
                            val user = auth.currentUser
                            //todo change
                            showSnackBarMessage(requireContext(), user!!.email!!)
                        }else{
                            showSnackBarMessage(requireContext(), task.exception?.message!!)
                        }

                    }
        }else{
            //todo delete
            auth.signOut()
            showSnackBarMessage(requireContext(), getString(R.string.st_enter_data))
        }
    }


}