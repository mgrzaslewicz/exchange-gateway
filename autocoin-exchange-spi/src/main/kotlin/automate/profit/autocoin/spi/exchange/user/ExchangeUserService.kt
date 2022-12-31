package automate.profit.autocoin.spi.exchange.user


interface ExchangeUserService {
    fun getExchangeUsers(userAccountId: String): List<ExchangeUser>
    fun getExchangeUser(
        userAccountId: String,
        exchangeUserId: String,
    ): ExchangeUser?

    fun exchangeUserBelongsToUserAccount(exchangeUserId: String): Boolean
}
