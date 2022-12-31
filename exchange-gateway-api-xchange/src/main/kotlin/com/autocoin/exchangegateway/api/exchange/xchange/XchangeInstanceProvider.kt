package com.autocoin.exchangegateway.api.exchange.xchange

import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification

interface XchangeInstanceProvider {
    operator fun invoke(exchangeSpecification: ExchangeSpecification): Exchange
}

/**
 * Wrap in class to make it testable as original xchange factory is an enum
 */
class XchangeInstanceWrapper(
    private val xchangeFactory: ExchangeFactory = ExchangeFactory.INSTANCE,
) : XchangeInstanceProvider {
    override operator fun invoke(exchangeSpecification: ExchangeSpecification): Exchange = xchangeFactory.createExchange(exchangeSpecification)
}
