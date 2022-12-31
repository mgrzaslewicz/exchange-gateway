package com.autocoin.exchangegateway.dto

fun StringBuilder.appendNullable(value: String?): StringBuilder {
    return if (value == null) {
        append("null")
    }
    else {
        append("\"")
        append(value)
        append("\"")
    }
}

fun StringBuilder.appendMapWithNullableValues(value: Map<String, String?>?): StringBuilder {
    append("{")
    if (value != null) {
        val entries = value.entries
        entries.forEachIndexed { index, entry ->
            append("\"")
            append(entry.key)
            append("\":\"")
            appendNullable(entry.value)
            if (index < entries.size - 1) {
                append("\",")
            }
        }
    }
    return append("}")
}

fun StringBuilder.appendMap(value: Map<String, SerializableToJson>): StringBuilder {
    append("{")
    val entries = value.entries
    entries.forEachIndexed { index, entry ->
        append("\"")
        append(entry.key)
        append("\":")
        entry.value.appendJson(this)
        if (index < entries.size - 1) {
            append("\",")
        }
    }
    return append("}")
}
