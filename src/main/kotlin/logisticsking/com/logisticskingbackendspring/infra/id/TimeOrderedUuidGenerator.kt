package logisticsking.com.logisticskingbackendspring.infra.id

import logisticsking.com.logisticskingbackendspring.domain.common.IdGenerator
import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.Clock
import java.util.UUID

@Component
class TimeOrderedUuidGenerator(
    private val clock: Clock = Clock.systemUTC(),
    private val random: SecureRandom = SecureRandom(),
) : IdGenerator {

    private var lastTimeStampMillis: Long = -1
    private var sequence: Int = 0

    @Synchronized
    override fun generate(): UUID {
        var timestampMillis = clock.millis()

        if (timestampMillis < lastTimeStampMillis) {
            timestampMillis = lastTimeStampMillis
        }

        if (timestampMillis == lastTimeStampMillis) {
            if (sequence == MAX_SEQUENCE) {
                timestampMillis = waitNextMillis(lastTimeStampMillis)
                sequence = 0
            } else {
                sequence += 1
            }
        } else {
            sequence = 0
        }

        lastTimeStampMillis = timestampMillis

        val mostSignificantBits = ((timestampMillis and TIMESTAMP_MASK) shl 16) or VERSION_BITS or sequence.toLong()
        val leastSignificantBits = (random.nextLong() and VARIANT_RANDOM_MASK) or RFC_4122_VARIANT_BITS

        return UUID(mostSignificantBits, leastSignificantBits)
    }

    private fun waitNextMillis(currentTimestampMillis: Long): Long {
        var nextTimestampMillis = clock.millis()

        while (nextTimestampMillis <= currentTimestampMillis) {
            Thread.onSpinWait()
            nextTimestampMillis = clock.millis()
        }

        return nextTimestampMillis
    }

    private companion object {
        private const val TIMESTAMP_MASK = 0x0000_FFFF_FFFF_FFFFL
        private const val VERSION_BITS = 0x0000_0000_0000_7000L
        private const val MAX_SEQUENCE = 0x0FFF
        private const val VARIANT_RANDOM_MASK = 0x3FFF_FFFF_FFFF_FFFFL
        private const val RFC_4122_VARIANT_BITS = Long.MIN_VALUE
    }
}
