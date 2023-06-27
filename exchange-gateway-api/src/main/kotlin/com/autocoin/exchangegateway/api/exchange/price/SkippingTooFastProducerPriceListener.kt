package com.autocoin.exchangegateway.api.exchange.price

import com.autocoin.exchangegateway.api.skippingconsumer.SkippingTooFastProducer
import com.autocoin.exchangegateway.spi.exchange.price.PriceListener
import com.autocoin.exchangegateway.spi.skippingconsumer.SkippingConsumer
import mu.KLogging
import com.autocoin.exchangegateway.spi.exchange.price.CurrencyPairWithPrice as SpiCurrencyPairWithPrice

class SkippingTooFastProducerPriceListener(
    private val decorated: PriceListener,
    private val onPriceSkipped: (SpiCurrencyPairWithPrice) -> Unit = {},
    private val skippingConsumer: SkippingConsumer,
) : PriceListener {
    private companion object : KLogging()

    override fun onPriceUpdated(currencyPairWithPrice: SpiCurrencyPairWithPrice) {
        skippingConsumer.run(
            job = { decorated.onPriceUpdated(currencyPairWithPrice) },
            onSkipped = {
                onPriceSkipped(currencyPairWithPrice)
            },
        )
    }

}

fun PriceListener.skippingTooFastProducer(
    onPriceSkipped: (SpiCurrencyPairWithPrice) -> Unit = {},
    skippingConsumer: SkippingConsumer = SkippingTooFastProducer(),
): PriceListener {
    return SkippingTooFastProducerPriceListener(this, onPriceSkipped, skippingConsumer)
}
