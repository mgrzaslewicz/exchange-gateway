package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.metadata.CurrencyMetadata
import automate.profit.autocoin.appendNullable
import automate.profit.autocoin.spi.exchange.metadata.CurrencyMetadata as SpiCurrencyMetadata

data class CurrencyMetadataDto(
    val scale: Int,
    val withdrawalFeeAmount: String?,
    val minWithdrawalAmount: String?,
    val withdrawalEnabled: Boolean?,
    val depositEnabled: Boolean?,
) : SerializableToJson {
    fun toCurrencyMetadata(): SpiCurrencyMetadata = CurrencyMetadata(
        scale = scale,
        withdrawalFeeAmount = withdrawalFeeAmount?.toBigDecimal(),
        minWithdrawalAmount = minWithdrawalAmount?.toBigDecimal(),
        withdrawalEnabled = withdrawalEnabled,
        depositEnabled = depositEnabled,
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("""{"scale":""")
        .append(scale)
        .append(""","withdrawalFeeAmount":""")
        .appendNullable(withdrawalFeeAmount)
        .append(""","minWithdrawalAmount":""")
        .appendNullable(minWithdrawalAmount)
        .append(""","withdrawalEnabled":""")
        .append(withdrawalEnabled)
        .append(""","depositEnabled":""")
        .append(depositEnabled)
        .append("}")

}

fun SpiCurrencyMetadata.toDto() = CurrencyMetadataDto(
    scale = scale,
    withdrawalFeeAmount = withdrawalFeeAmount?.toPlainString(),
    minWithdrawalAmount = minWithdrawalAmount?.toPlainString(),
    withdrawalEnabled = withdrawalEnabled,
    depositEnabled = depositEnabled,
)
