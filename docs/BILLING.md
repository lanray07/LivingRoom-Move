# Amazon purchasing — integration blocked

The compiled dependency is Amazon Appstore SDK 3.0.9. Receiver registration and PurchasingListener callbacks are present. `AmazonBilling` guards every purchase/restore call; `verifierConfigured=false` intentionally prevents transactions. Do not flip the flag merely to enable the buttons.

Needed before production:

1. Owner confirms package identity, developer account, banking/tax setup and actual subscription product IDs/periods in Amazon console.
2. Obtain **public** `AppstoreAuthenticationKey.pem` from that app's console and put it in assets. Never include the RVS shared secret in the APK.
3. Implement an authenticated, abuse-resistant HTTPS verification backend. Verify receiptId/userId via RVS; check SKU, product type, marketplace, cancellation/refund state and subscription validity/renewal status. Bind grants to the purchasing Amazon user and deduplicate replayed receipts. No custom app account is needed for free sessions.
4. Persist a backend-signed short-lived entitlement, verified offline against a pinned key. Expired/unknown/unverified state denies premium. Define cancellation grace/access-until-period-end from Amazon receipt semantics, not a simplistic cancelled boolean. Clear grants on Amazon user changes; validate device clock risk and offline policy.
5. Fetch localised price/title from `getProductData`; only offer matching supported products. Proposed UK test prices £4.99 monthly / £29.99 annually require console price configuration and validation. Never turn these planning figures into checkout strings.
6. Purchase callbacks must reach RVS before `notifyFulfillment(FULFILLED)`. Restore starts with `getPurchaseUpdates(true)`, processes each page, verifies receipts, reconciles revoked/expired grants and follows hasMore with reset=false. Retain request IDs and correlate responses; callbacks must not override an unrelated in-flight request.
7. Replace unavailable UI with clear dynamic prices, billing periods, renewal/cancellation disclosure, verified purchase/restore results and tested Amazon subscription-management route. Never use a guessed management URL.
8. Use Amazon App Tester and then Live App Testing with real SKUs: purchase, pending/network timeout, cancellation, renewal, refund, duplicate callback, unavailable SKU, pagination, restore on reinstall/new device, account change and backend outage. No entitlement is granted on transport success alone.

No sandbox purchase or restoration has been completed. No live products have been created, and no console changes have been made. The SDK may emit D8/R8 stack-map warnings; release build and hardware testing must remain part of acceptance.
