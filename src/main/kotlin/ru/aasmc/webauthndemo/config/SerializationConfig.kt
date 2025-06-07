package ru.aasmc.webauthndemo.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.webauthn4j.converter.CollectedClientDataConverter
import com.webauthn4j.converter.util.ObjectConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.aasmc.webauthndemo.db.converter.JpaAttestationObjectConverter
import ru.aasmc.webauthndemo.db.converter.JpaAuthenticatorTransportConverter
import ru.aasmc.webauthndemo.db.converter.JpaClientExtensionsConverter
import ru.aasmc.webauthndemo.db.converter.JpaCollectedClientDataConverter

@Configuration
class SerializationConfig {

    @Bean
    fun objectConverter(): ObjectConverter {
        val jsonMapper = ObjectMapper().registerKotlinModule()

        val cborMapper = ObjectMapper(CBORFactory())
            .registerKotlinModule()
        return ObjectConverter(jsonMapper, cborMapper)
    }

    @Bean
    fun jpaAuthenticatorTransportConverter(): JpaAuthenticatorTransportConverter {
        return JpaAuthenticatorTransportConverter(objectConverter())
    }

    @Bean
    fun jpaClientExtensionsConverter(): JpaClientExtensionsConverter {
        return JpaClientExtensionsConverter(objectConverter())
    }

    @Bean
    fun collectedClientDataConverter(): CollectedClientDataConverter {
        return CollectedClientDataConverter(objectConverter())
    }

    @Bean
    fun jpaCollectedClientDataConverter(): JpaCollectedClientDataConverter {
        return JpaCollectedClientDataConverter(collectedClientDataConverter())
    }

    @Bean
    fun jpaAttestationStatementConverter(): JpaAttestationObjectConverter {
        return JpaAttestationObjectConverter(objectConverter())
    }

}