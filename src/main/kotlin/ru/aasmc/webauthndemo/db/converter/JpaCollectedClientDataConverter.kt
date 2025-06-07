package ru.aasmc.webauthndemo.db.converter

import com.webauthn4j.converter.CollectedClientDataConverter
import com.webauthn4j.data.client.CollectedClientData
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class JpaCollectedClientDataConverter(
    private val converter: CollectedClientDataConverter
): AttributeConverter<CollectedClientData, ByteArray> {
    override fun convertToDatabaseColumn(attribute: CollectedClientData?): ByteArray? {
        return attribute?.let { converter.convertToBytes(it) }
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): CollectedClientData? {
        return dbData?.let { converter.convert(it) }
    }
}