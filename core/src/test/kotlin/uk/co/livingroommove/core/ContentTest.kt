package uk.co.livingroommove.core

import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class ContentTest {
    private val approved = Workout("fixture", "Test fixture", "Synthetic test data only", 5, "Beginner",
        Posture.SEATED, "chair", placeholder = false,
        review = Review(ReviewStatus.APPROVED, "2026-01-01", "Test fixture", "Test credential", "fixture-only"),
        videoUri = "asset:///test.mp4", movements = listOf(Movement("m", "Test", 300, "Test")),
        safetyPrompts = listOf("Test"), licence = Licence("fixture-only", true),
        voices = listOf(VoiceAsset("en-GB", "asset:///test.wav", "asset:///test.vtt", humanRecorded = true,
            approved = true, licenceReference = "fixture-only")))
    @Test fun draftNeverPlaysEvenWithMedia() {
        assertEquals("professional_review_required", PublicationGate.reason(approved.copy(placeholder = true)))
        assertEquals("professional_review_required", PublicationGate.reason(approved.copy(review = Review())))
    }
    @Test fun incompleteEvidenceFailsClosed() {
        assertEquals("review_evidence_missing", PublicationGate.reason(approved.copy(review = approved.review.copy(evidenceId = null))))
        assertEquals("approved_voice_required", PublicationGate.reason(approved.copy(voices = emptyList())))
        assertEquals("video_unavailable", PublicationGate.reason(approved.copy(videoUri = "http://unsafe")))
    }
    @Test fun expiredAndMalformedLicencesFailClosed() {
        for (value in listOf("2020-01-01T00:00:00Z", "bad"))
            assertEquals("licence_expired", PublicationGate.reason(approved.copy(licence = Licence("x", false, value))))
    }
    @Test fun narrationSelectsCompleteLicensedAsset() {
        val incomplete = approved.voices.first().copy(audioUri = null)
        val media = approved.copy(voices = listOf(incomplete) + approved.voices)
        assertNull(PublicationGate.reason(media))
        assertEquals("asset:///test.wav", approvedVoice(media)?.audioUri)
        assertNull(approvedVoice(approved.copy(voices = listOf(incomplete))))
    }
    @Test fun completionOnlyForNaturalApprovedPlayback() {
        val state = recordCompletion(LocalState(), approved, true, 1L)
        assertEquals(1, state.progress["fixture"]?.completed)
        assertEquals(2, recordCompletion(state, approved, true, 2L).progress["fixture"]?.completed)
        assertThrows(IllegalArgumentException::class.java) { recordCompletion(state, approved, false, 3L) }
    }
    @Test fun unverifiedAndExpiredEntitlementsAreDenied() {
        val now = Instant.parse("2026-10-12T00:00:00Z")
        assertFalse(Entitlement(true, now.plusSeconds(1), false).permits(now))
        assertFalse(Entitlement(true, now, true).permits(now))
        assertFalse(Entitlement(true, null, true).permits(now))
        assertTrue(Entitlement(true, now.plusSeconds(1), true).permits(now))
    }
    @Test fun catalogueRejectsDuplicatesAndTraversal() {
        assertThrows(IllegalArgumentException::class.java) { Catalogue(1, 1, workouts = listOf(approved, approved), programmes = emptyList()).validate() }
        assertFalse(safeMediaUri("asset:///../secret"))
        assertThrows(IllegalArgumentException::class.java) { Catalogue(2, 1, workouts = listOf(approved), programmes = emptyList()).validate() }
    }
}
