package com.sky.SkyFleetDriver.model

data class LoginUser(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val device_id: String,
    val id: String,
    val otp: String
)