package com.autocoin.exchangegateway.api.exchange.xchange

import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bibox
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.binance
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitbay
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitfinex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitmex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitso
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bitstamp
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bittrex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.bleutrade
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.cexio
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.coinbasepro
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.coindeal
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.coinex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.exmo
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.ftx
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.gateio
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.gemini
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.hitbtc
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.idex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.kraken
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.kucoin
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.luno
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.okex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.poloniex
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.tradeogre
import com.autocoin.exchangegateway.api.exchange.xchange.ExchangeNames.Companion.yobit
import com.autocoin.exchangegateway.api.exchange.xchange.fork.HitBtcExchangeFork
import com.autocoin.exchangegateway.api.exchange.xchange.fork.PoloniexExchangeFork
import com.autocoin.exchangegateway.api.exchange.xchange.fork.ZondaBitbayExchangFork
import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import org.knowm.xchange.Exchange
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.bibox.BiboxExchange
import org.knowm.xchange.binance.BinanceExchange
import org.knowm.xchange.bitfinex.BitfinexExchange
import org.knowm.xchange.bitmex.BitmexExchange
import org.knowm.xchange.bitso.BitsoExchange
import org.knowm.xchange.bitstamp.BitstampExchange
import org.knowm.xchange.bittrex.BittrexExchange
import org.knowm.xchange.bleutrade.BleutradeExchange
import org.knowm.xchange.cexio.CexIOExchange
import org.knowm.xchange.coinbasepro.CoinbaseProExchange
import org.knowm.xchange.coindeal.CoindealExchange
import org.knowm.xchange.coinex.CoinexExchange
import org.knowm.xchange.exmo.ExmoExchange
import org.knowm.xchange.ftx.FtxExchange
import org.knowm.xchange.gateio.GateioExchange
import org.knowm.xchange.gemini.v1.GeminiExchange
import org.knowm.xchange.idex.IdexExchange
import org.knowm.xchange.kraken.KrakenExchange
import org.knowm.xchange.kucoin.KucoinExchange
import org.knowm.xchange.luno.LunoExchange
import org.knowm.xchange.okex.OkexExchange
import org.knowm.xchange.tradeogre.TradeOgreExchange
import org.knowm.xchange.yobit.YoBitExchange
import java.util.function.Function
import org.knowm.xchange.Exchange as XchangeExchange

class DefaultXchangeProvider<T>(
    private val xchangeInstanceProvider: XchangeInstanceProvider,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
    private val exchangeNameToXchangeClass: Function<ExchangeName, Class<out Exchange>> = defaultExchangeNameToXchangeClass,
) : XchangeProvider<T> {
    companion object {
        val defaultExchangeNameToXchangeClassMap: Map<ExchangeName, Class<out Exchange>> = mapOf(
            bibox to BiboxExchange::class.java,
            binance to BinanceExchange::class.java,
            bitbay to ZondaBitbayExchangFork::class.java,
            bitfinex to BitfinexExchange::class.java,
            bitmex to BitmexExchange::class.java,
            bitso to BitsoExchange::class.java,
            bitstamp to BitstampExchange::class.java,
            bittrex to BittrexExchange::class.java,
            bleutrade to BleutradeExchange::class.java,
            cexio to CexIOExchange::class.java,
            coinbasepro to CoinbaseProExchange::class.java,
            coindeal to CoindealExchange::class.java,
            coinex to CoinexExchange::class.java,
            exmo to ExmoExchange::class.java,
            ftx to FtxExchange::class.java,
            gateio to GateioExchange::class.java,
            gemini to GeminiExchange::class.java,
            hitbtc to HitBtcExchangeFork::class.java,
            idex to IdexExchange::class.java,
            kraken to KrakenExchange::class.java,
            kucoin to KucoinExchange::class.java,
            luno to LunoExchange::class.java,
            okex to OkexExchange::class.java,
            poloniex to PoloniexExchangeFork::class.java,
            tradeogre to TradeOgreExchange::class.java,
            yobit to YoBitExchange::class.java,
        )
        val defaultExchangeNameToXchangeClass: Function<ExchangeName, Class<out Exchange>> = Function { exchangeName ->
            defaultExchangeNameToXchangeClassMap[exchangeName]
                ?: throw IllegalArgumentException("No Xchange class found for exchange name $exchangeName")
        }
    }

    override operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): XchangeExchange {
        val exchangeSpec = ExchangeSpecification(exchangeNameToXchangeClass.apply(exchangeName))
        if (apiKey.supplier != null) {
            xchangeSpecificationApiKeyAssigner.assignKeys(
                exchangeName = exchangeName,
                exchangeSpecification = exchangeSpec,
                apiKeySupplier = apiKey.supplier!!,
            )
        }
        // TODO provide a way to configure the exchange
        exchangeSpec.isShouldLoadRemoteMetaData = true
        val xchange = xchangeInstanceProvider(exchangeSpec)
        return xchange

    }
}
