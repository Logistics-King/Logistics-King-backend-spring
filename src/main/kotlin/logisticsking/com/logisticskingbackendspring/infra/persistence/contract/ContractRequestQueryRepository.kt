package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ContractRequestQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    private val contractRequest = QContractRequestJpaEntity.contractRequestJpaEntity

    fun findByIdForUpdate(id: UUID): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(contractRequest.id.eq(id))
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findByIdAndVendorIdForUpdate(
        id: UUID,
        vendorId: UUID,
    ): ContractRequestJpaEntity? {
        return queryFactory
            .selectFrom(contractRequest)
            .where(
                contractRequest.id.eq(id),
                contractRequest.vendorId.eq(vendorId),
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }
}
