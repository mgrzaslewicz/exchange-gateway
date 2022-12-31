package automate.profit.autocoin.exchange.xchange

import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bibox
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.binance
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitbay
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitfinex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitmex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitso
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bitstamp
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bittrex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.bleutrade
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.cexio
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.coinbasepro
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.coindeal
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.coinex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.exmo
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.ftx
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.gateio
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.gemini
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.hitbtc
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.idex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.kraken
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.kucoin
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.luno
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.okex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.poloniex
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.tradeogre
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.yobit
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.apikey.ApiKey
import automate.profit.xchange.HitBtcExchangeFork
import automate.profit.xchange.PoloniexExchangeFork
import automate.profit.xchange.ZondaBitbayExchangFork
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
import java.util.function.Supplier
import org.knowm.xchange.Exchange as XchangeExchange

class DefaultXchangeProvider(
    private val xchangeInstanceProvider: XchangeInstanceProvider,
    private val xchangeSpecificationApiKeyAssigner: XchangeSpecificationApiKeyAssigner,
    private val exchangeNameToXchangeClass: Function<ExchangeName, Class<out Exchange>> = defaultExchangeNameToXchangeClass,
) : XchangeProvider {
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
        apiKey: Supplier<ApiKey>?,
    ): XchangeExchange {
        val exchangeSpec = ExchangeSpecification(exchangeNameToXchangeClass.apply(exchangeName))
        if (apiKey != null) {
            xchangeSpecificationApiKeyAssigner.assignKeys(
                exchangeName = exchangeName,
                exchangeSpecification = exchangeSpec,
                apiKeySupplier = apiKey,
            )
        }
        // TODO when it needs to be more effective, prevent from remote init each time and change provide json file
        // It will need ExchangeMetadataService modification
        exchangeSpec.isShouldLoadRemoteMetaData = true
        val xchange = xchangeInstanceProvider(exchangeSpec)
        return xchange

    }
}
