package ru.aasmc.webauthndemo.web.dto

data class VerifyRq(
    val authOperationId: String,
    val webAuthn: WebAuthnData
)

data class WebAuthnData(
    val id: String,
    val rawId: String,
    val authenticatorData: String,
    val clientDataJSON: String,
    val signature: String,
    val userHandle: String? = null
)
