package ru.aasmc.webauthndemo.db.repoistory

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import ru.aasmc.webauthndemo.db.entity.CredentialEntity
import java.util.Optional

interface CredentialEntityRepository: JpaRepository<CredentialEntity, Long> {

    fun findByCredentialId(credentialId: String): Optional<CredentialEntity>

    @Modifying
    @Transactional
    @Query("""
        update credential set sign_count = :counterValue where credential_id = :credentialId
    """, nativeQuery = true)
    fun updateCounter(@Param("credentialId") credentialId: String,
                      @Param("counterValue") counterValue: Long): Int

}