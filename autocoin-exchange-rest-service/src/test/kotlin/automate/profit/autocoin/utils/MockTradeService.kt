package automate.profit.autocoin.utils

import org.knowm.xchange.dto.Order
import org.knowm.xchange.dto.trade.*
import org.knowm.xchange.service.trade.TradeService
import org.knowm.xchange.service.trade.params.CancelOrderParams
import org.knowm.xchange.service.trade.params.TradeHistoryParams
import org.knowm.xchange.service.trade.params.orders.OpenOrdersParams

class MockTradeService : TradeService {

    override fun placeStopOrder(p0: StopOrder?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelOrder(p0: String?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun cancelOrder(p0: CancelOrderParams?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun placeLimitOrder(p0: LimitOrder?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun placeMarketOrder(p0: MarketOrder?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getTradeHistory(p0: TradeHistoryParams?): UserTrades {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createOpenOrdersParams(): OpenOrdersParams {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createTradeHistoryParams(): TradeHistoryParams {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun verifyOrder(p0: LimitOrder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun verifyOrder(p0: MarketOrder?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenOrders(): OpenOrders {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOpenOrders(p0: OpenOrdersParams?): OpenOrders {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getOrder(vararg p0: String?): MutableCollection<Order> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
