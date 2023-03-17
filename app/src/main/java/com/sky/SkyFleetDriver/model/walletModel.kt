package com.sky.SkyFleetDriver.model

data class walletModel(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val balance: String,
    val trans: List<Tran>
)