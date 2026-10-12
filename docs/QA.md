# Manual acceptance plan

Record tester, date, model/build, Fire OS/API, TV size/viewing distance, remote model, network and exact result for each case. No physical Fire TV testing has been completed.

| Area | Cases | Pass condition |
|---|---|---|
| Remote | Every screen via D-pad/Select; long lists; Back; return to selected item; media keys | Visible focus, no traps, predictable return, no touchscreen |
| Accessibility | 100/115/130% text plus OS scaling; VoiceView; captions; 2–3 m reading | No clipped text; announced button purpose; readable contrast |
| Family | Decline/accept; Back; local changes; no remote access | Consent before setup; no covert monitoring |
| Playback | Reviewed silent MP4/audio/VTT; pause/resume/replay; Home interruption; audio focus loss | Synchronised tracks; pauses in background; no duplicate completion |
| Recovery | Resume after process kill/reboot; network loss; decoder error; retry | Saved place; no false completion; clear error |
| Offline | Licensed bundled asset with network disabled; rights expiry; missing media | Correct playback or honest unavailable state |
| Privacy | Delete data; relaunch; uninstall; logs; disabled analytics | Data removed; no health identifiers/secrets/uploads |
| Locales | UK/US switch; missing locale; draft locale; RTL harness | Offline fallback; no unreviewed text shown; no truncation |
| Commerce | App Tester/live purchase/restore, refunds, renewal, account change, duplicates, pagination | RVS first; correct dynamic pricing/grants; fail closed |
| Performance | Cold start; 1GB RAM; repeated playback; 30-minute run | No ANRs/leaks; stable decoder use; no background playback |
| Content | Every review/evidence/licence; caption/narration language; safe chair prompts | Complete external approval records |

Automated domain tests cover publication/signature/entitlement gates and completion. The Android JVM rendered journey verifies Select, focus restoration, draft playback blocking and family consent and captures five actual screens. Instrumentation tests for device Select/Back and family consent compile but could not execute locally because no emulator/hardware connected. Expand playback integration tests with real licensed reviewed media when available. JVM/emulator tests cannot validate Fire OS VoiceView, Amazon SDK commerce, hardware decoders or living-room reading comfort.
