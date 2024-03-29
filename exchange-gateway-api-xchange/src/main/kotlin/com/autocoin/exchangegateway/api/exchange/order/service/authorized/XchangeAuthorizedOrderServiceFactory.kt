package com.autocoin.exchangegateway.api.exchange.order.service.authorized

import com.autocoin.exchangegateway.api.exchange.order.XchangeLimitOrderToOrderTransformer
import com.autocoin.exchangegateway.api.exchange.order.defaultXchangeLimitOrderToOrderTransformer
import com.autocoin.exchangegateway.api.exchange.xchange.SupportedXchangeExchange
import com.autocoin.exchangegateway.api.exchange.xchange.XchangeProvider
import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyPair
import com.autocoin.exchangegateway.spi.exchange.order.CancelOrderParams
import com.autocoin.exchangegateway.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
import org.knowm.xchange.binance.service.BinanceCancelOrderParams
import org.knowm.xchange.service.trade.params.DefaultCancelOrderParamId
import org.knowm.xchange.service.trade.params.orders.DefaultOpenOrdersParamCurrencyPair
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParamCurrencyPair
import java.time.Clock
import java.util.function.Function
import org.knowm.xchange.currency.CurrencyPair as XchangeCurrencyPair
import org.knowm.xchange.service.trade.params.CancelOrderParams as XchangeCancelOrderParams


class XchangeAuthorizedOrderServiceFactory<T>(
    private val xchangeProvider: XchangeProvider<T>,
    private val clock: Clock,
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair> = com.autocoin.exchangegateway.api.exchange.currency.defaultCurrencyPairToXchange,
    private val cancelOrderParamsToXchangeParams: Function<CancelOrderParams, XchangeCancelOrderParams> = defaultCancelOrderParamsToXchangeParams(
        currencyPairToXchange,
    ),
    private val openOrdersCurrencyPairParamsToXchangeParams: Function<CurrencyPair, OpenOrdersParamCurrencyPair> = defaultOpenOrdersCurrencyPairParamsToXchangeParams(
        currencyPairToXchange,
    ),
    private val xchangeLimitOrderToOrderTransformer: XchangeLimitOrderToOrderTransformer = defaultXchangeLimitOrderToOrderTransformer,
) : AuthorizedOrderServiceFactory<T> {
    companion object {
        fun defaultCancelOrderParamsToXchangeParams(currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>): Function<CancelOrderParams, XchangeCancelOrderParams> =
            Function { params ->
                when (params.exchange) {
                    SupportedXchangeExchange.binance -> BinanceCancelOrderParams(
                        currencyPairToXchange.apply(
                            params.currencyPair,
                        ),
                        params.orderId,
                    )

                    else -> DefaultCancelOrderParamId(params.orderId)
                }
            }

        fun defaultOpenOrdersCurrencyPairParamsToXchangeParams(currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>): Function<CurrencyPair, OpenOrdersParamCurrencyPair> =
            Function { currencyPair ->
                DefaultOpenOrdersParamCurrencyPair(currencyPairToXchange.apply(currencyPair))
            }

    }

    override fun createAuthorizedOrderService(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): XchangeAuthorizedOrderService<T> {
        val xchange = xchangeProvider(
            exchange = exchange,
            apiKey = apiKey,
        )

        return XchangeAuthorizedOrderService(
            exchange = exchange,
            apiKey = apiKey,
            delegate = xchange.tradeService,
            cancelOrderParamsToXchangeParams = cancelOrderParamsToXchangeParams,
            openOrdersCurrencyPairParamsToXchangeParams = openOrdersCurrencyPairParamsToXchangeParams,
            currencyPairToXchange = currencyPairToXchange,
            xchangeLimitOrderToOrderTransformer = xchangeLimitOrderToOrderTransformer,
            clock = clock,
        )
    }
}
