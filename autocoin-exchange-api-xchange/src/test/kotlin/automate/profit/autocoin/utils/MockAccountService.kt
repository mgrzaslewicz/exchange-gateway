package automate.profit.autocoin.utils

import org.knowm.xchange.currency.Currency
import org.knowm.xchange.dto.account.AccountInfo
import org.knowm.xchange.dto.account.FundingRecord
import org.knowm.xchange.service.account.AccountService
import org.knowm.xchange.service.trade.params.TradeHistoryParams
import org.knowm.xchange.service.trade.params.WithdrawFundsParams
import java.math.BigDecimal

class MockAccountService : AccountService {
    override fun withdrawFunds(p0: Currency?, p1: BigDecimal?, p2: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun withdrawFunds(p0: WithdrawFundsParams?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun requestDepositAddress(p0: Currency?, vararg p1: String?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getFundingHistory(p0: TradeHistoryParams?): MutableList<FundingRecord> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAccountInfo(): AccountInfo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun createFundingHistoryParams(): TradeHistoryParams {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}