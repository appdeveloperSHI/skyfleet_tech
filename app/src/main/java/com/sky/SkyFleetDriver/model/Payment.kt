package com.sky.SkyFleetDriver.model

data class Payment(
    val base_fair: String,
    val current_job: String,
    val destination_location: String,
    val extra_distance_crg: String,
    val extra_distance_travelled: String,
    val name: String,
    val order_id: String,
    val origin_location: String,
    val total_distance: String,
    val total_fare: String,
    val total_time: String,
    val type_of_payment: String,
    val vehicle: String,
    val waiting_charge: String,
    val waiting_time: String,
    val minimum_fare: String,
    val date: String,
    val booking_id: String
)