package automate.profit.autocoin.exchange.currency


fun CurrencyPair.toXchangeCurrencyPair() = org.knowm.xchange.currency.CurrencyPair(base, counter)
