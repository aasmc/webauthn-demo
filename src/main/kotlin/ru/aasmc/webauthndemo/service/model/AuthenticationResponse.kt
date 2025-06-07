package ru.aasmc.webauthndemo.service.model

data class PublicKeyCredentialResponse(
    val id: String,
    val rawId: String,
    val response: AuthenticationResponse,
    val type: String = "public-key"
)

data class AuthenticationResponse(
    val authenticatorData: String,
    val clientDataJSON: String,
    val signature: String,
    val userHandle: String? = null
) {

    companion object {
        fun fromRequest(request: VerifyRequest): AuthenticationResponse {
            return AuthenticationResponse(
                authenticatorData = request.webAuthn.authenticatorData,
                clientDataJSON = request.webAuthn.clientDataJSON,
                signature = request.webAuthn.signature,
                userHandle = request.webAuthn.userHandle
            )
        }
    }

}
