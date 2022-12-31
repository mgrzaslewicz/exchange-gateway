package automate.profit.autocoin

interface SerializableToJson {
    fun toJson(): String = appendJson(StringBuilder()).toString()
    fun appendJson(builder: StringBuilder): StringBuilder = builder.append(toJson())
}
