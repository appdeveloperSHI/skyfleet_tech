package com.sky.SkyFleetDriver.model

import android.os.Parcel
import android.os.Parcelable


data class Job(
    val customermobile: String?,
    val customername: String?,
    val destination_location: String?,
    val est_ex_hr_charge: String?,
    val est_ex_km_charge: String?,
    val est_trip_time: String?,
    val job_id: String?,
    val origin_location: String?,
    val paymenttype: String?,
    var status: String?,
    val total_distance: Int,
    val total_price: String?,
    val vid: String?,
    val schedule_time: String?,
    val schedule_date: String?,
    val waypts: List<WayptX>?,
    val booking_id: String?,
    val wayptcount:String?,
    val wayptcurrent:String?




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
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createTypedArrayList(WayptX),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(customermobile)
        parcel.writeString(customername)
        parcel.writeString(destination_location)
        parcel.writeString(est_ex_hr_charge)
        parcel.writeString(est_ex_km_charge)
        parcel.writeString(est_trip_time)
        parcel.writeString(job_id)
        parcel.writeString(origin_location)
        parcel.writeString(paymenttype)
        parcel.writeString(status)
        parcel.writeInt(total_distance)
        parcel.writeString(total_price)
        parcel.writeString(vid)
        parcel.writeString(schedule_time)
        parcel.writeString(schedule_date)
        parcel.writeTypedList(waypts)
        parcel.writeString(booking_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Job> {
        override fun createFromParcel(parcel: Parcel): Job {
            return Job(parcel)
        }

        override fun newArray(size: Int): Array<Job?> {
            return arrayOfNulls(size)
        }
    }
}