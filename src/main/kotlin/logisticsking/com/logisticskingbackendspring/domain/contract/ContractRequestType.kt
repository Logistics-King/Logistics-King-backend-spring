package logisticsking.com.logisticskingbackendspring.domain.contract

enum class ContractRequestType(
    val requesterType: ContractPartyType,

    val approverType: ContractPartyType,
) {
    VENDOR_OFFER(
        requesterType = ContractPartyType.VENDOR,
        approverType = ContractPartyType.AGENCY,
    ),
    AGENCY_OFFER(
        requesterType = ContractPartyType.AGENCY,
        approverType = ContractPartyType.VENDOR,
    ),
}
