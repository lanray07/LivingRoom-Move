# Measurement plan

Default: no analytics uploader, SDK or persistent event tracking. `Measurement` is a typed no-payload event sink, and `NoMeasurement` discards events. Application opens, safety acknowledgement, approved playback start/completion/repeat and programme views call this sink. Progress is the user-visible local state. Commerce events must be connected only after receipt verification is implemented. No name, diagnosis, precise workout identity or raw receipt may be added to event properties. Obtain consent before any off-device analytics where required. Explain storage/retention and deletion first.

Event contract: app_opened, onboarding_completed, session_started, session_completed, repeat_session, programme_viewed, purchase_initiated, purchase_completed, entitlement_restored. Commerce success events fire only after verified entitlement; playback complete only on natural end. Do not emit completion for drafts/technical tests.

Activation: proportion reaching the first reviewed free-session start. Completion: natural ends / legitimate starts. Retention: anonymous consenting cohorts returning in week 1/4, with no streak pressure. Conversion: verified purchase / programme views. Cancellation: verified backend subscription changes, distinguishing auto-renew cancellation from lost current access. Analyse aggregate counts only; exercise choice and health profiling are excluded.

Use aggregated, consented measurements to improve readable navigation and reliability. Never use retention targets to add guilt, enforced streaks or intrusive prompts. Events are a defined integration contract; external analytics has not been configured or enabled.
