package com.autocoin.exchangegateway.dto.order


data class CancelOrdersDto(
    val orders: List<CanceledOrderDto>,
)
