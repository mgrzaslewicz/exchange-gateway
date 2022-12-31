package automate.profit.xchange

import org.knowm.xchange.poloniex.PoloniexAdapters
import org.knowm.xchange.poloniex.PoloniexExchange
import org.knowm.xchange.poloniex.PoloniexUtils
import org.knowm.xchange.poloniex.dto.marketdata.PoloniexMarketData
import org.knowm.xchange.poloniex.service.PoloniexMarketDataServiceRaw

class PoloniexExchangeFork : PoloniexExchange() {
    /**
     * Copy of original + cleanup
     * original logic loads poloniex.json metadata and uses it to fill metadata.
     * It contains currency pairs no longer existing at exchange, so remove them
     */
    override fun remoteInit() {

        val poloniexMarketDataServiceRaw = marketDataService as PoloniexMarketDataServiceRaw

        val poloniexCurrencyInfoMap = poloniexMarketDataServiceRaw.poloniexCurrencyInfo
        val poloniexMarketDataMap = poloniexMarketDataServiceRaw.allPoloniexTickers

        exchangeMetaData = PoloniexAdapters.adaptToExchangeMetaData(
            poloniexCurrencyInfoMap, poloniexMarketDataMap, exchangeMetaData,
        )
        removeNoLongerExistingCurrencyPairs(poloniexMarketDataMap)
    }

    private fun removeNoLongerExistingCurrencyPairs(poloniexMarketDataMap: Map<String, PoloniexMarketData>) {
        val poloniexCurrencyPairs = poloniexMarketDataMap.map { PoloniexUtils.toCurrencyPair(it.key) }
        val currencyPairsNotExistingAtExchange = exchangeMetaData.currencyPairs
            .filter { !poloniexCurrencyPairs.contains(it.key) }
            .map { it.key }
        currencyPairsNotExistingAtExchange.forEach {
            exchangeMetaData.currencyPairs.remove(it)
        }
    }
}
