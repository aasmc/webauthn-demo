package ru.aasmc.webauthndemo.service.model

import ru.aasmc.webauthndemo.web.dto.VerifyRs

data class VerifyResult(
    val redirectUri: String
) {

    fun toDto(): VerifyRs {
        return VerifyRs(redirectUri)
    }

}