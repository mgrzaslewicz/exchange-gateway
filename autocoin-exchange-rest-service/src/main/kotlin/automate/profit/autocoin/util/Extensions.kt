package automate.profit.autocoin.util

import java.time.Instant
import java.util.*

fun Date.toEpochSeconds() = this.toInstant().epochSecond
fun Instant.toDate() = Date(this.toEpochMilli())
