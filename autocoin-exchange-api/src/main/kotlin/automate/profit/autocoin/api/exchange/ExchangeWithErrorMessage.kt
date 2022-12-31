package automate.profit.autocoin.api.exchange

import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.ExchangeWithErrorMessage as SpiExchangeWithErrorMessage

data class ExchangeWithErrorMessage(
    override val exchangeName: ExchangeName,
    override val errorMessage: String?,
) : SpiExchangeWithErrorMessage

