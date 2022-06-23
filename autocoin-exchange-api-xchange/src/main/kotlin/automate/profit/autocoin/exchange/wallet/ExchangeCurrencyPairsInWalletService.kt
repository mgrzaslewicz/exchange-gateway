package automate.profit.autocoin.exchange.wallet

import automate.profit.autocoin.exchange.apikey.ExchangeKeyDto
import automate.profit.autocoin.exchange.currency.ExchangeCurrencyBalance
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.metadata.ExchangeMetadata
import automate.profit.autocoin.exchange.metadata.ExchangeMetadataService
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import mu.KLogging

interface ExchangeCurrencyPairsInWalletService {
    fun generateFromWalletIfGivenEmpty(exchangeName: String, exchangeUserId: String, currencyPairs: List<CurrencyPair>): List<CurrencyPair>
    fun generateFromWalletIfGivenEmpty(exchangeName: String, exchangeKey: ExchangeKeyDto, currencyPairs: List<CurrencyPair>): List<CurrencyPair>
}

/**
 * TODO Verify this mechanism is working on production, for now it does not matter as it's not used by most important supported exchanges
 */
class DefaultExchangeCurrencyPairsInWalletService(
    private val exchangeMetadataService: ExchangeMetadataService,
    private val exchangeWalletService: XchangeExchangeWalletService
) : ExchangeCurrencyPairsInWalletService {

    companion object : KLogging()

    override fun generateFromWalletIfGivenEmpty(exchangeName: String, exchangeKey: ExchangeKeyDto, currencyPairs: List<CurrencyPair>): List<CurrencyPair> {
        return if (currencyPairs.isNotEmpty()) currencyPairs
        else runBlocking {
            val exchangeMetadataCall = async { exchangeMetadataService.getMetadata(exchangeName) }
            val exchangeWalletCall = async { exchangeWalletService.getCurrencyBalances(exchangeName, exchangeKey) }

            val exchangeMetadata = exchangeMetadataCall.await()
            val currencyBalances = exchangeWalletCall.await()
            allPossibleCurrencyPairsFromBalances(exchangeMetadata, currencyBalances).also {
                logger.info { "Generated possible currency pairs: $it" }
            }
        }
    }

    override fun generateFromWalletIfGivenEmpty(exchangeName: String, exchangeUserId: String, currencyPairs: List<CurrencyPair>): List<CurrencyPair> {
        return if (currencyPairs.isNotEmpty()) currencyPairs
        else
            runBlocking {
                val exchangeMetadataCall = async { exchangeMetadataService.getMetadata(exchangeName) }
                val exchangeWalletCall = async { exchangeWalletService.getCurrencyBalances(exchangeName, exchangeUserId) }

                val exchangeMetadata = exchangeMetadataCall.await()
                val currencyBalances = exchangeWalletCall.await()
                allPossibleCurrencyPairsFromBalances(exchangeMetadata, currencyBalances).also {
                    logger.info { "Generated possible currency pairs: $it" }
                }
            }
    }

    private fun allPossibleCurrencyPairsFromBalances(exchangeMetadata: ExchangeMetadata, exchangeCurrencyBalances: List<ExchangeCurrencyBalance>): List<CurrencyPair> {
        val currencyCodesInWallet = exchangeCurrencyBalances.map { it.currencyCode }
        return exchangeMetadata.currencyPairMetadata.keys.filter { currencyPair ->
            currencyPair.counter in currencyCodesInWallet
        }
    }

}
