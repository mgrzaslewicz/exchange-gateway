package automate.profit.autocoin.exchange.metadata

import automate.profit.autocoin.SerializableToJson
import automate.profit.autocoin.api.exchange.metadata.FeeRanges

data class FeeRangesDto(
    val makerFeeRanges: List<FeeRangeDto>,
    val takerFeeRanges: List<FeeRangeDto>,
) : SerializableToJson {
    fun toFeeRanges() = FeeRanges(
        makerFees = makerFeeRanges.map { it.toFeeRange() },
        takerFees = takerFeeRanges.map { it.toFeeRange() },
    )

    override fun appendJson(builder: StringBuilder) = builder
        .append("{")
        .append("\"makerFeeRanges\":[")
        .append(makerFeeRanges.joinToString(",") { it.toJson() })
        .append("],")
        .append("\"takerFeeRanges\":[")
        .append(takerFeeRanges.joinToString(",") { it.toJson() })
        .append("]")
        .append("}")
}

fun FeeRanges.toDto() = FeeRangesDto(
    makerFeeRanges = this.makerFees.map { it.toDto() },
    takerFeeRanges = this.takerFees.map { it.toDto() },
)

