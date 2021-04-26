package com.rkhvstnv.projectboard




import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MyFirebaseClass {
    private val mFireStore = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()


    /**FirebaseAuth */
    fun getCurrentUserId(): String{
        val currentUser = auth.currentUser
        return currentUser?.uid ?: ""
    }
    fun updateUserPassword(newPassword: String, myCallBack: MyCallBack){
        auth.currentUser?.updatePassword(newPassword)?.addOnCompleteListener { task->
            if (task.isSuccessful){
                myCallBack.onCallbackErrorMessage("")
            }else{
                myCallBack.onCallbackErrorMessage(task.exception?.message!!)
            }
        }
    }
    fun signOutUser(){
        auth.signOut()
    }

    /**FireStore (User Data)*/
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

    fun updateUserData(userHashMap: HashMap<String, Any>, myCallBack: MyCallBack){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).update(userHashMap).addOnCompleteListener {
                if (it.isSuccessful){
                    myCallBack.onCallbackErrorMessage("")
                } else{
                    myCallBack.onCallbackErrorMessage(it.exception?.message!!)
                }
            }
    }

    /**Firebase Storage (Images)*/


}
