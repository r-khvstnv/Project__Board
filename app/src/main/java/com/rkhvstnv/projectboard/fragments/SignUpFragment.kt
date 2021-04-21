package com.rkhvstnv.projectboard.fragments

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.rkhvstnv.projectboard.*
import com.rkhvstnv.projectboard.databinding.FragmentSignUpBinding

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
            onBackPressed()
        }
        binding.llSignUp.setOnClickListener {
            registerUser()
        }

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun registerUser(){
        //remove empty spaces
        val name: String = binding.etName.text.toString().trim { it <= ' ' }
        val email: String = binding.etEmail.text.toString().trim { it <= ' ' }
        val password: String = binding.etPassword.text.toString().trim { it <= ' ' }
        val confirmPassword: String = binding.etConfirmPassword.text.toString().trim { it <= ' ' }

        //register user if all data entered right
        if (validateUserData(name, email, password, confirmPassword)){
            //it will be hidden in corresponding methods
            showProgressDialog(requireContext())
            //create user
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        //if tas is success create new user object
                        if (task.isSuccessful){
                            val firebaseUser = auth.currentUser
                            //store user data in fireStore
                            val user = UserDataClass(firebaseUser!!.uid, name, firebaseUser.email!!)
                            FireStoreClass().registerUser(user, object : MyCallBack{
                                override fun onCallbackObject(userData: UserDataClass) {}

                                override fun onCallbackErrorMessage(message: String) {

                                    hideProgressDialog()

                                    if (message.isEmpty()){
                                        //successful registration
                                        showSnackBarMessage(requireContext(),
                                            getString(R.string.st_registered))
                                        //todo change dir
                                        //todo make sth with user
                                        onBackPressed()
                                    }
                                    else{
                                        showSnackBarMessage(requireContext(), message)
                                    }
                                }

                            })
                        }
                        else{
                            //display error message if tas was unsuccessful
                            hideProgressDialog()
                            showSnackBarMessage(requireContext(), task.exception?.message.toString())
                        }
                    }
        }
    }
    //validate inputted data
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