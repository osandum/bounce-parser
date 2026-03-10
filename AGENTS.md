# AGENTS.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test

```bash
mvn compile          # compile
mvn test             # run all tests
mvn test -Dtest=DsnParserTest         # run one test class
mvn test -Dtest=DsnParserTest#testQuotaExceededResponse  # run single test method
mvn package          # build jar
```

Tests also scan a local `samples/` directory (not in source control) for `.eml` files via `testLocalSamples()`.

## Architecture

This is a single-module Maven library (`dk.sandum:bounce-parser`) for parsing bounced/failed email delivery notifications.

**Entry point:** `BounceParser.parseMessage(Message)` — takes a `javax.mail.Message`, validates it is a bounce (Return-Path must be `<>`), then walks the MIME tree to populate a `MailDeliveryStatus`.

**MIME part dispatch in `BounceParser.parsePart()`:**
- `multipart/*` — recurse into body parts
- `text/plain` — try `QmailFailure.tryParse()`, then `EximFailure.tryParse()` (both regex-based)
- `message/delivery-status` — standard RFC 3464 DSN; parses `Action`, `Status`, `Final-Recipient`, `Reporting-MTA`, `Diagnostic-Code` headers; detects "quota exceeded" text when status code is absent
- `message/disposition-notification` — RFC 3798 MDN; automated-deletion dispositions are treated as failures
- `message/rfc822` / `text/rfc822` — extracts original message headers into `MailDeliveryStatus`

**Key model classes:**
- `MailDeliveryStatus` — result object: recipient, action, status code, reporting agent, original message headers, plain text part
- `MailDeliveryAction` — enum: `failed`, `failure`, `delayed`, `delivered`, `relayed`, `expanded`, `unknown`
- `MailSystemStatusCode` — RFC 3463 status code (`class.subject.detail`); class 2=success, 4=transient, 5=permanent
- `QmailFailure` / `EximFailure` — regex parsers for non-standard plaintext bounces from qmail and Exim respectively
- `MailDisposition` — MDN disposition parser

**Test fixtures** (`.eml` files) live in `src/test/resources/`.
