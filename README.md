# SolidGate API


[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.solidgate/solidgate-sdk/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.solidgate/solidgate-sdk)

This library provides basic API options of SolidGate payment gateway.

## Usage

Card-gate example

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
        
        val response = api.charge(attributes)

```



