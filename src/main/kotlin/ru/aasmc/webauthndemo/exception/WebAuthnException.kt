package ru.aasmc.webauthndemo.exception

import org.springframework.http.HttpStatus
import java.lang.RuntimeException

class WebAuthnException(
    val status: HttpStatus,
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)