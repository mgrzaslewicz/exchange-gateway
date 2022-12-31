package automate.profit.autocoin.exchange.order.service.authorized

import automate.profit.autocoin.exchange.currency.defaultCurrencyPairToXchange
import automate.profit.autocoin.exchange.order.XchangeLimitOrderToOrderTransformer
import automate.profit.autocoin.exchange.order.defaultXchangeLimitOrderToOrderTransformer
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.binance
import automate.profit.autocoin.exchange.xchange.XchangeProvider
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKeySupplier
import automate.profit.autocoin.spi.exchange.currency.CurrencyPair
import automate.profit.autocoin.spi.exchange.order.CancelOrderParams
import automate.profit.autocoin.spi.exchange.order.service.authorized.AuthorizedOrderServiceFactory
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
    private val currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair> = defaultCurrencyPairToXchange,
    private val cancelOrderParamsToXchangeParams: Function<CancelOrderParams, XchangeCancelOrderParams> = defaultCancelOrderParamsToXchangeParams(currencyPairToXchange),
    private val openOrdersCurrencyPairParamsToXchangeParams: Function<CurrencyPair, OpenOrdersParamCurrencyPair> = defaultOpenOrdersCurrencyPairParamsToXchangeParams(
        currencyPairToXchange,
    ),
    private val xchangeLimitOrderToOrderTransformer: XchangeLimitOrderToOrderTransformer = defaultXchangeLimitOrderToOrderTransformer,
) : AuthorizedOrderServiceFactory<T> {
    companion object {
        fun defaultCancelOrderParamsToXchangeParams(currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>): Function<CancelOrderParams, XchangeCancelOrderParams> =
            Function { params ->
                when (params.exchangeName) {
                    binance -> BinanceCancelOrderParams(currencyPairToXchange.apply(params.currencyPair), params.orderId)
                    else -> DefaultCancelOrderParamId(params.orderId)
                }
            }

        fun defaultOpenOrdersCurrencyPairParamsToXchangeParams(currencyPairToXchange: Function<CurrencyPair, XchangeCurrencyPair>): Function<CurrencyPair, OpenOrdersParamCurrencyPair> =
            Function { currencyPair ->
                DefaultOpenOrdersParamCurrencyPair(currencyPairToXchange.apply(currencyPair))
            }

    }

    override fun createAuthorizedOrderService(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): XchangeAuthorizedOrderService<T> {
        val xchange = xchangeProvider(
            exchangeName = exchangeName,
            apiKey = apiKey,
        )

        return XchangeAuthorizedOrderService(
            exchangeName = exchangeName,
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
