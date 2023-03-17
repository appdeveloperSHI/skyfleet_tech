package com.sky.SkyFleetDriver.model

/** By vincent 06/08/2021 */

data class pendingItem(
    val ResponseCode: String,
    val Result: String,
    val jobs: List<Job>,
    val total_request: Int,
    val vehicle: String
)