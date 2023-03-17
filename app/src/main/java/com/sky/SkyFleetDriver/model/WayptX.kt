package com.sky.SkyFleetDriver.model

import android.os.Parcel
import android.os.Parcelable

data class WayptX(
    val address: String?,
    val completed:String?

) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.readString(),parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(address)
        parcel.writeString(completed)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WayptX> {
        override fun createFromParcel(parcel: Parcel): WayptX {
            return WayptX(parcel)
        }

        override fun newArray(size: Int): Array<WayptX?> {
            return arrayOfNulls(size)
        }
    }
}