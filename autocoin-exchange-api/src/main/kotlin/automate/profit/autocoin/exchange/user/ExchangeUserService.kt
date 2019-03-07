package automate.profit.autocoin.exchange.user

interface ExchangeUserService {
    fun exchangeUserBelongsToUserAccount(exchangeUserId: String): Boolean
}
