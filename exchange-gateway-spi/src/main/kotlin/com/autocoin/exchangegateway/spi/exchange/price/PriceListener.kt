package com.autocoin.exchangegateway.spi.exchange.price

import com.autocoin.exchangegateway.spi.exchange.price.CurrencyPairWithPrice as SpiCurrencyPairWithPrice
interface PriceListener {
    fun onPriceUpdated(currencyPairWithPrice: SpiCurrencyPairWithPrice)
}

