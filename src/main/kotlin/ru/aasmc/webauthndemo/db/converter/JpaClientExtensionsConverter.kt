package ru.aasmc.webauthndemo.db.converter

import com.fasterxml.jackson.core.type.TypeReference
import com.webauthn4j.converter.util.ObjectConverter
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class JpaClientExtensionsConverter(
    private val objectConverter: ObjectConverter
) : AttributeConverter<AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput>, String> {
    override fun convertToDatabaseColumn(attribute: AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput>?): String? {
        return attribute?.let { objectConverter.jsonConverter.writeValueAsString(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput>? {
        return dbData?.let {
            objectConverter.jsonConverter.readValue(
                it,
                object : TypeReference<AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput>>() {})
        }
    }
}