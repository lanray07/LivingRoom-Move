# Localisation — human review required

English UK and US Android string resources are installed. Runtime locale selection works without a network. Catalogue language remains English UK as explicit fallback. Android handles resource formatting/layout direction; no RTL locale is claimed as tested.

French, German, Spanish, Italian, Portuguese and Dutch have review queues under `drafts/`. These are **not completed translations**. Six store-metadata queues also exist. Unsupported/unapproved languages deliberately fall back to English, never to unreviewed exercise safety text.

`tools/translate_drafts.py` is developer-only and requires a provider implementing the documented JSON HTTP contract. Set `TRANSLATION_ENDPOINT` and `TRANSLATION_API_KEY` securely in your local environment, then run `python tools/translate_drafts.py fr`. Keys are never passed in command arguments or embedded in the app. Drafts stay outside `res/`; translation memory keys include the source/glossary/locale digest and validates formatting tokens. No provider was configured or called during implementation.

Before promotion: a fluent human translator and appropriate professional must review safety, movement, captions/narration, purchase terminology and locale-specific disclosures. Record reviewer, date, source digest, correction history and approved version. Add only reviewed files to Android `values-xx`, test truncation/font scaling and offline selection on-device, then expose that language in settings. Use plurals for language forms where counts need them. Use locale-aware number/currency formatting, and Amazon localised product pricing for subscriptions. Listing/graphics review is separate from in-app review.

Future catalogue/caption/script translation should use the same draft envelope and per-media review/licensing requirements; the supplied pipeline currently translates UI strings only. Do not silently run it on exercise instructions as a publishing step.
