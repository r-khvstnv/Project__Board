package com.rkhvstnv.projectboard

import android.os.Parcel
import android.os.Parcelable

data class UserModel(
    val id: String = "", val name: String = "",
    val email: String = "", val imageProfile:  String = "",
    val mobilePhone: String = "", val fcmToken: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(email)
        parcel.writeString(imageProfile)
        parcel.writeString(mobilePhone)
        parcel.writeString(fcmToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserModel> {
        override fun createFromParcel(parcel: Parcel): UserModel {
            return UserModel(parcel)
        }

        override fun newArray(size: Int): Array<UserModel?> {
            return arrayOfNulls(size)
        }
    }
}