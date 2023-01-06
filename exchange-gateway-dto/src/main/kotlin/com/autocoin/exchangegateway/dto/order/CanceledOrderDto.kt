package com.autocoin.exchangegateway.dto.order


data class CanceledOrderDto(
    val orderId: String,
    val success: Boolean,
)

