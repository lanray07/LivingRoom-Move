# Known limitations

1. There is no approved workout, filmed movement, human narration or caption licence. All ten session records now have authored production scripts and timelines but remain drafts and cannot play in the app. The full real-workout completion flow therefore cannot be content-validated yet.
2. Only a debug technical audio clip is playable in the app now. Separate Remotion cue-card review videos and synthetic audio are local production assets, not published lessons or filmed demonstrations. Actual instructional video decoder tests require approved footage.
3. Amazon transport is compiled but commerce is disabled. No authentication PEM, actual products, RVS backend, trusted offline entitlement, live pricing or tested management route exists.
4. No physical Fire TV or Vega testing has been done. MinSdk25 deliberately excludes Fire OS5. Generic Android emulator results are separate evidence.
5. Local preferences/favourites/progress are implemented; cloud sync, remote family monitoring and accounts are intentionally absent. Welcome reminders appear only in the app; there are no scheduled background notifications.
6. UK/US English UI is implemented; six languages are review queues, not completed translations. Catalogue fallback remains English UK. The provider-neutral translation pipeline needs an external API; media/script translation and human approval are still required.
7. Signed metadata validation is implemented/tested; hosted admin, updater, atomic rollback storage, media hashing/download management and clock-tamper controls are not connected. Bundled approved media can play offline; none is supplied.
8. Legal drafts, listing copy and assets need owner/legal/rights review. The AI portrait is fictional brand imagery and never instructional evidence.
9. Screen reader behaviour, OS-level caption preferences, full focus restoration after process death, low-memory decoder performance, audio track selection on all Fire OS generations and safe-area comfort require hardware QA.
10. Analytics uses a no-op typed event sink with no external collector. The OSV runtime inventory query checks indexed Maven advisories only; it excludes unindexed flaws and bundled SDK internals. Build/lint and an advisory query are not a full security audit.
