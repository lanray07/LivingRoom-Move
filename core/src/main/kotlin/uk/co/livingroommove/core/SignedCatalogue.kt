package uk.co.livingroommove.core

import java.security.PublicKey
import java.security.Signature
import java.time.Instant

/** Verify the exact bytes before parsing. Pin the publisher's public key; private keys stay off-device.
 * Keep the previous verified catalogue for explicit rollback, never accept an older network version. */
class SignedCatalogue(private val publisherKey: PublicKey) {
    fun verify(bytes: ByteArray, signature: ByteArray, installedVersion: Long, now: Instant): Catalogue {
        require(bytes.size <= 1024 * 1024) { "Catalogue exceeds limit" }
        require(Signature.getInstance("SHA256withECDSA").run {
            initVerify(publisherKey); update(bytes); verify(signature)
        }) { "Invalid publisher signature" }
        val catalogue = catalogueJson.decodeFromString<Catalogue>(bytes.toString(Charsets.UTF_8))
        catalogue.validate()
        require(catalogue.version > installedVersion) { "Network downgrade rejected" }
        require(catalogue.expiresAt != null && Instant.parse(catalogue.expiresAt).isAfter(now)) { "Catalogue expired" }
        return catalogue
    }
}
