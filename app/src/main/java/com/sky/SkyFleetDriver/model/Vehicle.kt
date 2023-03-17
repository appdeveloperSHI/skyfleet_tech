package com.sky.SkyFleetDriver.model

data class Vehicle(
    val chargeOn: Boolean,
    val dttime: String,
    val ignition: Int,
    val latitude: Double,
    val location: String,
    val longitude: Double,
    val speed: Double,
    val vehicleName: String
)