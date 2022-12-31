package com.autocoin.exchangegateway.dto

interface SerializableToJson {
    fun toJson(): String = appendJson(StringBuilder()).toString()
    fun appendJson(builder: StringBuilder): StringBuilder = builder.append(toJson())
}
