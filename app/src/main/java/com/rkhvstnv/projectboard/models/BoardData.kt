package com.rkhvstnv.projectboard.models

import android.os.Parcel
import android.os.Parcelable

data class BoardData (
    val createdBy: String = "",
    val name: String = "",
    val image: String = "",
    val assignedToUserIds: ArrayList<String> = ArrayList(),
    val startDate: Long = 0,
    val dueDate: Long = 0
        ) : Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readLong(),
        parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(createdBy)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeStringList(assignedToUserIds)
        parcel.writeLong(startDate)
        parcel.writeLong(dueDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardData> {
        override fun createFromParcel(parcel: Parcel): BoardData {
            return BoardData(parcel)
        }

        override fun newArray(size: Int): Array<BoardData?> {
            return arrayOfNulls(size)
        }
    }

}