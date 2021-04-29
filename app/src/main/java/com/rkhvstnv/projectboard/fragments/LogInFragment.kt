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
            activity?.onBackPressed()
        }
        binding.llLogIn.setOnClickListener {
            requestSignInUser()
        }
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Next method check that need fields were filled
     *  If it's completed, will try to signIn user wit this data*/
    private fun requestSignInUser(){
        if (!binding.etEmail.text.isNullOrEmpty() && !binding.etPassword.text.isNullOrEmpty()){
            val email: String = binding.etEmail.text.toString()
            val password: String = binding.etPassword.text.toString()

            showProgressDialog(requireContext())
            /** Try to signIn user*/
            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){
                            hideProgressDialog()

                            val intent = Intent(activity, MainActivity::class.java)
                            activity?.startActivity(intent)
                            activity?.finish()

                        }else{ /** Inform that some problems were occurred */
                            hideProgressDialog()
                            showSnackBarMessage(requireContext(), task.exception?.message!!)
                        }
                    }
        } else{ /** Inform user that all needed data hasn't been entered*/
            showSnackBarMessage(requireContext(), getString(R.string.st_enter_data))
        }
    }


}