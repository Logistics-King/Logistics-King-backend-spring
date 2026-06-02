package logisticsking.com.logisticskingbackendspring.infra.persistence.permission

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import logisticsking.com.logisticskingbackendspring.domain.user.UserRole

@Converter
class UserRoleSetConverter : AttributeConverter<Set<UserRole>, String> {

    override fun convertToDatabaseColumn(attribute: Set<UserRole>?): String {
        val roles = attribute.orEmpty()
            .sortedBy { it.ordinal }
            .joinToString(separator = ",") { role -> "\"${role.name}\"" }

        return "[$roles]"
    }

    override fun convertToEntityAttribute(dbData: String?): Set<UserRole> {
        if (dbData.isNullOrBlank() || dbData.trim() == "null") {
            return emptySet()
        }

        return dbData
            .trim()
            .removePrefix("[")
            .removeSuffix("]")
            .split(",")
            .map { role -> role.trim().removeSurrounding("\"") }
            .filter { role -> role.isNotBlank() }
            .map(UserRole::valueOf)
            .toSet()
    }
}
