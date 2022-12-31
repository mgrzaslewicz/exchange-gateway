package com.autocoin.exchangegateway.api.exchange.xchange.fork

import org.knowm.xchange.currency.Currency
import org.knowm.xchange.currency.CurrencyPair
import org.knowm.xchange.dto.meta.CurrencyMetaData
import org.knowm.xchange.dto.meta.CurrencyPairMetaData
import org.knowm.xchange.dto.meta.FeeTier
import org.knowm.xchange.dto.meta.WalletHealth
import org.knowm.xchange.hitbtc.v2.HitbtcAdapters
import org.knowm.xchange.hitbtc.v2.HitbtcExchange
import org.knowm.xchange.hitbtc.v2.dto.HitbtcCurrency
import org.knowm.xchange.hitbtc.v2.dto.HitbtcSymbol
import org.knowm.xchange.hitbtc.v2.service.HitbtcMarketDataServiceRaw
import java.math.BigDecimal

class HitBtcExchangeFork : HitbtcExchange() {

    override fun remoteInit() {
        val dataService = marketDataService as HitbtcMarketDataServiceRaw
        val hitbtcSymbols = dataService.hitbtcSymbols
        val currencies = dataService.hitbtcCurrencies
            .associateBy({ hitBtcCurrency -> Currency(hitBtcCurrency.id) })
            { hitBtcCurrency: HitbtcCurrency ->
                val walletHealth = when {
                    hitBtcCurrency.delisted -> WalletHealth.OFFLINE
                    !hitBtcCurrency.payoutEnabled && !hitBtcCurrency.payinEnabled -> WalletHealth.OFFLINE
                    !hitBtcCurrency.payoutEnabled -> WalletHealth.WITHDRAWALS_DISABLED
                    !hitBtcCurrency.payinEnabled -> WalletHealth.DEPOSITS_DISABLED
                    else -> WalletHealth.ONLINE
                }
                CurrencyMetaData(null, hitBtcCurrency.payoutFee, null, walletHealth)
            }

        val currencyPairs = hitbtcSymbols.associateBy(
            { hitbtcSymbol: HitbtcSymbol ->
                CurrencyPair(
                    Currency(hitbtcSymbol.baseCurrency),
                    Currency(hitbtcSymbol.quoteCurrency),
                )
            },
        ) { hitbtcSymbol: HitbtcSymbol ->
            CurrencyPairMetaData(
                null as BigDecimal?,
                hitbtcSymbol.quantityIncrement,
                null as BigDecimal?,
                hitbtcSymbol.tickSize.scale(),
                null as Array<FeeTier?>?,
            )
        }
        exchangeMetaData = HitbtcAdapters.adaptToExchangeMetaData(hitbtcSymbols, currencies, currencyPairs)
    }
}
