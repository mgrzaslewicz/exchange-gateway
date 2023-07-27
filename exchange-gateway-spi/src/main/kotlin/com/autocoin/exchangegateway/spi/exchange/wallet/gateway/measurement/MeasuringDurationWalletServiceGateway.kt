package com.autocoin.exchangegateway.spi.exchange.wallet.gateway.measurement

import com.autocoin.exchangegateway.spi.exchange.Exchange
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.gateway.WalletServiceGateway
import java.math.BigDecimal
import java.time.Duration
import kotlin.system.measureTimeMillis

interface OnGetCurrencyBalanceMeasured<T> {
    operator fun invoke(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        duration: Duration,
    )
}

interface OnGetCurrencyBalancesMeasured<T> {
    operator fun invoke(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        duration: Duration,
    )
}

interface OnWithdrawMeasured<T> {
    operator fun invoke(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
        duration: Duration,
    )
}

class MeasuringDurationWalletServiceGateway<T>(
    private val decorated: WalletServiceGateway<T>,
    private val onGetCurrencyBalanceMeasuredHandlers: List<OnGetCurrencyBalanceMeasured<T>> = emptyList(),
    private val onGetCurrencyBalancesMeasuredHandlers: List<OnGetCurrencyBalancesMeasured<T>> = emptyList(),
    private val onWithdrawMeasuredHandlers: List<OnWithdrawMeasured<T>> = emptyList(),
) : WalletServiceGateway<T> {

    override fun getCurrencyBalance(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        val result: CurrencyBalance
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getCurrencyBalance(
                    exchange = exchange,
                    apiKey = apiKey,
                    currencyCode = currencyCode,
                )
            },
        )
        onGetCurrencyBalanceMeasuredHandlers.forEach {
            it(
                exchange = exchange,
                apiKey = apiKey,
                currencyCode = currencyCode,
                duration = duration,
            )
        }
        return result
    }

    override fun getCurrencyBalances(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        val result: List<CurrencyBalance>
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getCurrencyBalances(
                    exchange = exchange,
                    apiKey = apiKey,
                )
            },
        )
        onGetCurrencyBalancesMeasuredHandlers.forEach {
            it(
                exchange = exchange,
                apiKey = apiKey,
                duration = duration,
            )
        }
        return result
    }

    override fun withdraw(
        exchange: Exchange,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult {
        val result: WithdrawResult
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.withdraw(
                    exchange = exchange,
                    apiKey = apiKey,
                    currencyCode = currencyCode,
                    amount = amount,
                    address = address,
                )
            },
        )
        onWithdrawMeasuredHandlers.forEach {
            it(
                exchange = exchange,
                apiKey = apiKey,
                currencyCode = currencyCode,
                amount = amount,
                address = address,
                duration = duration,
            )
        }
        return result
    }
}
