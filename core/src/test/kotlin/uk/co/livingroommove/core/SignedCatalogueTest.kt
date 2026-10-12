package uk.co.livingroommove.core

import org.junit.Assert.*
import org.junit.Test
import java.security.KeyPairGenerator
import java.security.Signature
import java.time.Instant

class SignedCatalogueTest {
    private val pair = KeyPairGenerator.getInstance("EC").apply { initialize(256) }.generateKeyPair()
    private val bytes = """{"schemaVersion":1,"version":2,"expiresAt":"2027-01-01T00:00:00Z","workouts":[],"programmes":[]}""".toByteArray()
    private fun sign(data: ByteArray) = Signature.getInstance("SHA256withECDSA").run { initSign(pair.private); update(data); sign() }
    private val now = Instant.parse("2026-10-12T00:00:00Z")
    @Test fun authenticCatalogueAccepted() { assertEquals(2L, SignedCatalogue(pair.public).verify(bytes, sign(bytes), 1, now).version) }
    @Test fun tamperedCatalogueRejected() { assertThrows(IllegalArgumentException::class.java) { SignedCatalogue(pair.public).verify(bytes + ' '.code.toByte(), sign(bytes), 1, now) } }
    @Test fun downgradeAndExpiryRejected() {
        assertThrows(IllegalArgumentException::class.java) { SignedCatalogue(pair.public).verify(bytes, sign(bytes), 2, now) }
        assertThrows(IllegalArgumentException::class.java) { SignedCatalogue(pair.public).verify(bytes, sign(bytes), 1, Instant.parse("2028-01-01T00:00:00Z")) }
    }
}
