package ru.aasmc.webauthndemo.web.controller

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import ru.aasmc.webauthndemo.exception.WebAuthnException
import ru.aasmc.webauthndemo.service.VerificationService
import ru.aasmc.webauthndemo.service.model.VerifyRequest
import ru.aasmc.webauthndemo.web.dto.ErrorRs
import ru.aasmc.webauthndemo.web.dto.VerifyRq
import ru.aasmc.webauthndemo.web.dto.VerifyRs

@RestController
@RequestMapping("/api/v1/verify")
class VerificationController(
    private val service: VerificationService
) {

    @PostMapping
    fun verify(@RequestBody req: VerifyRq): ResponseEntity<VerifyRs> {
        log.info("Received POST request to verify authentication by WebAuthn: {}", req)
        val result = service.verify(VerifyRequest.fromDto(req))
        return ResponseEntity.ok(result.toDto())
    }

    @ExceptionHandler(WebAuthnException::class)
    fun handleWebAuthnException(ex: WebAuthnException): ResponseEntity<ErrorRs> {
        log.error("Caught WebAuthnException. Message: {}", ex.message)
        val error = ErrorRs(code = ex.status.name, message = ex.message ?: "unknown error")
        return ResponseEntity.status(ex.status)
            .body(error)
    }

    @ExceptionHandler(value = [RuntimeException::class, Exception::class])
    fun handleOtherExceptions(ex: Throwable): ErrorRs {
        log.error(
            "Caught unexpected exception. Message: {}",
            ex.message ?: "unknown message",
            ex
        )
        return ErrorRs(
            HttpStatus.INTERNAL_SERVER_ERROR.name,
            ex.message ?: "unknown error"
        )
    }


    companion object {
        private val log = LoggerFactory.getLogger(VerificationController::class.java)
    }
}