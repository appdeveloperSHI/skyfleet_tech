package com.sky.SkyFleetDriver.model

data class paymentModel(
    val ResponseCode: String,
    val Responsemsg: String,
    val Result: String,
    val payment: Payment
)