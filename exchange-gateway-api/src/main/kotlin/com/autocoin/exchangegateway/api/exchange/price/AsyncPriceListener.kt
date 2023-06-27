package com.autocoin.exchangegateway.api.exchange.price

import com.autocoin.exchangegateway.spi.exchange.price.PriceListener
import mu.KLogging
import java.util.concurrent.ExecutorService
import com.autocoin.exchangegateway.spi.exchange.price.CurrencyPairWithPrice as SpiCurrencyPairWithPrice

class AsyncPriceListener(
    private val executorService: ExecutorService,
    private val decorated: PriceListener,
) : PriceListener {
    private companion object : KLogging()

    override fun onPriceUpdated(currencyPairWithPrice: SpiCurrencyPairWithPrice) {
        executorService.submit {
            decorated.onPriceUpdated(currencyPairWithPrice)
        }
    }

}

fun PriceListener.async(executorService: ExecutorService): PriceListener {
    return AsyncPriceListener(executorService, this)
}

