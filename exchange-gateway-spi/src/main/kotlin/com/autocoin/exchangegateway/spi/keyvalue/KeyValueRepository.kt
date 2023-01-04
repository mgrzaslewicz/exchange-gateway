package com.autocoin.exchangegateway.spi.keyvalue

/**
 * @param R - read/write result
 * @param K - key
 * @param V - value
 */
interface KeyValueRepository<R, K, V> {
    fun saveNewVersion(
        key: K,
        value: V,
    ): R

    fun getLatestVersion(key: K): R?
    fun keepLastNVersions(
        key: K,
        maxVersions: Int,
    )
}
