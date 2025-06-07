package ru.aasmc.webauthndemo

import com.fasterxml.jackson.core.Base64Variants
import com.fasterxml.jackson.databind.ObjectMapper
import com.webauthn4j.WebAuthnManager
import com.webauthn4j.data.AuthenticatorAssertionResponse
import com.webauthn4j.data.AuthenticatorAttestationResponse
import com.webauthn4j.data.PublicKeyCredential
import com.webauthn4j.data.client.challenge.Challenge
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientOutput
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput
import com.webauthn4j.test.EmulatorUtil
import com.webauthn4j.test.authenticator.u2f.FIDOU2FAuthenticator
import com.webauthn4j.test.client.ClientPlatform
import com.webauthn4j.util.Base64UrlUtil
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import ru.aasmc.webauthndemo.config.AppProperties
import ru.aasmc.webauthndemo.db.entity.CredentialEntity
import ru.aasmc.webauthndemo.db.repoistory.CredentialEntityRepository
import ru.aasmc.webauthndemo.web.dto.VerifyRq
import ru.aasmc.webauthndemo.web.dto.VerifyRs
import ru.aasmc.webauthndemo.web.dto.WebAuthnData
import java.util.*

@ActiveProfiles("test")
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VerificationControllerTest @Autowired constructor(
    private val appProperties: AppProperties,
    private val credentialEntityRepository: CredentialEntityRepository,
    private val objectMapper: ObjectMapper,
    private val webTestClient: WebTestClient
) {

    private val url = "/api/v1/verify"
    private val platform: ClientPlatform = EmulatorUtil.createClientPlatform( FIDOU2FAuthenticator())

    @Test
    fun verify() {
        val userId = UUID.randomUUID().toString()
        val userIdBytes = userId.toByteArray()
        val challenge = DefaultChallenge()
        val passKeys = createPasskey(platform, appProperties, userIdBytes, challenge)
        val credentialEntity = saveCredentials(challenge, passKeys)
        val authOperationId = UUID.randomUUID().toString()

        val authenticatePassKeyResponse = authenticatePassKeys(platform, challenge, passKeys.rawId, appProperties.id)

        val req = prepareVerifyRequest(authOperationId, authenticatePassKeyResponse)

        webTestClient.post()
            .uri(url)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchange()
            .expectStatus().isOk
            .expectBody(VerifyRs::class.java)
            .value { rs ->
                assertThat(rs.redirectUri).isEqualTo("http://locahost:8080/redirect-uri")
            }

    }

    private fun saveCredentials(
        challenge: Challenge,
        passKeys: PublicKeyCredential<AuthenticatorAttestationResponse, RegistrationExtensionClientOutput>
    ): CredentialEntity {
        val manager = WebAuthnManager.createNonStrictWebAuthnManager()
        objectMapper.setBase64Variant(Base64Variants.MODIFIED_FOR_URL);
        val response = objectMapper.writeValueAsString(passKeys)

        val registrationData = manager.parseRegistrationResponseJSON(response)
        val credentialIdBytes =
            registrationData.attestationObject!!.authenticatorData.attestedCredentialData!!.credentialId
        val credentialId = Base64UrlUtil.encodeToString(credentialIdBytes)
        val credentialEntity = CredentialEntity(
            null,
            credentialId,
            registrationData.attestationObject,
            registrationData.collectedClientData,
            registrationData.clientExtensions,
            registrationData.transports,
            registrationData.attestationObject!!.authenticatorData.signCount,
            Base64UrlUtil.encodeToString(challenge.value)
        )
        return credentialEntityRepository.save(credentialEntity)
    }

    private fun prepareVerifyRequest(
        authOperationId: String,
        passKeys: PublicKeyCredential<AuthenticatorAssertionResponse, AuthenticationExtensionClientOutput>
    ): VerifyRq {
        return VerifyRq(
            authOperationId,
            WebAuthnData(
                id = passKeys.id!!,
                rawId = Base64UrlUtil.encodeToString(passKeys.rawId),
                authenticatorData = Base64UrlUtil.encodeToString(passKeys.response!!.authenticatorData),
                clientDataJSON = Base64UrlUtil.encodeToString(passKeys.response!!.clientDataJSON),
                signature = Base64UrlUtil.encodeToString(passKeys.response!!.signature),
                userHandle = null
            )
        )
    }
}