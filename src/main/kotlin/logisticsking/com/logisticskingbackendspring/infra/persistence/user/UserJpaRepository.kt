package logisticsking.com.logisticskingbackendspring.infra.persistence.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.UUID

interface UserJpaRepository : JpaRepository<UserJpaEntity, UUID> {
    fun findByIdAndDeletedAtIsNull(id: UUID): UserJpaEntity?
    fun findByLoginIdAndDeletedAtIsNull(loginId: String): UserJpaEntity?
    fun findByNameAndEmailAndDeletedAtIsNull(
        name: String,
        email: String,
    ): UserJpaEntity?

    fun findByLoginIdAndEmailAndDeletedAtIsNull(
        loginId: String,
        email: String,
    ): UserJpaEntity?

    fun existsByLoginId(loginId: String): Boolean
    fun existsByEmail(email: String): Boolean

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
        """
        update UserJpaEntity u
        set u.encodedPassword = :encodedPassword
        where u.id = :id
          and u.deletedAt is null
        """
    )
    fun updatePassword(
        @Param("id") id: UUID,
        @Param("encodedPassword") encodedPassword: String,
    ): Int
}
