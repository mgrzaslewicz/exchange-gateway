package automate.profit.autocoin.exchange.order.authorized

import automate.profit.autocoin.api.exchange.ApiKey
import automate.profit.autocoin.api.exchange.ApiKeySupplier
import automate.profit.autocoin.api.exchange.currency.CurrencyPair
import automate.profit.autocoin.api.exchange.order.Order
import automate.profit.autocoin.exchange.currency.defaultCurrencyPairToXchange
import automate.profit.autocoin.exchange.order.service.authorized.XchangeAuthorizedOrderServiceFactory
import automate.profit.autocoin.exchange.xchange.ExchangeNames.Companion.kucoin
import automate.profit.autocoin.exchange.xchange.XchangeProvider
import automate.profit.autocoin.spi.exchange.ExchangeName
import automate.profit.autocoin.spi.exchange.order.OrderSide
import automate.profit.autocoin.spi.exchange.order.OrderStatus.*
import automate.profit.autocoin.spi.exchange.order.service.authorized.AuthorizedOrderService
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import mu.KLogging
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.knowm.xchange.Exchange
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.dto.trade.OpenOrders
import org.knowm.xchange.service.trade.TradeService
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.Clock
import java.util.*
import java.util.function.Supplier
import automate.profit.autocoin.spi.exchange.apikey.ApiKey as SpiApiKey
import org.knowm.xchange.dto.Order as XchangeOrder

@ExtendWith(MockitoExtension::class)
class XchangeAuthorizedOrderServiceTest {
    companion object : KLogging()

    @Mock
    private lateinit var wrappedTradeService: TradeService
    private lateinit var tested: AuthorizedOrderService

    private val buyOrderId1 = "buy-order-id-1"
    private val buyOrderId2 = "buy-order-id-2"
    private val sellOrderId1 = "sell-order-id-1"

    private val currencyPair = CurrencyPair.of("ETH/BTC")
    private val limitPriceBigDecimal = BigDecimal.ONE

    private val openBuyOrder1 = Order(
        exchangeName = kucoin,
        exchangeOrderId = buyOrderId1,
        side = OrderSide.BID_BUY,
        orderedAmount = BigDecimal(5.5),
        filledAmount = BigDecimal(3.5),
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = PARTIALLY_FILLED,
        receivedAtMillis = System.currentTimeMillis(), exchangeTimestampMillis = null,
    )

    private val openBuyOrder2 = Order(
        exchangeName = kucoin,
        exchangeOrderId = buyOrderId2,
        side = OrderSide.BID_BUY,
        orderedAmount = BigDecimal(5.5),
        filledAmount = BigDecimal.ZERO,
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = NEW,
        receivedAtMillis = System.currentTimeMillis(), exchangeTimestampMillis = null,
    )

    private val openSellOrder1 = Order(
        exchangeName = kucoin,
        exchangeOrderId = sellOrderId1,
        side = OrderSide.ASK_SELL,
        orderedAmount = BigDecimal(45.5),
        filledAmount = BigDecimal.ZERO,
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = NEW,
        receivedAtMillis = System.currentTimeMillis(), exchangeTimestampMillis = null,
    )

    @BeforeEach
    fun setUp() {
        tested = XchangeAuthorizedOrderServiceFactory<String>(
            xchangeProvider = object : XchangeProvider {
                override operator fun invoke(
                    exchangeName: ExchangeName,
                    apiKey: Supplier<SpiApiKey>?,
                ): Exchange {
                    return mock<Exchange>().apply { whenever(tradeService).thenReturn(wrappedTradeService) }
                }

            },
            clock = Clock.systemDefaultZone(),
        ).createAuthorizedOrderService(
            kucoin,
            apiKey = ApiKeySupplier(
                id = "1-2-3",
                supplier = {
                    ApiKey(
                        publicKey = "public-key",
                        secretKey = "secret-key",
                    )
                },
            ),
        )
    }

    @Test
    fun shouldReturnSellOrderWithId() {
        // given
        returnPlacedOrderId(sellOrderId1)
        // when
        val sellOrder = tested.placeLimitSellOrder(currencyPair, BigDecimal(3.0), BigDecimal(2.0))
        // then
        assertThat(sellOrder.exchangeOrderId).isEqualTo(sellOrderId1)
    }

    @Test
    fun shouldReturnBuyOrderWithId() {
        // given
        returnPlacedOrderId("some-id")
        // when
        val buyOrder = tested.placeLimitBuyOrder(currencyPair, BigDecimal(3.0), BigDecimal(2.0))
        // then
        assertThat(buyOrder.exchangeOrderId).isEqualTo("some-id")
    }

    @Test
    fun shouldOrderExistInOpenOrders() {
        // given
        returnOpenOrdersWhenAnyCurrencyPair(openBuyOrder1, openSellOrder1)
        // when
        val openOrders = tested.getOpenOrders()
        // then
        assertThat(openOrders.map { it.exchangeOrderId }).contains(openBuyOrder1.exchangeOrderId)
    }

    @Test
    fun shouldOrderExistInOpenOrdersForGivenCurrencyPair() {
        // given
        returnOpenOrdersWhenGivenCurrencyPair(openSellOrder1)
        // when
        val openOrders = tested.getOpenOrders(currencyPair)
        // then
        assertThat(openOrders.map { it.exchangeOrderId }).containsOnly(openSellOrder1.exchangeOrderId)
    }

    @Test
    fun shouldOrderNotExistInOpenOrders() {
        // given
        returnOpenOrdersWhenAnyCurrencyPair(openBuyOrder1, openSellOrder1)
        // when
        val openOrders = tested.getOpenOrders()
        // then
        assertThat(openOrders.map { it.exchangeOrderId }).doesNotContain(openBuyOrder2.exchangeOrderId)
    }

    private fun returnPlacedOrderId(orderId: String) {
        whenever(wrappedTradeService.placeLimitOrder(any(LimitOrder::class.java))).thenReturn(orderId)
    }

    private fun returnOpenOrdersWhenAnyCurrencyPair(vararg orders: Order) {
        // any(OpenOrdersParams::class.java)
        whenever(wrappedTradeService.getOpenOrders()).thenReturn(
            OpenOrders(
                orders.map { it.toXchangeLimitOrder() },
            ),
        )
    }

    private fun returnOpenOrdersWhenGivenCurrencyPair(vararg orders: Order) {
        whenever(wrappedTradeService.getOpenOrders(any(OpenOrdersParams::class.java))).thenReturn(
            OpenOrders(
                orders.map { it.toXchangeLimitOrder() },
            ),
        )
    }

    private fun OrderSide.toXchangeType() = if (this == OrderSide.BID_BUY) {
        org.knowm.xchange.dto.Order.OrderType.BID
    }
    else {
        org.knowm.xchange.dto.Order.OrderType.ASK
    }

    private fun Order.xchangeOrderStatus() = when (this.status) {
        NEW -> XchangeOrder.OrderStatus.NEW
        FILLED -> XchangeOrder.OrderStatus.FILLED
        PARTIALLY_CANCELED -> XchangeOrder.OrderStatus.PARTIALLY_CANCELED
        PARTIALLY_FILLED -> XchangeOrder.OrderStatus.PARTIALLY_FILLED
        CANCELED -> XchangeOrder.OrderStatus.CANCELED
        NOT_AVAILABLE -> null
        else -> throw IllegalStateException("Status $this not handled")
    }

    private fun Order.toXchangeLimitOrder(): LimitOrder = LimitOrder.Builder(
        this.side.toXchangeType(),
        defaultCurrencyPairToXchange.apply(this.currencyPair),
    )
        .id(this.exchangeOrderId)
        .originalAmount(this.orderedAmount)
        .limitPrice(this.price)
        .orderStatus(this.xchangeOrderStatus())
        .timestamp(this.exchangeTimestampMillis?.let { Date(it) })
        .build()

}


