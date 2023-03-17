package com.sky.SkyFleetDriver.model

data class homescreenModel(
    val ResponseCode: String,
    val Result: String,
    val driver_status: String,
    val jobs: List<Job>,
    val location: String,
    val name: String,
    val total_request: Int,
    val vehicle: String
)