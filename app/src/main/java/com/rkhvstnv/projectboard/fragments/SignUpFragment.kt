package com.rkhvstnv.projectboard.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.FragmentSignUpBinding
import com.rkhvstnv.projectboard.models.UserDataClass

class SignUpFragment : BaseFragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        binding.ivBack.setOnClickListener {
            activity?.onBackPressed()
        }
        binding.llSignUp.setOnClickListener {
            requestUserRegistration()
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun requestUserRegistration(){
        val name: String = binding.etName.text.toString()
        /** Save user time and automatically remove empty spaces, if they were entered*/
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
        val confirmPassword: String = binding.etConfirmPassword.text.toString().trim { it <= ' ' }

        /** Register user if all data entered right*/
        if (validateUserData(name, email, password, confirmPassword)){

            showProgressDialog(requireContext())
            /** Create user auth parameters in fireBase*/
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        /** On success, create user document in FireStore Users collection*/
                        if (task.isSuccessful){
                            val firebaseUser = auth.currentUser
                            //store user data in fireStore
                            val user = UserDataClass(firebaseUser!!.uid, name, firebaseUser.email!!)
                            FirebaseClass().createUserData(user, object : MyCallBack{
                                override fun onCallbackSuccess(any: Any) {
                                    showSnackBarMessage(requireContext(),
                                        getString(R.string.st_registered))
                                    /** Get back to IntroFragment*/
                                    activity?.onBackPressed()
                                }

                                override fun onCallbackErrorMessage(message: String) {
                                    hideProgressDialog()
                                    showSnackBarMessage(requireContext(), message)
                                }

                            })
                        }
                        else{
                            /** Display error message,
                             *  if system couldn't create new auth parameters*/
                            hideProgressDialog()
                            showSnackBarMessage(requireContext(), task.exception?.message.toString())
                        }
                    }
        }
    }

    /** Next method validate entered data and show corresponding message
     *  if sth has been inputted wrong*/
    private fun validateUserData(
        name: String, email: String, password: String, confirmPassword: String) : Boolean{

        return when{
            TextUtils.isEmpty(name) ->{
                showSnackBarMessage(requireContext(),
                    getString(R.string.st_please_enter) + ' ' + getString(R.string.st_name))
                false
            }
            TextUtils.isEmpty(email) ->{
                showSnackBarMessage(requireContext(),
                    getString(R.string.st_please_enter) + ' ' + getString(R.string.st_email))
                false
            }
            TextUtils.isEmpty(password) ->{
                showSnackBarMessage(requireContext(),
                    getString(R.string.st_please_enter) + ' ' + getString(R.string.st_password))
                false
            }
            TextUtils.isEmpty(name) ->{
                showSnackBarMessage(requireContext(),
                    getString(R.string.st_please_enter) + ' ' + getString(R.string.st_confirm_password))
                false
            }
            password != confirmPassword -> {
                showSnackBarMessage(requireContext(),
                    getString(R.string.st_password_mismatch))
                false
            }
            else -> true
        }
    }
}