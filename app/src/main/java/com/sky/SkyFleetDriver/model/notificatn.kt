package com.sky.SkyFleetDriver.model

data class notificatn(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val `data`: List<notificatnItem>
)