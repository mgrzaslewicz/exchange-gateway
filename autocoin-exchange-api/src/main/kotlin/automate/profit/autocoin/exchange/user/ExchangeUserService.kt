package automate.profit.autocoin.exchange.user

data class ExchangeUserDto(
    val id: String,
    val name: String,
)


interface ExchangeUserService {
    fun getExchangeUsers(userAccountId: String): List<ExchangeUserDto>
    fun getExchangeUser(userAccountId: String, exchangeUserId: String): ExchangeUserDto?
    fun exchangeUserBelongsToUserAccount(exchangeUserId: String): Boolean
}
