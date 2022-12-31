package automate.profit.autocoin.util

import java.time.Instant
import java.util.*

fun Instant.toDate() = Date(this.toEpochMilli())
