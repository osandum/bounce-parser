# bounce-parser – E-mail Bounce Parser

A Java library for parsing bounced e-mail delivery notifications. Given a `javax.mail.Message`, it returns a structured `MailDeliveryStatus` describing what happened and to whom.

## Usage

```java
MailDeliveryStatus status = BounceParser.parseMessage(message);

MailDeliveryAction action = status.getDeliveryAction(); // failed, delayed, delivered, …
MailSystemStatusCode code = status.getDeliveryStatus(); // e.g. 5.2.2
InternetAddress recipient = status.getRecipient();      // original recipient preferred

if (action.isFailed() && code.isPermanent()) {
    // hard bounce — consider disabling the address
}
```

`getRecipient()` returns `originalRecipient` if present, falling back to `finalRecipient` and then the `To` header of the original message. When `originalRecipient` and `finalRecipient` are both set and differ, the message was forwarded before bouncing — the bounce is at the forwarding destination, not at your original recipient.

## What it handles

| Format | Detection |
|--------|-----------|
| RFC 3464 DSN (`message/delivery-status`) | Standard; parses `Action`, `Status`, `Final-Recipient`, `Original-Recipient`, `Diagnostic-Code` |
| RFC 3798 MDN (`message/disposition-notification`) | Automated-deletion dispositions treated as failures |
| qmail plain-text bounce | Regex on `text/plain` body |
| Exim plain-text bounce | Regex on `text/plain` body |
| t-online plain-text bounce | Regex on `text/plain` body |
| Quota exceeded without status code | Detected from `Diagnostic-Code` text |

## Delivery actions

`MailDeliveryAction`: `failed`, `failure`, `delayed`, `delivered`, `relayed`, `expanded`, `unknown`

`isFailed()` returns true for both `failed` and `failure`.

## Status codes

`MailSystemStatusCode` encapsulates an [RFC 3463](http://rfc.net/rfc3463.html) `class.subject.detail` triple.

- `isPermanent()` — class 5, hard bounce
- `isTransient()` — class 4, soft bounce / retry
- `isSuccess()` — class 2

## Maven

```xml
<dependency>
    <groupId>dk.sandum</groupId>
    <artifactId>bounce-parser</artifactId>
    <version>0.8.8</version>
</dependency>
```

## Prerequisites

- Java 7 or later (compiled with `-source 1.7 -target 1.7`)
- Maven 3
- An SLF4J binding on the runtime classpath (the library depends on `slf4j-api` but does not prescribe an implementation)

## Building

```
mvn package
mvn test
```

Place additional `.eml` sample files in a `samples/` directory at the project root to have them exercised automatically by the test suite.
