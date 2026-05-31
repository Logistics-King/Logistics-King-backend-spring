package logisticsking.com.logisticskingbackendspring.domain.common

import java.util.UUID

interface IdGenerator {
    fun generate(): UUID
}
