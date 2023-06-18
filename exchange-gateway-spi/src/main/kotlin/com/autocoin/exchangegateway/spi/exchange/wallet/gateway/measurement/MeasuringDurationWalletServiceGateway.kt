package com.autocoin.exchangegateway.spi.exchange.wallet.gateway.measurement

import com.autocoin.exchangegateway.spi.exchange.ExchangeName
import com.autocoin.exchangegateway.spi.exchange.apikey.ApiKeySupplier
import com.autocoin.exchangegateway.spi.exchange.currency.CurrencyBalance
import com.autocoin.exchangegateway.spi.exchange.wallet.WithdrawResult
import com.autocoin.exchangegateway.spi.exchange.wallet.gateway.WalletServiceGateway
import java.math.BigDecimal
import java.time.Duration
import kotlin.system.measureTimeMillis

interface OnGetCurrencyBalanceMeasured<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        duration: Duration,
    )
}

interface OnGetCurrencyBalancesMeasured<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        duration: Duration,
    )
}

interface OnWithdrawMeasured<T> {
    operator fun invoke(
        exchangeName: ExchangeName,
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
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
    ): CurrencyBalance {
        val result: CurrencyBalance
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getCurrencyBalance(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyCode = currencyCode,
                )
            },
        )
        onGetCurrencyBalanceMeasuredHandlers.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                currencyCode = currencyCode,
                duration = duration,
            )
        }
        return result
    }

    override fun getCurrencyBalances(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
    ): List<CurrencyBalance> {
        val result: List<CurrencyBalance>
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.getCurrencyBalances(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                )
            },
        )
        onGetCurrencyBalancesMeasuredHandlers.forEach {
            it(
                exchangeName = exchangeName,
                apiKey = apiKey,
                duration = duration,
            )
        }
        return result
    }

    override fun withdraw(
        exchangeName: ExchangeName,
        apiKey: ApiKeySupplier<T>,
        currencyCode: String,
        amount: BigDecimal,
        address: String,
    ): WithdrawResult {
        val result: WithdrawResult
        val duration = Duration.ofMillis(
            measureTimeMillis {
                result = decorated.withdraw(
                    exchangeName = exchangeName,
                    apiKey = apiKey,
                    currencyCode = currencyCode,
                    amount = amount,
                    address = address,
                )
            },
        )
        onWithdrawMeasuredHandlers.forEach {
            it(
                exchangeName = exchangeName,
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
