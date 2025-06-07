package ru.aasmc.webauthndemo.db.entity

import com.webauthn4j.data.AuthenticatorTransport
import com.webauthn4j.data.attestation.AttestationObject
import com.webauthn4j.data.client.CollectedClientData
import com.webauthn4j.data.extension.client.AuthenticationExtensionsClientOutputs
import com.webauthn4j.data.extension.client.RegistrationExtensionClientOutput
import jakarta.persistence.*
import ru.aasmc.webauthndemo.db.converter.JpaAttestationObjectConverter
import ru.aasmc.webauthndemo.db.converter.JpaAuthenticatorTransportConverter
import ru.aasmc.webauthndemo.db.converter.JpaClientExtensionsConverter
import ru.aasmc.webauthndemo.db.converter.JpaCollectedClientDataConverter

@Entity
@Table(name = "credential")
class CredentialEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,
    @Column(name = "credential_id", nullable = false)
    var credentialId: String,
    @Convert(converter = JpaAttestationObjectConverter::class)
    @Column(name = "attestation_object", nullable = false)
    var attestationObject: AttestationObject? = null,
    @Convert(converter = JpaCollectedClientDataConverter::class)
    @Column(name = "client_data", nullable = false)
    var clientData: CollectedClientData? = null,
    @Convert(converter = JpaClientExtensionsConverter::class)
    @Column(name = "client_extensions")
    var clientExtensions: AuthenticationExtensionsClientOutputs<RegistrationExtensionClientOutput>? = null,
    @Convert(converter = JpaAuthenticatorTransportConverter::class)
    @Column(name = "transports")
    var transports: Set<AuthenticatorTransport>? = null,
    @Column(name = "sign_count", nullable = false)
    var signCount: Long = 0,
    @Column("current_challenge")
    var challenge: String? = null
)