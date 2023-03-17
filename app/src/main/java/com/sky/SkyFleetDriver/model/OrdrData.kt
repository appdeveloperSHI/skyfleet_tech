package com.sky.SkyFleetDriver.model

data class OrdrData(
    val rider_status: String,
    val total_accept_order: String,
    val total_complete_order: String,
    val total_reject_order: String,
    val total_sale: String
)