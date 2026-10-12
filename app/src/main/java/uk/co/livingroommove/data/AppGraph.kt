package uk.co.livingroommove.data

import android.content.Context
import uk.co.livingroommove.core.*
import uk.co.livingroommove.billing.AmazonBilling

/** Constructor injection; no global service locator or Google services. */
class AppGraph(context: Context) {
    val measurement: Measurement = NoMeasurement
    val local = LocalRepository(context.applicationContext)
    val catalogue: Catalogue = context.assets.open("catalogue-v1.json").bufferedReader().use {
        catalogueJson.decodeFromString<Catalogue>(it.readText()).also(Catalogue::validate)
    }
    val billing = AmazonBilling(context.applicationContext)
}
