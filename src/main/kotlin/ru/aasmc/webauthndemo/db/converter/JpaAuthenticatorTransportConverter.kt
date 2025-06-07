package ru.aasmc.webauthndemo.db.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.webauthn4j.converter.util.ObjectConverter
import com.webauthn4j.data.AuthenticatorTransport
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import java.io.ByteArrayInputStream

@Converter
class JpaAuthenticatorTransportConverter(
    private val objectConverter: ObjectConverter
) : AttributeConverter<Set<AuthenticatorTransport>, ByteArray> {

    override fun convertToDatabaseColumn(attribute: Set<AuthenticatorTransport>?): ByteArray? {
        return attribute?.let { objectConverter.jsonConverter.writeValueAsBytes(it) }
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): Set<AuthenticatorTransport>? {
        return dbData?.let {
            objectConverter.jsonConverter.readValue(
                ByteArrayInputStream(it),
                object : TypeReference<Set<AuthenticatorTransport>>() {})
        }
    }
}