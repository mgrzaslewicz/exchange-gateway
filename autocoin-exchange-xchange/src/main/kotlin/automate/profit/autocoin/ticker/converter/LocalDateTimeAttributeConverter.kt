package automate.profit.autocoin.ticker.converter

import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter(autoApply = true)
class LocalDateTimeAttributeConverter : AttributeConverter<ZonedDateTime, Timestamp> {

    override fun convertToDatabaseColumn(dateTime: ZonedDateTime?): Timestamp? = if (dateTime == null) null else Timestamp.from(dateTime.toInstant())

    override fun convertToEntityAttribute(timestamp: Timestamp?): ZonedDateTime? = if (timestamp == null) null else ZonedDateTime.ofInstant(timestamp.toInstant(), ZoneId.systemDefault())
}