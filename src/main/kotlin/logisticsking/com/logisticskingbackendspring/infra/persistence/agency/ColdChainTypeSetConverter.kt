package logisticsking.com.logisticskingbackendspring.infra.persistence.agency

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import logisticsking.com.logisticskingbackendspring.domain.common.ColdChainType

@Converter
class ColdChainTypeSetConverter : AttributeConverter<Set<ColdChainType>, String> {

    override fun convertToDatabaseColumn(attribute: Set<ColdChainType>?): String {
        val types = attribute.orEmpty()
            .sortedBy { it.ordinal }
            .joinToString(separator = ",") { type -> "\"${type.name}\"" }

        return "[$types]"
    }

    override fun convertToEntityAttribute(dbData: String?): Set<ColdChainType> {
        if (dbData.isNullOrBlank() || dbData.trim() == "null") {
            return emptySet()
        }

        return dbData
            .trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { type -> type.trim().removeSurrounding("\"") }
            .filter { type -> type.isNotBlank() }
            .map(ColdChainType::valueOf)
            .toSet()
    }
}
