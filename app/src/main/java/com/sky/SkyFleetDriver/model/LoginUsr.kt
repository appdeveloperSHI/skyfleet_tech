package com.sky.SkyFleetDriver.model

/** By vincent 06/08/2021 */

data class LoginUsr(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val device_id: String,
    val id: String,
    val otp: String
)

