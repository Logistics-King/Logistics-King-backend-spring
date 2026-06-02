package logisticsking.com.logisticskingbackendspring.domain.vendor

enum class ProductCategory {
    // 의류: 옷, 신발, 패션 잡화 등 일반 의류성 품목
    CLOTHING,

    // 일반 잡화: 생활용품, 문구, 소형 비식품 잡화
    GENERAL_GOODS,

    // 식품: 상온 식품, 반찬, 과일 등 변질 가능성이 있는 품목
    FOOD,

    // 전자제품: 소형 가전, 컴퓨터 주변기기 등 충격 주의 품목
    ELECTRONICS,

    // 서류: 문서, 책자, 계약서 등 종이류 품목
    DOCUMENT,

    // 화장품: 화장품, 뷰티 제품, 일부 액체성 품목
    COSMETIC,

    // 기타: 위 카테고리로 분류하기 어려운 품목
    ETC,
}
