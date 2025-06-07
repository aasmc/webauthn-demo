package ru.aasmc.webauthndemo.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.webauthn4j.WebAuthnManager
import com.webauthn4j.credential.CredentialRecordImpl
import com.webauthn4j.data.AuthenticationData
import com.webauthn4j.data.AuthenticationParameters
import com.webauthn4j.data.attestation.AttestationObject
import com.webauthn4j.data.attestation.authenticator.AuthenticatorData
import com.webauthn4j.data.client.Origin
import com.webauthn4j.data.client.challenge.DefaultChallenge
import com.webauthn4j.server.ServerProperty
import com.webauthn4j.util.Base64UrlUtil
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import ru.aasmc.webauthndemo.config.AppProperties
import ru.aasmc.webauthndemo.db.entity.CredentialEntity
import ru.aasmc.webauthndemo.db.repoistory.CredentialEntityRepository
import ru.aasmc.webauthndemo.exception.WebAuthnException
import ru.aasmc.webauthndemo.service.model.AuthenticationResponse
import ru.aasmc.webauthndemo.service.model.PublicKeyCredentialResponse
import ru.aasmc.webauthndemo.service.model.VerifyRequest
import ru.aasmc.webauthndemo.service.model.VerifyResult

@Service
class DefaultVerificationService(
    private val repository: CredentialEntityRepository,
    private val objectMapper: ObjectMapper,
    private val appProperties: AppProperties
) : VerificationService {

    override fun verify(request: VerifyRequest): VerifyResult {
        log.info("Started verifying auth operation with id: {}", request.authOperationId)

        try {
            val manager = WebAuthnManager.createNonStrictWebAuthnManager()
            val authenticationData: AuthenticationData = getAuthenticatorData(request, manager)
            val credentialId = getCredentialId(authenticationData)
            verifyAuthenticationData(credentialId, manager, authenticationData)
            return VerifyResult("http://locahost:8080/redirect-uri")
        } catch (t: Throwable) {
            throw WebAuthnException(HttpStatus.INTERNAL_SERVER_ERROR, t.message ?: "unknown exception", t)
        }
    }

    private fun verifyAuthenticationData(
        credentialId: String,
        manager: WebAuthnManager,
        authenticationData: AuthenticationData
    ) {
        val credentialEntity = repository.findByCredentialId(credentialId)
            .orElseThrow { WebAuthnException(HttpStatus.NOT_FOUND, "No credential in store") }

        val challenge = DefaultChallenge(
            credentialEntity.challenge
                ?: throw WebAuthnException(HttpStatus.NOT_FOUND, "No challenge in store")
        )
        val origin = Origin(appProperties.origin)
        val serverProperty = ServerProperty(
            origin,
            appProperties.id,
            challenge
        )

        val credentialRecord = getCredentialRecord(credentialEntity)

        val allowCredentials: List<ByteArray>? = null
        val userVerificationRequired = appProperties.userVerificationRequired
        val userPresenceRequired = appProperties.userPresenceRequired

        val authenticationParameters = AuthenticationParameters(
            serverProperty,
            credentialRecord,
            allowCredentials,
            userVerificationRequired,
            userPresenceRequired
        )

        manager.verify(authenticationData, authenticationParameters)
        updateSignCount(authenticationData, credentialEntity)
    }

    private fun updateSignCount(authenticationData: AuthenticationData, credentialEntity: CredentialEntity) {
        val toCopy = credentialEntity.attestationObject?.authenticatorData
        val newSignCount = authenticationData.authenticatorData?.signCount ?: throw WebAuthnException(
            HttpStatus.BAD_REQUEST,
            "no sign count in request"
        )
        val updatedData = AuthenticatorData(
            toCopy!!.rpIdHash,
            toCopy.flags,
            newSignCount,
            toCopy.attestedCredentialData,
            toCopy.extensions
        )

        val updatedAttestation = AttestationObject(
            updatedData,
            credentialEntity.attestationObject!!.attestationStatement
        )
        credentialEntity.attestationObject = updatedAttestation
        repository.save(credentialEntity)
    }

    private fun getCredentialRecord(credentialEntity: CredentialEntity): CredentialRecordImpl {
        val attestationObject = credentialEntity.attestationObject
        val clientData = credentialEntity.clientData
        val extensions = credentialEntity.clientExtensions
        val transports = credentialEntity.transports
        val credentialRecord = CredentialRecordImpl(attestationObject!!, clientData, extensions, transports)
        return credentialRecord
    }

    private fun getCredentialId(authenticationData: AuthenticationData): String {
        val credentialId = Base64UrlUtil.encodeToString(
            authenticationData.credentialId ?: throw WebAuthnException(
                HttpStatus.NOT_FOUND,
                "No credential id in authentication data"
            )
        )
        return credentialId
    }

    private fun getAuthenticatorData(
        request: VerifyRequest,
        manager: WebAuthnManager
    ): AuthenticationData {
        val authenticationResponse = AuthenticationResponse.fromRequest(request)
        val publicKeyCredentialResponse = PublicKeyCredentialResponse(request.webAuthn.id, request.webAuthn.rawId, authenticationResponse)
        val authResponseStr = objectMapper.writeValueAsString(publicKeyCredentialResponse)
        val authenticationData: AuthenticationData = manager.parseAuthenticationResponseJSON(authResponseStr)
        return authenticationData
    }


    companion object {
        private val log = LoggerFactory.getLogger(DefaultVerificationService::class.java)
    }
}