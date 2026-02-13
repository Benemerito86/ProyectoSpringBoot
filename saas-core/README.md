erDiagram
USER ||--o{ SUBSCRIPTION : has
USER ||--o{ PAYMENT_METHOD : holds
PLAN ||--o{ SUBSCRIPTION : defines
SUBSCRIPTION ||--o{ INVOICE : generates

    SUBSCRIPTION {
        Long id
        LocalDate startDate
        Enum status
        History audited_by_envers
    }
    PAYMENT_METHOD {
        String type
        String discriminator
    }
