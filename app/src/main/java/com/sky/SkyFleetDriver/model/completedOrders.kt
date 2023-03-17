package com.sky.SkyFleetDriver.model

/** By vincent 06/08/2021 */

data class completedOrders(
    val ResponseCode: String,
    val ResponseMsg: String,
    val Result: String,
    val order_data: List<completedItem>
)