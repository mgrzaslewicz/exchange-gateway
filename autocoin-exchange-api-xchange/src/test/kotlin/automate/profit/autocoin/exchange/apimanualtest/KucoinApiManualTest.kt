package automate.profit.autocoin.exchange.apimanualtest

import automate.profit.autocoin.exchange.SupportedExchange
import automate.profit.autocoin.exchange.XchangeSpecificationApiKeyAssigner
import automate.profit.autocoin.exchange.apikey.ExchangeApiKey
import automate.profit.autocoin.exchange.peruser.ExchangeSpecificationVerifier
import org.knowm.xchange.ExchangeFactory
import org.knowm.xchange.ExchangeSpecification
import org.knowm.xchange.kucoin.KucoinExchange
import java.lang.System.getenv

fun main() {
    val exchangeSpecification = ExchangeSpecification(KucoinExchange::class.java)
    val apiKey = ExchangeApiKey(
        publicKey = getenv("PUBLIC_KEY"),
        secretKey = getenv("SECRET_KEY"),
        exchangeSpecificKeyParameters = mapOf("passphrase" to getenv("PASSPHRASE"))
    )
    XchangeSpecificationApiKeyAssigner(ExchangeSpecificationVerifier()).assignKeys(SupportedExchange.KUCOIN, exchangeSpecification, apiKey)
    val kucoinExchange = ExchangeFactory.INSTANCE.createExchange(exchangeSpecification) as KucoinExchange
    val accounts = kucoinExchange.accountService.kucoinAccounts
//    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT") // expected to be OK
    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT") // expected to be OK
//    val tradeFees = kucoinExchange.marketDataService.getKucoinTradeFee("1EARTH-USDT,1INCH-USDT") // surprise: 401 for more than 2 symbols
    System.out.println("done")
}