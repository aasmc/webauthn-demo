package ru.aasmc.webauthndemo.db.converter

import com.webauthn4j.converter.util.ObjectConverter
import com.webauthn4j.data.attestation.AttestationObject
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class JpaAttestationObjectConverter(
    private val objectConverter: ObjectConverter
): AttributeConverter<AttestationObject, ByteArray> {


    override fun convertToDatabaseColumn(attribute: AttestationObject?): ByteArray? {
        return attribute?.let {
            objectConverter.cborConverter.writeValueAsBytes(it)
        }
    }

    override fun convertToEntityAttribute(dbData: ByteArray?): AttestationObject? {
        return dbData?.let {
            objectConverter.cborConverter.readValue(it, AttestationObject::class.java)
        }
    }
}