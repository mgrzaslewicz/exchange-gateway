package automate.profit.autocoin.ticker.converter

import automate.profit.autocoin.exchange.currency.CurrencyPair
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class CurrencyPairAttributeConverter : AttributeConverter<CurrencyPair, String> {

    override fun convertToDatabaseColumn(currencyPair: CurrencyPair) = currencyPair.toString()

    override fun convertToEntityAttribute(currencyPair: String) = CurrencyPair.of(currencyPair)
}
