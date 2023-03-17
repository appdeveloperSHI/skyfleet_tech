package com.sky.SkyFleetDriver.model

data class totalCompletedStatus(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val order_data: OrdrData
)