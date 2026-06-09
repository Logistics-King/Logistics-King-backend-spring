package logisticsking.com.logisticskingbackendspring.app.common

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

@Schema(description = "공통 페이지 응답")
data class PageResponse<T : Any>(
    @field:Schema(description = "현재 페이지 데이터")
    val items: List<T>,
    @field:Schema(description = "현재 페이지 번호. 0부터 시작합니다.", example = "0")
    val page: Int,
    @field:Schema(description = "페이지 크기", example = "20")
    val size: Int,
    @field:Schema(description = "전체 데이터 수", example = "128")
    val totalElements: Long,
    @field:Schema(description = "전체 페이지 수", example = "7")
    val totalPages: Int,
    @field:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean,
    @field:Schema(description = "이전 페이지 존재 여부", example = "false")
    val hasPrevious: Boolean,
) {
    companion object {
        fun <T : Any, R : Any> from(
            page: Page<T>,
            mapper: (T) -> R,
        ): PageResponse<R> {
            return PageResponse(
                items = page.content.map(mapper),
                page = page.number,
                size = page.size,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious(),
            )
        }
    }
}
