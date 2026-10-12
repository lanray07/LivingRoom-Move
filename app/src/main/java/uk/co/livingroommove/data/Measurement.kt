package uk.co.livingroommove.data

enum class MoveEvent {
    APP_OPENED, ONBOARDING_COMPLETED, SESSION_STARTED, SESSION_COMPLETED,
    REPEAT_SESSION, PROGRAMME_VIEWED, PURCHASE_INITIATED, PURCHASE_COMPLETED, ENTITLEMENT_RESTORED
}
/** No payloads: never expose session IDs, diagnoses, names or receipts to analytics. */
fun interface Measurement { fun event(event: MoveEvent) }
object NoMeasurement : Measurement { override fun event(event: MoveEvent) = Unit }
