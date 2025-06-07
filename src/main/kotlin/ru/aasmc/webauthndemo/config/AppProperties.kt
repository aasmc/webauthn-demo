package ru.aasmc.webauthndemo.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.relying-party")
data class AppProperties (
    val origin: String,
    val id: String,
    val userPresenceRequired: Boolean,
    val userVerificationRequired: Boolean
)
