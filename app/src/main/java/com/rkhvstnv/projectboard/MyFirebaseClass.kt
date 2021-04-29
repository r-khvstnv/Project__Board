package com.rkhvstnv.projectboard




import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rkhvstnv.projectboard.models.BoardData
import com.rkhvstnv.projectboard.models.UserDataClass


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
                myCallBack.onCallbackSuccess("")
            }else{
                myCallBack.onCallbackErrorMessage(task.exception?.message!!)
            }
        }
    }
    fun signOutUser(){
        auth.signOut()
    }

    /**FireStore (User/Board/ Data)*/
    //register new user data in fireStore collections
    fun createUserData(userData: UserDataClass, myCallBack: MyCallBack){
        //create new collection in firebase
        mFireStore.collection(Constants.USERS).document(getCurrentUserId())
            .set(userData, SetOptions.merge()).addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        myCallBack.onCallbackSuccess("")
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
                myCallBack.onCallbackSuccess(loggedUser)
        }.addOnFailureListener {
            myCallBack.onCallbackErrorMessage(it.message!!)
        }
    }

    fun updateUserData(userHashMap: HashMap<String, Any>, myCallBack: MyCallBack){
        mFireStore.collection(Constants.USERS)
            .document(getCurrentUserId()).update(userHashMap).addOnCompleteListener {
                if (it.isSuccessful){
                    myCallBack.onCallbackSuccess("")
                } else{
                    myCallBack.onCallbackErrorMessage(it.exception?.message!!)
                }
            }
    }

    //register new board data in fireStore collections
    fun createNewBoard(boardData: BoardData, myCallBack: MyCallBack){
        //set id for document (document path)
        val documentPath: String = getCurrentUserId() + System.currentTimeMillis()
        //create new collection in firebase
        mFireStore.collection(Constants.BOARDS).document(documentPath)
            .set(boardData, SetOptions.merge()).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    //return path for saving in user data as beenAttachedToBoards
                    myCallBack.onCallbackSuccess(documentPath)
                }else{
                    myCallBack.onCallbackErrorMessage(task.exception?.message!!)
                }
            }
    }

    /**Firebase Storage (Images)*/
    fun uploadImage(uri: Uri, pathString: String, myCallBack: MyCallBack){
        val storageReference: StorageReference =
            FirebaseStorage.getInstance().reference.child(pathString)
        storageReference.putFile(uri).addOnSuccessListener { taskSnapshot ->
            //todo
            taskSnapshot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                myCallBack.onCallbackSuccess(it.toString())
            }
        }.addOnFailureListener { exception ->
            myCallBack.onCallbackErrorMessage(exception.message!!)
        }
    }

}
