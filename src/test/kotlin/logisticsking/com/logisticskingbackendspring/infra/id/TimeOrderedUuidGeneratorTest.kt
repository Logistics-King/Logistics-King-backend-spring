package logisticsking.com.logisticskingbackendspring.infra.id

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.security.SecureRandom
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

class TimeOrderedUuidGeneratorTest {

    @Test
    fun `UUIDv7 형식으로 ID를 생성한다`() {
        val generator = TimeOrderedUuidGenerator(
            clock = Clock.fixed(Instant.ofEpochMilli(FIXED_TIMESTAMP_MILLIS), ZoneId.of("UTC")),
            random = SecureRandom(),
        )

        val id = generator.generate()

        assertEquals(7, id.version())
        assertEquals(2, id.variant())
        assertEquals(FIXED_TIMESTAMP_MILLIS, id.mostSignificantBits ushr 16)
    }

    @Test
    fun `같은 millisecond에서 생성된 ID도 생성 순서대로 정렬된다`() {
        val generator = TimeOrderedUuidGenerator(
            clock = Clock.fixed(Instant.ofEpochMilli(FIXED_TIMESTAMP_MILLIS), ZoneId.of("UTC")),
            random = SecureRandom(),
        )

        val ids = (1..10).map { generator.generate() }

        assertEquals(ids, ids.sorted())
    }

    private companion object {
        private const val FIXED_TIMESTAMP_MILLIS = 1_700_000_000_000L
    }
}
