package com.autocoin.exchangegateway.api.exchange.orderbook.authorized

import com.autocoin.exchangegateway.api.exchange.currency.defaultCurrencyPairToXchange
import com.autocoin.exchangegateway.api.exchange.currency.defaultXchangeCurrencyPairTransformer
import com.autocoin.exchangegateway.api.exchange.order.defaultXchangeTypeToOrderSide
import com.autocoin.exchangegateway.api.exchange.orderbook.XchangeLimitOrderToOrderInOrderBookTransformer
import com.autocoin.exchangegateway.api.exchange.orderbook.XchangeOrderBookTransformer
import com.autocoin.exchangegateway.api.exchange.orderbook.defaultXchangeLimitOrderToOrderInOrderBookTransformer
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.OrderSide
import com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized.AuthorizedOrderBookService
import com.autocoin.exchangegateway.spi.exchange.orderbook.service.authorized.AuthorizedOrderBookServiceFactory
import org.knowm.xchange.dto.Order
import java.time.Clock
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair

class XchangeAuthorizedOrderBookServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val clock: Clock,
    private val xchangeCurrencyPairTransformer: Function<XchangeCurrencyPair, CurrencyPair> = defaultXchangeCurrencyPairTransformer,
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair> = defaultCurrencyPairToXchange,
    private val xchangeOrderBookTransformer: XchangeOrderBookTransformer = XchangeAuthorizedOrderBookService.defaultXchangeOrderBookTransformer,
    private val xchangeTypeToOrderSide: Function<Order.OrderType, OrderSide> = defaultXchangeTypeToOrderSide,
    private val xchangeLimitOrderToOrderInOrderBookTransformer: XchangeLimitOrderToOrderInOrderBookTransformer = defaultXchangeLimitOrderToOrderInOrderBookTransformer,
) : AuthorizedOrderBookServiceFactory<T> {
    override fun createAuthorizedOrderBookService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): AuthorizedOrderBookService<T> {
        val xchange = xchangeProvider(
            exchange = exchange,
            apiKey = apiKey,
        )
        return XchangeAuthorizedOrderBookService(
            exchange = exchange,
            apiKey = apiKey,
            delegate = xchange.marketDataService,
            clock = clock,
            xchangeCurrencyPairTransformer = xchangeCurrencyPairTransformer,
            currencyPairToXchange = currencyPairToXchange,
            xchangeOrderBookTransformer = xchangeOrderBookTransformer,
            xchangeTypeToOrderSide = xchangeTypeToOrderSide,
            xchangeLimitOrderToOrderInOrderBookTransformer = xchangeLimitOrderToOrderInOrderBookTransformer,
        )
    }
}
