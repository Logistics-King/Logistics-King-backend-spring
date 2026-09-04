package logisticsking.com.logisticskingbackendspring.infra.async

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

@EnableAsync
@Configuration
class AsyncConfig {

    // AccessLog 저장은 원래 API 응답 흐름과 분리되어야 한다.
    // 이 executor는 @Async("accessLogTaskExecutor")에서 사용하며, access log DB 저장 작업만 처리한다.
    @Bean
    fun accessLogTaskExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            // 평소에는 2개 스레드로 access log 저장 작업을 처리한다.
            corePoolSize = 2

            // 큐가 밀리면 최대 4개 스레드까지 늘려 처리한다.
            maxPoolSize = 4

            // core thread가 모두 작업 중이면 최대 1000개까지 대기열에 쌓는다.
            queueCapacity = 1_000

            // 로그에서 access log 비동기 작업 스레드를 구분하기 위한 이름 prefix다.
            setThreadNamePrefix("access-log-")

            initialize()
        }
    }
}
