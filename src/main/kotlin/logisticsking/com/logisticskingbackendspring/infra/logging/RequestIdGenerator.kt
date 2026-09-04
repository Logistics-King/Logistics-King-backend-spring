package logisticsking.com.logisticskingbackendspring.infra.logging

import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

@Component
class RequestIdGenerator {
    private val lastMillis = AtomicLong(0)
    private val sequence = AtomicInteger(0)

    fun next(): Long {
        val now = System.currentTimeMillis()
        val previous = lastMillis.getAndUpdate { current ->
            if (now > current) now else current
        }
        val baseMillis = maxOf(now, previous)
        val nextSequence = if (baseMillis == previous) {
            sequence.incrementAndGet() % SEQUENCE_LIMIT
        } else {
            sequence.set(0)
            0
        }

        return baseMillis * SEQUENCE_LIMIT + nextSequence
    }

    private companion object {
        private const val SEQUENCE_LIMIT = 10_000
    }
}
