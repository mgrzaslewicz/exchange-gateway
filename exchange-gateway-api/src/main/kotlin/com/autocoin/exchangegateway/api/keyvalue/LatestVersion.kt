package com.autocoin.exchangegateway.api.keyvalue

import java.nio.file.Path

data class LatestVersion<V>(
    val file: Path,
    val value: V,
)
