# Review, media and catalogue publication

All ten current session records remain DRAFT/placeholder. They now contain authored movement plans, proposed safety wording and narrator scripts, with timed review audio/captions and a Remotion production studio under [content](../content/README.md). Reviewer identities and approvals, licensed instructional footage and published human narration remain absent. Review assets do not establish validated exercise instructions.

Use the [free-session production brief](FREE_SESSION_PRODUCTION_BRIEF.md) to commission the first reviewed library and specify the media handover. It is a production specification, not approved exercise content.

For each session obtain a qualified UK exercise professional's scope-appropriate review, named reviewer/credentials, date and retained evidence reference. The professional must review movements, seated alternatives, contraindication notes, timing, safety prompts, footage and voice/caption scripts. A status field alone is not evidence; retain the underlying signed review record outside the app. Verify professional suitability and insurance with the owner.

Obtain filmed demonstration/model releases and media distribution licences covering territories, languages, offline use and expiry. Narration must be human recorded and licensed. Use silent video plus matching audio and VTT tracks of equal duration. At publication, test track selection/alignment, reduced speed, captions, media errors and lifecycle interruption on target Fire TV hardware. Separate recording licences must cover every language asset.

Set `placeholder=false` and `review.status=APPROVED` only after evidence is complete. Run `validate_release.py --release` plus Kotlin gate tests. Bundled assets use `asset:///path.mp4`, `asset:///path.wav`, `asset:///path.vtt`; these play offline without networking. HTTPS is required for remote media. Never ship developer tone media as a workout. If footage is missing, playback stays unavailable; no replacement exercise instructions are improvised.

The versioned JSON schema is represented by `core/Content.kt`. `SignedCatalogue` verifies detached SHA256withECDSA signatures over exact UTF-8 bytes, a pinned publisher key, version/expiry and structural validation. Keep signing private keys in a secure admin service, outside app/source/env logs. Remote update installation is not connected in this preview.

Future secure admin: separate authenticated web service with least-privilege roles, MFA, immutable audit records, explicit professional approval before publish, and publisher signing in a key-management service. Upload licensed media, hash references, enforce byte limits, serve over HTTPS, and support rights revocation. Client caches must validate media digests and rights expiry, use atomic replacement and retain the previous verified snapshot. On rollback, the server should publish a new increasing version containing the prior approved content. Local fallback may use an already verified snapshot only while its licence/metadata remains valid. Define clock manipulation protection for remote expiry before shipping.

Downloading sessions is future work. Bundled offline playback is implemented; there is no download UI or claim of finished offline downloadable programmes.
