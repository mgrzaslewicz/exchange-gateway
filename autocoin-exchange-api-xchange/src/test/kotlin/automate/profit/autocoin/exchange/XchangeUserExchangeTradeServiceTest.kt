package automate.profit.autocoin.exchange

import automate.profit.autocoin.exchange.SupportedExchange.KUCOIN
import automate.profit.autocoin.exchange.currency.CurrencyPair
import automate.profit.autocoin.exchange.order.ExchangeOrder
import automate.profit.autocoin.exchange.order.ExchangeOrderStatus.NEW
import automate.profit.autocoin.exchange.order.ExchangeOrderStatus.PARTIALLY_FILLED
import automate.profit.autocoin.exchange.order.ExchangeOrderType.ASK_SELL
import automate.profit.autocoin.exchange.order.ExchangeOrderType.BID_BUY
import automate.profit.autocoin.exchange.order.toXchangeLimitOrder
import automate.profit.autocoin.exchange.peruser.XchangeUserExchangeTradeService
import automate.profit.autocoin.exchange.ratelimiter.NoOpExchangeRateLimiter
import com.nhaarman.mockitokotlin2.whenever
import mu.KLogging
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.knowm.xchange.dto.trade.LimitOrder
import org.knowm.xchange.dto.trade.OpenOrders
import org.knowm.xchange.service.trade.TradeService
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@ExtendWith(MockitoExtension::class)
class XchangeUserExchangeTradeServiceTest {
    companion object : KLogging()

    @Mock
    private lateinit var wrappedTradeService: TradeService
    private lateinit var tested: XchangeUserExchangeTradeService

    private val buyOrderId1 = "buy-order-id-1"
    private val buyOrderId2 = "buy-order-id-2"
    private val sellOrderId1 = "sell-order-id-1"

    private val currencyPair = CurrencyPair.of("ETH/BTC")
    private val limitPriceBigDecimal = BigDecimal.ONE

    private val openBuyOrder1 = ExchangeOrder(
        exchangeName = KUCOIN.exchangeName,
        orderId = buyOrderId1,
        type = BID_BUY,
        orderedAmount = BigDecimal(5.5),
        filledAmount = BigDecimal(3.5),
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = PARTIALLY_FILLED,
        timestamp = null
    )

    private val openBuyOrder2 = ExchangeOrder(
        exchangeName = KUCOIN.exchangeName,
        orderId = buyOrderId2,
        type = BID_BUY,
        orderedAmount = BigDecimal(5.5),
        filledAmount = BigDecimal.ZERO,
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = NEW,
        timestamp = null
    )

    private val openSellOrder1 = ExchangeOrder(
        exchangeName = KUCOIN.exchangeName,
        orderId = sellOrderId1,
        type = ASK_SELL,
        orderedAmount = BigDecimal(45.5),
        filledAmount = BigDecimal.ZERO,
        price = limitPriceBigDecimal,
        currencyPair = currencyPair,
        status = NEW,
        timestamp = null
    )

    @BeforeEach
    fun setUp() {
        tested = XchangeUserExchangeTradeService(
            exchangeName = KUCOIN.exchangeName,
            wrapped = wrappedTradeService,
            exchangeRateLimiter = NoOpExchangeRateLimiter()
        )
    }

    @Test
    fun shouldReturnTrueForCompletedOrder() {
        // given
        noCreatedIdInOpenOrders()
        // when
        val completed = tested.isOrderNotOpen(openBuyOrder2)
        // then
        assertThat(completed).isTrue()
    }

    @Test
    fun shouldReturnFalseForIncompleteOrder() {
        // given
        createdIdInOpenOrders()
        // when
        val completed = tested.isOrderNotOpen(openBuyOrder2)
        // then
        assertThat(completed).isFalse()
    }

    @Test
    fun shouldReturnSellOrderWithId() {
        // given
        returnOrderId(sellOrderId1)
        // when
        val sellOrder = tested.placeSellOrder(currencyPair, BigDecimal(3.0), BigDecimal(2.0))
        // then
        assertThat(sellOrder.orderId).isEqualTo(sellOrderId1)
    }

    @Test
    fun shouldReturnBuyOrderWithId() {
        // given
        returnOrderId("some-id")
        // when
        val buyOrder = tested.placeBuyOrder(currencyPair, BigDecimal(3.0), BigDecimal(2.0))
        // then
        assertThat(buyOrder.orderId).isEqualTo("some-id")
    }

    private fun returnOrderId(orderId: String) {
        whenever(wrappedTradeService.placeLimitOrder(any(LimitOrder::class.java))).thenReturn(orderId)
    }

    private fun noCreatedIdInOpenOrders() {
        whenever(wrappedTradeService.getOpenOrders(any(OpenOrdersParams::class.java))).thenReturn(
            OpenOrders(
                listOf(
                    openBuyOrder1.toXchangeLimitOrder(),
                    openSellOrder1.toXchangeLimitOrder()
                )
            )
        )
    }

    private fun createdIdInOpenOrders() {
        whenever(wrappedTradeService.getOpenOrders(any(OpenOrdersParams::class.java))).thenReturn(
            OpenOrders(
                listOf(
                    openBuyOrder1.toXchangeLimitOrder(),
                    openBuyOrder2.toXchangeLimitOrder(),
                    openSellOrder1.toXchangeLimitOrder()
                )
            )
        )
    }

}


