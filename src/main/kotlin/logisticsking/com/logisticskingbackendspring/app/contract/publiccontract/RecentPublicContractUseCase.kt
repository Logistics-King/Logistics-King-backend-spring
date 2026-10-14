package logisticsking.com.logisticskingbackendspring.app.contract.publiccontract

interface GetRecentPublicContractsUseCase {
    fun getRecentPublicContracts(region: String?): List<RecentPublicContractResult>
}
