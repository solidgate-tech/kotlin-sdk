# Solidgate API

[![MvnRepository](https://badges.mvnrepository.com/badge/com.solidgate/solidgate-api-sdk/badge.svg?label=MvnRepository)](https://mvnrepository.com/artifact/com.solidgate/solidgate-api-sdk)

Kotlin SDK provides API options for integrating Solidgate’s payment orchestrator into your Kotlin applications.

Check our
* <a href="https://docs.solidgate.com/" target="_blank">Payment guide</a> to understand business value better
* <a href="https://api-docs.solidgate.com/" target="_blank">API Reference</a> to find more examples of usage

## Structure

<table style="width: 100%; background: transparent;">
  <colgroup>
    <col style="width: 50%;">
    <col style="width: 50%;">
  </colgroup>
  <tr>
    <th>SDK for Kotlin contains</th>
    <th>Table of contents</th>
  </tr>
  <tr>
    <td>
      <code>src/</code> – main library source code for development<br>
      <code>build.gradle.kts</code> – script for managing dependencies and library imports<br>
      <code>gradle.properties</code> – configuration file for Gradle
    </td>
    <td>
      <a href="https://github.com/solidgate-tech/kotlin-sdk?tab=readme-ov-file#requirements">Requirements</a><br>
      <a href="https://github.com/solidgate-tech/kotlin-sdk?tab=readme-ov-file#installation">Installation</a><br>
      <a href="https://github.com/solidgate-tech/kotlin-sdk?tab=readme-ov-file#usage">Usage</a><br>
      <a href="https://github.com/solidgate-tech/kotlin-sdk?tab=readme-ov-file#errors">Errors</a>
    </td>
  </tr>
</table>

## Requirements

* **Kotlin**: 1.4 or later
* **Gradle** or **Maven**: as a build tool
* **Solidgate account**: Public and secret key (request via <a href="mailto:sales@solidgate.com">sales@solidgate.com</a>)

<br>

## Installation

To start using the Kotlin SDK:

1. **Add the SDK** to your project according to your build tool. <br>

   **Maven**
   ```xml
   <dependency>
       <groupId>com.solidgate</groupId>
       <artifactId>solidgate-api-sdk</artifactId>
       <version>0.6.0</version>
   </dependency>
   ```

   **Gradle (Kotlin DSL)**
   ```kotlin
   implementation("com.solidgate:solidgate-api-sdk:0.6.0")
   ```

   **Gradle (Groovy DSL)**
   ```groovy
   implementation 'com.solidgate:solidgate-api-sdk:0.6.0'
   ```
2. Initialize the SDK with your **public** and **secret key**.
3. Use test credentials for validation, then transition to production credentials for deployment.

_Use the provided Dockerfile for running the SDK locally._

<br>

## Usage

### Charge a payment

```kotlin
val api = Api(HttpClient(), Credentials("testMerchant", "private0-test-test-test-key123456789"))

val attributes = Attributes(mapOf(
    "amount" to 123,
    "currency" to "USD",
    "customer_email" to "testuser@example.com",
    "ip_address" to "8.8.8.8",
    "order_description" to "Test subscription",
    "order_id" to "order12345",
    "platform" to "WEB",
    "card_cvv" to "XXX",
    "card_exp_month" to 12,
    "card_exp_year" to 24,
    "card_number" to "4111 11XX XXXX 1111"
))

val response = api.auth(attributes)
```

<br>

## Errors

Handle <a href="https://docs.solidgate.com/payments/payments-insights/error-codes/" target="_blank">errors</a>.

```kotlin
try {
	val response = api.auth(attributes)
} catch (e: Exception) {
	println(e.message)
}
```

---

Looking for help? <a href="https://support.solidgate.com/support/tickets/new" target="_blank">Contact us</a> <br>
Want to contribute? <a href="https://github.com/solidgate-tech/kotlin-sdk/pulls" target="_blank">Submit a pull request</a>