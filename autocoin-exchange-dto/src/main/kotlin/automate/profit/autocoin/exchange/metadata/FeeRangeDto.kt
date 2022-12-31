package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.metadata.FeeRange
import automate.profit.autocoin.appendNullable
import automate.profit.autocoin.spi.exchange.metadata.FeeRange as SpiFeeRange

data class FeeRangeDto(
    val beginAmount: String,
    val feeAmount: String?,
    val feeRatio: String?,
) : SerializableToJson {
    init {
        if (feeAmount == null && feeRatio == null) {
            throw error("Both feeAmount and feeRatio are null. One of them needs to be provided")
        }
    }

    fun toFeeRange() = FeeRange(
        beginAmount = this.beginAmount.toBigDecimal(),
        feeAmount = this.feeAmount?.toBigDecimal(),
        feeRatio = this.feeRatio?.toBigDecimal(),
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("""{"beginAmount":"""")
        .append(beginAmount)
        .append("\",\"feeAmount\":")
        .appendNullable(feeAmount)
        .append(""","feeRatio":""")
        .appendNullable(feeRatio)
        .append("}")
}

fun SpiFeeRange.toDto() = FeeRangeDto(
    beginAmount = this.beginAmount.toPlainString(),
    feeAmount = this.feeAmount?.toPlainString(),
    feeRatio = this.feeRatio?.toPlainString(),
)


