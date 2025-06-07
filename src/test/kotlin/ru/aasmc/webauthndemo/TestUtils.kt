package ru.aasmc.webauthndemo

import com.webauthn4j.data.AttestationConveyancePreference
import com.webauthn4j.data.AuthenticatorAssertionResponse
import com.webauthn4j.data.AuthenticatorAttachment
import com.webauthn4j.data.AuthenticatorAttestationResponse
import com.webauthn4j.data.AuthenticatorSelectionCriteria
import com.webauthn4j.data.PublicKeyCredential
import com.webauthn4j.data.PublicKeyCredentialCreationOptions
import com.webauthn4j.data.PublicKeyCredentialDescriptor
import com.webauthn4j.data.PublicKeyCredentialParameters
import com.webauthn4j.data.PublicKeyCredentialRequestOptions
import com.webauthn4j.data.PublicKeyCredentialRpEntity
import com.webauthn4j.data.PublicKeyCredentialType
import com.webauthn4j.data.PublicKeyCredentialUserEntity
import com.webauthn4j.data.ResidentKeyRequirement
import com.webauthn4j.data.UserVerificationRequirement
import com.webauthn4j.data.attestation.statement.COSEAlgorithmIdentifier
import com.webauthn4j.data.client.challenge.Challenge
import com.webauthn4j.data.extension.client.AuthenticationExtensionClientOutput
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput
import com.webauthn4j.test.client.ClientPlatform
import ru.aasmc.webauthndemo.config.AppProperties


// credit to: https://smartyr.me/blog/testing-passkeys-webauthn-with-spring/

fun authenticatePassKeys(
    clientPlatform: ClientPlatform,
    challenge: Challenge,
    publicKeyId: ByteArray,
    rpId: String
): PublicKeyCredential<AuthenticatorAssertionResponse, AuthenticationExtensionClientOutput> {
    val pkOptions = PublicKeyCredentialRequestOptions(
        challenge,
        3000,
        rpId,
        mutableListOf(
            PublicKeyCredentialDescriptor(
                PublicKeyCredentialType.PUBLIC_KEY,
                publicKeyId,
                null
            )
        ),
        UserVerificationRequirement.DISCOURAGED,
        null
    )
    return clientPlatform.get(pkOptions)
}

fun createPasskey(
    clientPlatform: ClientPlatform,
    appProperties: AppProperties,
    userId: ByteArray,
    challenge: Challenge
): PublicKeyCredential<AuthenticatorAttestationResponse, RegistrationExtensionClientOutput> {
    return clientPlatform.create(createPublicKeyCredentialCreationOptions(appProperties, userId, challenge))
}

fun createPublicKeyCredentialCreationOptions(appProperties: AppProperties,
                                             userId: ByteArray,
                                             challenge: Challenge
): PublicKeyCredentialCreationOptions {
    return PublicKeyCredentialCreationOptions(
        PublicKeyCredentialRpEntity(appProperties.id, "rpName"),
        PublicKeyCredentialUserEntity(userId, "user", "username"),
        challenge,
        listOf(
            PublicKeyCredentialParameters(
                PublicKeyCredentialType.PUBLIC_KEY,
                COSEAlgorithmIdentifier.ES256
            )
        ),
        3000,
        null,
        AuthenticatorSelectionCriteria(
            AuthenticatorAttachment.PLATFORM,
            false,
            ResidentKeyRequirement.DISCOURAGED,
            UserVerificationRequirement.PREFERRED
        ),
        null,
        AttestationConveyancePreference.NONE,
        null
    )
}