package ru.aasmc.webauthndemo.service.model

import ru.aasmc.webauthndemo.web.dto.VerifyRq

data class VerifyRequest(
    val authOperationId: String,
    val webAuthn: WebAuthnRequest
) {
    companion object {
        fun fromDto(dto: VerifyRq): VerifyRequest {
            return VerifyRequest(
                authOperationId = dto.authOperationId,
                webAuthn = WebAuthnRequest(
                    id = dto.webAuthn.id,
                    rawId = dto.webAuthn.rawId,
                    authenticatorData = dto.webAuthn.authenticatorData,
                    clientDataJSON = dto.webAuthn.clientDataJSON,
                    signature = dto.webAuthn.signature,
                    userHandle = dto.webAuthn.userHandle
                )
            )
        }
    }
}

data class WebAuthnRequest(
    val id: String,
    val rawId: String,
    val authenticatorData: String,
    val clientDataJSON: String,
    val signature: String,
    val userHandle: String? = null
)
