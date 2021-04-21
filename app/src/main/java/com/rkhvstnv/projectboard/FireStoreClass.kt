package com.rkhvstnv.projectboard




import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase


class FireStoreClass {
    private val mFireStore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    //register new user data in fireStore collections
    fun registerUser(userData: UserDataClass, myCallBack: MyCallBack){
        //create new collection in firebase
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .set(userData, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        myCallBack.onCallbackErrorMessage("")
                    }else{
                        myCallBack.onCallbackErrorMessage(task.exception?.message!!)
                    }
            }
    }

    //sign in user with received info from fireStore
    fun getSignedInUserData(myCallBack: MyCallBack){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).get().addOnSuccessListener { document ->
                //save data to object
                val loggedUser = document.toObject<UserDataClass>()!!
                myCallBack.onCallbackObject(loggedUser)
        }.addOnFailureListener {
            myCallBack.onCallbackErrorMessage(it.message!!)
        }


    }

    fun signOutUser(){
        auth.signOut()
    }
    fun getCurrentUserId(): String{
        val currentUser = auth.currentUser
        return currentUser?.uid ?: ""

    }
}