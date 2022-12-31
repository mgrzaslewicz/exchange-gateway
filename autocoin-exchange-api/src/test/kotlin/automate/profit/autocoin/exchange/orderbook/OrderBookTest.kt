package automate.profit.autocoin.exchange.orderbook

import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.orderbook.OrderBook
import automate.profit.autocoin.api.exchange.orderbook.OrderInOrderBook
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderSide.BID_BUY
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ArgumentConversionException
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.converter.DefaultArgumentConverter
import org.junit.jupiter.params.converter.SimpleArgumentConverter
import org.junit.jupiter.params.provider.CsvSource
import java.math.BigDecimal


class OrderBookTest {
    private val objectMapper = ObjectMapper().registerModule(KotlinModule())
    private val currencyPair = CurrencyPair.of("currencyX/currencyY")
    private val timestampDoesNotMatter = System.currentTimeMillis()


    private val trxBtcCurrencyPair = CurrencyPair.of("TRX/BTC")

    private val xtzBtcCurrencyPair = CurrencyPair.of("XTZ/BTC")

    /**
     * real order book snapshot taken from exchange
     */
    private val realOrderBook1 = OrderBook(
        receivedAtMillis = timestampDoesNotMatter,
        exchangeTimestampMillis = null,
        exchangeName = ExchangeName("realExchange1"),
        currencyPair = trxBtcCurrencyPair,
        sellOrders = emptyList(),
        buyOrders = listOf(
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("319351.7968"),
                price = BigDecimal("0.00000196"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("280513.5493"),
                price = BigDecimal("0.00000195"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("705084.6506"),
                price = BigDecimal("0.00000194"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("42232.9603"),
                price = BigDecimal("0.00000193"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("117945.6236"),
                price = BigDecimal("0.00000192"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3765.7738"),
                price = BigDecimal("0.00000191"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("28618.863"),
                price = BigDecimal("0.0000019"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10387.9906"),
                price = BigDecimal("0.00000189"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("627.7139"),
                price = BigDecimal("0.00000188"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("16050.7139"),
                price = BigDecimal("0.00000187"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10400.2043"),
                price = BigDecimal("0.00000186"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("4365.5091"),
                price = BigDecimal("0.00000185"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("5690.3322"),
                price = BigDecimal("0.00000184"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1665.0538"),
                price = BigDecimal("0.00000183"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("597.4002"),
                price = BigDecimal("0.00000182"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3277.15"),
                price = BigDecimal("0.00000181"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("24428.9424"),
                price = BigDecimal("0.0000018"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1219.7978"),
                price = BigDecimal("0.00000179"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1811.329"),
                price = BigDecimal("0.00000178"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("70812.9665"),
                price = BigDecimal("0.00000177"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("75.6863"),
                price = BigDecimal("0.00000176"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1792.0558"),
                price = BigDecimal("0.00000175"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("11984.6863"),
                price = BigDecimal("0.00000174"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("752.6664"),
                price = BigDecimal("0.00000173"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("107.6506"),
                price = BigDecimal("0.00000172"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("4055.8807"),
                price = BigDecimal("0.00000171"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("154.9801"),
                price = BigDecimal("0.0000017"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("475.6874"),
                price = BigDecimal("0.00000169"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000168"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("544.0586"),
                price = BigDecimal("0.00000167"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000166"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000165"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("117.003"),
                price = BigDecimal("0.00000164"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000163"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("56.1179"),
                price = BigDecimal("0.00000162"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("505.6874"),
                price = BigDecimal("0.00000161"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("35.6874"),
                price = BigDecimal("0.0000016"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3170.3414"),
                price = BigDecimal("0.00000159"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("55.6874"),
                price = BigDecimal("0.00000158"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2803.6874"),
                price = BigDecimal("0.00000157"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("543.3797"),
                price = BigDecimal("0.00000156"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000155"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("174.0874"),
                price = BigDecimal("0.00000154"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000153"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000152"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("525.6874"),
                price = BigDecimal("0.00000151"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3724.1542"),
                price = BigDecimal("0.0000015"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("25.6874"),
                price = BigDecimal("0.00000149"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("7.2604"),
                price = BigDecimal("0.00000148"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1367.8046"),
                price = BigDecimal("0.00000147"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("857.2604"),
                price = BigDecimal("0.00000146"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("7000.0"),
                price = BigDecimal("0.00000145"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("14.427"),
                price = BigDecimal("0.00000144"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1139.837"),
                price = BigDecimal("0.00000143"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1000.0"),
                price = BigDecimal("0.00000142"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10.0"),
                price = BigDecimal("0.0000014"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("9163.1704"),
                price = BigDecimal("0.00000138"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1992.3311"),
                price = BigDecimal("0.00000136"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2324.4232"),
                price = BigDecimal("0.00000135"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1001.2"),
                price = BigDecimal("0.00000134"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10.1484"),
                price = BigDecimal("0.00000132"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("32.1"),
                price = BigDecimal("0.0000013"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("111.0"),
                price = BigDecimal("0.00000127"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("6.9484"),
                price = BigDecimal("0.00000126"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("835.0689"),
                price = BigDecimal("0.00000125"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("969.4977"),
                price = BigDecimal("0.00000122"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1505.4708"),
                price = BigDecimal("0.0000012"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("630.0"),
                price = BigDecimal("0.00000118"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2863.9086"),
                price = BigDecimal("0.00000114"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3217.9679"),
                price = BigDecimal("0.00000111"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("373.787"),
                price = BigDecimal("0.00000108"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("200000.0"),
                price = BigDecimal("0.00000106"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("200.0"),
                price = BigDecimal("0.00000105"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2.7132"),
                price = BigDecimal("0.00000102"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("38849.67"),
                price = BigDecimal("0.000001"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("3000.0"),
                price = BigDecimal("0.00000099"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2.1391"),
                price = BigDecimal("0.00000097"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("200.0"),
                price = BigDecimal("0.00000092"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1.7087"),
                price = BigDecimal("0.00000091"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1000.0"),
                price = BigDecimal("0.0000009"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("54.3175"),
                price = BigDecimal("0.00000085"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("200.0"),
                price = BigDecimal("0.00000083"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1.1075"),
                price = BigDecimal("0.00000079"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("178.0"),
                price = BigDecimal("0.00000078"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1.7945"),
                price = BigDecimal("0.00000073"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1.9532"),
                price = BigDecimal("0.00000067"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("5000.0"),
                price = BigDecimal("0.0000006"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("52378.5293"),
                price = BigDecimal("0.00000051"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("127654.968"),
                price = BigDecimal("0.0000005"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10500.0"),
                price = BigDecimal("0.0000004"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("10000.0"),
                price = BigDecimal("0.00000033"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("202.0703"),
                price = BigDecimal("0.00000032"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("12000.0"),
                price = BigDecimal("0.00000027"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("4417.5652"),
                price = BigDecimal("0.00000023"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("15000.0"),
                price = BigDecimal("0.00000022"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("51400.0"),
                price = BigDecimal("0.0000002"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("11362.8258"),
                price = BigDecimal("0.00000019"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("18710.2342"),
                price = BigDecimal("0.00000018"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("6628.5797"),
                price = BigDecimal("0.00000015"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange1"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1806.5681"),
                price = BigDecimal("0.00000011"),
                currencyPair = trxBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            )
        )
    )

    /**
     * real order book snapshot taken from exchange
     */
    private val realOrderBook2 = OrderBook(
        receivedAtMillis = timestampDoesNotMatter,
        exchangeTimestampMillis = null,
        exchangeName = ExchangeName("realExchange1"),
        currencyPair = trxBtcCurrencyPair,
        sellOrders = emptyList(),
        buyOrders = listOf(
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("155.742"),
                price = BigDecimal("0.00023049"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("302.10567656"),
                price = BigDecimal("0.00023048"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1000.0"),
                price = BigDecimal("0.00023039"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("267.64"),
                price = BigDecimal("0.00023035"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("632.0"),
                price = BigDecimal("0.00023015"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("119.9109"),
                price = BigDecimal("0.00023004"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("335.0146"),
                price = BigDecimal("0.00022894"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("6117.0"),
                price = BigDecimal("0.00022893"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("477.0"),
                price = BigDecimal("0.00022891"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1228.0723365"),
                price = BigDecimal("0.00022839"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2478.0"),
                price = BigDecimal("0.00022725"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("128.515"),
                price = BigDecimal("0.000225"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1290.0"),
                price = BigDecimal("0.00022499"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("1245.0"),
                price = BigDecimal("0.00022442"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("66.0"),
                price = BigDecimal("0.00022272"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("250.0"),
                price = BigDecimal("0.00022056"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("93.139"),
                price = BigDecimal("0.0002201"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("4858.0"),
                price = BigDecimal("0.00022"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("250.0"),
                price = BigDecimal("0.00021056"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("83.246"),
                price = BigDecimal("0.00020241"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("15.0"),
                price = BigDecimal("0.00020142"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("81.0"),
                price = BigDecimal("0.00020042"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("61.999"),
                price = BigDecimal("0.0002"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("130.208"),
                price = BigDecimal("0.000192"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("500.0"),
                price = BigDecimal("0.00019156"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("199.346"),
                price = BigDecimal("0.00018985"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2494.065"),
                price = BigDecimal("0.00018924"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("901.587"),
                price = BigDecimal("0.000189"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("2.871"),
                price = BigDecimal("0.0001851"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            ),
            OrderInOrderBook(
                exchangeName = ExchangeName("realExchange2"),
                side = BID_BUY,
                orderedAmount = BigDecimal("40233.691"),
                price = BigDecimal("0.000185"),
                currencyPair = xtzBtcCurrencyPair,
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            )
        )
    )
    private val sampleBuyOrders = listOf(
        OrderInOrderBook(
            exchangeName = ExchangeName("exchangeA"),
            side = BID_BUY,
            orderedAmount = 10.toBigDecimal(),
            price = 1.5.toBigDecimal(),
            currencyPair = currencyPair,
            receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
        ),
        OrderInOrderBook(
            exchangeName = ExchangeName("exchangeA"),
            side = BID_BUY,
            orderedAmount = 15.toBigDecimal(),
            price = 1.45.toBigDecimal(),
            currencyPair = currencyPair,
            receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
        ),
        OrderInOrderBook(
            exchangeName = ExchangeName("exchangeA"),
            side = BID_BUY,
            orderedAmount = 30.toBigDecimal(),
            price = 1.40.toBigDecimal(),
            currencyPair = currencyPair,
            receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
        ),
        OrderInOrderBook(
            exchangeName = ExchangeName("exchangeA"),
            side = BID_BUY,
            orderedAmount = 20.toBigDecimal(),
            price = 1.37.toBigDecimal(),
            currencyPair = currencyPair,
            receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
        )
    )

    private class NullableConverter : SimpleArgumentConverter() {
        @Throws(ArgumentConversionException::class)
        override fun convert(source: Any, targetType: Class<*>?): Any? {
            return if ("null" == source) {
                null
            } else DefaultArgumentConverter.INSTANCE.convert(source, targetType)
        }
    }

    @ParameterizedTest(name = "Weighted average of currencyX price should be {0} for amount {1}")
    @CsvSource(
        *[
            "1.50000000, 9.0",
            "1.50000000, 10.0",
            "1.48333333, 15.0", // 'avg = (10.0 * 1.5 + 5 * 1.45) / 15.0 = 1.48(3)'
            "1.41533333, 75", // whole order book
            "null, 76" // more than in order book
        ]
    )
    fun shouldCalculateWeightedAverageBuyPrice(
        @ConvertWith(NullableConverter::class) expectedWeightedAveragePrice: BigDecimal?,
        amount: BigDecimal
    ) {
        val orderBook = OrderBook(
            exchangeName = sampleBuyOrders.first().exchangeName,
            currencyPair = sampleBuyOrders.first().currencyPair,
            buyOrders = sampleBuyOrders, sellOrders = emptyList(),
            receivedAtMillis = timestampDoesNotMatter,
            exchangeTimestampMillis = null,
        )
        val avgPrice = orderBook.getWeightedAverageBuyPrice(amount)
        assertThat(avgPrice?.averagePrice).isEqualTo(expectedWeightedAveragePrice)
        if (avgPrice != null) {
            assertThat(avgPrice.baseCurrencyAmount).isEqualTo(amount.setScale(8))
        }
    }

    @ParameterizedTest(name = "Weighted average of currencyX price should be {0} for USD price {1} and amount {2}")
    @CsvSource(
        *[
            "1.50000000, 6.00000000, 10.0, 90.0", // 90.0 / 10.0 = 9 units of currencyY, value of first order is 15.0 currencyY
            "1.50000000, 10.00000000, 8.0, 120.0", // 15 units of currencyY
            "1.48333333, 15.00000000, 10.0, 222.5", // 22.25 units
            "1.41533333, 75.00000000, 10.0, 1061.5", // 106.15 units, whole order book
            "null, null, 10.0, 1061.6" // more than in order book
        ]
    )
    fun shouldCalculateWeightedAverageBuyPriceInUsd(
        @ConvertWith(NullableConverter::class) expectedAveragePrice: BigDecimal?,
        @ConvertWith(NullableConverter::class) expectedBaseCurrencyAmount: BigDecimal?,
        usdPrice: BigDecimal,
        usdAmount: BigDecimal
    ) {
        val orderBook = OrderBook(
            exchangeName = sampleBuyOrders.first().exchangeName,
            currencyPair = sampleBuyOrders.first().currencyPair,
            buyOrders = sampleBuyOrders, sellOrders = emptyList(),
            receivedAtMillis = timestampDoesNotMatter,
            exchangeTimestampMillis = null,
        )
        val avgPrice = orderBook.getWeightedAverageBuyPrice(usdAmount, usdPrice)
        assertThat(avgPrice?.averagePrice).isEqualTo(expectedAveragePrice)
        assertThat(avgPrice?.baseCurrencyAmount).isEqualTo(expectedBaseCurrencyAmount)
    }

    @Test
    fun testRealOrderBook2AverageBuyPrice() {
        val avgPriceHavingUsd = realOrderBook2.getWeightedAverageBuyPrice(BigDecimal(200.0), BigDecimal(7150.31))
        assertThat(avgPriceHavingUsd?.averagePrice).isEqualTo(BigDecimal("0.00023049"))
        assertThat(avgPriceHavingUsd?.baseCurrencyAmount).isEqualTo(BigDecimal("121.35370407"))
    }

    @Test
    fun testRealOrderBook1AverageBuyPrice() {
        val avgPriceHavingUsd = realOrderBook1.getWeightedAverageBuyPrice(BigDecimal(1000.0), BigDecimal(7150.31))
        assertThat(avgPriceHavingUsd?.averagePrice).isEqualTo(BigDecimal("0.00000196"))
        assertThat(avgPriceHavingUsd?.baseCurrencyAmount).isEqualTo(BigDecimal("71354.12053920"))
    }

    @Test
    fun testRealExchangeFromJsonOntUsdtOrderBookAverageBuyPrice() {
        val orderBookString = this.javaClass.getResource("/real-exchange-ont-usdt-orderbook.json").readText()
        val json = objectMapper.readTree(orderBookString)
        val buyOrdersJsonArray = json.get("buyOrders") as ArrayNode
        val buyOrders = buyOrdersJsonArray.map {
            OrderInOrderBook(
                exchangeName = ExchangeName(it.get("exchangeName").textValue()),
                side = OrderSide.valueOf(it.get("side").textValue()),
                orderedAmount = BigDecimal(it.get("orderedAmount").doubleValue()),
                price = BigDecimal(it.get("price").textValue()),
                currencyPair = CurrencyPair.of(base = it.get("baseCurrency").textValue(), counter = it.get("counterCurrency").textValue()),
                receivedAtMillis = timestampDoesNotMatter, exchangeTimestampMillis = null,
            )
        }
        val orderBook = OrderBook(
            exchangeName = buyOrders.first().exchangeName,
            currencyPair = buyOrders.first().currencyPair,
            buyOrders = buyOrders, sellOrders = emptyList(),
            receivedAtMillis = timestampDoesNotMatter,
            exchangeTimestampMillis = null,
        )
        val avgPriceHavingUsd = orderBook.getWeightedAverageBuyPrice(BigDecimal(1000.0), BigDecimal("1.00909226"))
        assertThat(avgPriceHavingUsd?.averagePrice).isEqualTo(BigDecimal("0.55100000"))
        assertThat(avgPriceHavingUsd?.baseCurrencyAmount).isEqualTo(BigDecimal("1798.52933632"))
    }

}
