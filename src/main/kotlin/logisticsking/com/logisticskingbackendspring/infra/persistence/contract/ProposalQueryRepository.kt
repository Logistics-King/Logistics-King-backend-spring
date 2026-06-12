package logisticsking.com.logisticskingbackendspring.infra.persistence.contract

import com.querydsl.jpa.impl.JPAQueryFactory
import jakarta.persistence.LockModeType
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ProposalQueryRepository(
    private val queryFactory: JPAQueryFactory,
) {
    private val proposal = QProposalJpaEntity.proposalJpaEntity

    fun findByIdAndAgencyIdForUpdate(
        id: UUID,
        agencyId: UUID,
    ): ProposalJpaEntity? {
        return queryFactory
            .selectFrom(proposal)
            .where(
                proposal.id.eq(id),
                proposal.agencyId.eq(agencyId),
            )
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetchOne()
    }

    fun findAllByContractRequestIdForUpdate(contractRequestId: UUID): List<ProposalJpaEntity> {
        return queryFactory
            .selectFrom(proposal)
            .where(proposal.contractRequestId.eq(contractRequestId))
            .orderBy(proposal.createdAt.desc())
            .setLockMode(LockModeType.PESSIMISTIC_WRITE)
            .fetch()
    }
}
