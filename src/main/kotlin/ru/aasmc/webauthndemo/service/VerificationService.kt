package ru.aasmc.webauthndemo.service

import ru.aasmc.webauthndemo.service.model.VerifyRequest
import ru.aasmc.webauthndemo.service.model.VerifyResult

interface VerificationService {

    fun verify(request: VerifyRequest): VerifyResult

}