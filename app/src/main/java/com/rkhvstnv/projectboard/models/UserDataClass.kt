package com.rkhvstnv.projectboard.models

import android.os.Parcel
import android.os.Parcelable

data class UserDataClass(
    val id: String = "", val name: String = "",
    val email: String = "", val imageProfile:  String = "",
    val phone: String = "", val fcmToken: String = "",
    /**Consist all boardIds to which is attached user,
     * regardless is he creator or member */
    val beenAttachedToBoards: ArrayList<String> = ArrayList()
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(imageProfile)
        parcel.writeString(phone)
        parcel.writeString(fcmToken)
        parcel.writeStringList(beenAttachedToBoards)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserDataClass> {
        override fun createFromParcel(parcel: Parcel): UserDataClass {
            return UserDataClass(parcel)
        }

        override fun newArray(size: Int): Array<UserDataClass?> {
            return arrayOfNulls(size)
        }
    }
}