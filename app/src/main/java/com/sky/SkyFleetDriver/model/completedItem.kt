package com.sky.SkyFleetDriver.model

import android.os.Parcel
import android.os.Parcelable

//@Parcelize
data class completedItem(
    val booking_id: String?,
    val destination_location: String?,
    val email: String?,
    val jid: String?,
    val mobile: String?,
    val name: String?,
    val origin_location: String?,
    val paymenttype: String?,
    val pickup_date: String?,
    val pickup_time: String?,
    val sign: String?,
    val status: String?,
    val total: String?,
    val viapoint: String?,
    val exact_trip_time:String?,
    val exact_trip_distance: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(booking_id)
        parcel.writeString(destination_location)
        parcel.writeString(email)
        parcel.writeString(jid)
        parcel.writeString(mobile)
        parcel.writeString(name)
        parcel.writeString(origin_location)
        parcel.writeString(paymenttype)
        parcel.writeString(pickup_date)
        parcel.writeString(pickup_time)
        parcel.writeString(sign)
        parcel.writeString(status)
        parcel.writeString(total)
        parcel.writeString(viapoint)
        parcel.writeString(exact_trip_distance)
        parcel.writeString(exact_trip_time)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<completedItem> {
        override fun createFromParcel(parcel: Parcel): completedItem {
            return completedItem(parcel)
        }

        override fun newArray(size: Int): Array<completedItem?> {
            return arrayOfNulls(size)
        }
    }
}