package com.rkhvstnv.projectboard



import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.rkhvstnv.projectboard.R
import com.rkhvstnv.projectboard.fragments.BaseFragment
import com.rkhvstnv.projectboard.fragments.PrimaryFragment
import com.rkhvstnv.projectboard.fragments.SignUpFragment


class FireStoreClass {
    private val mFireStore = Firebase.firestore

    //register new user in fireStore collections
    fun registerUser(fragment: BaseFragment, userModel: UserModel){
        //create new collection in firebase
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .set(userModel, SetOptions.merge()).addOnCompleteListener { task ->
                    //hide progress dialog
                    fragment.hideProgressDialog()
                    if (task.isSuccessful){
                        fragment.showSnackBarMessage(fragment.requireContext(),
                                "You have successfully registered")
                    }else{
                        fragment.showSnackBarMessage(fragment.requireContext(),
                                task.exception?.message.toString())
                    }
            }
    }

    //sign in user with received info from fireStore
    fun signInUser(fragment: BaseFragment){
        mFireStore.collection(Constants.USERS)
                .document(getCurrentUserId()).get().addOnSuccessListener { document ->
                    //hide progress dialog
                    fragment.hideProgressDialog()
                    //save data to object
                    val loggedUser = document.toObject<UserModel>()
                    //show main activity
                    fragment.startMainActivity()
                }
    }
    fun getCurrentUserId(): String{
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.uid ?: ""

    }
}