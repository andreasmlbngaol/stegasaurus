# Stegasaurus

## Table of Contents

* [Features](#features)
* [Installation](#installation)
* [Usage Flow](#usage-flow)
* [Technical Overview](#technical-overview)
* [Platform Notes](#platform-notes)
* [Limitations & Known Issues](#limitations--known-issues)
* [Build Instructions (Developers)](#build-instructions-developers)

Stegasaurus is a Kotlin Multiplatform and Compose Multiplatform application designed to demonstrate practical steganography combined with hybrid cryptography. It supports the following platforms:

* **Android**
* **Desktop** (Linux, Ubuntu)
* **Web** (alpha; unstable and experimental)

The project embeds encrypted messages inside images, producing a **PNG file** as output.

## Cryptography

Stegasaurus uses the following algorithms:

* **X25519** — key agreement
* **HKDF** — key derivation
* **ChaCha20-Poly1305** — authenticated encryption

On Android and Desktop, the app uses **Java Bouncy Castle**. The Web version uses **stablelib**.

---

## Features

### 1. **Encrypt & Embed**

Encrypt a text message using hybrid cryptography and hide it inside an image.

### 2. **Extract & Decrypt**

Extract an embedded ciphertext from an image and decrypt it using the shared key.

---

## Installation

Download installers from the Releases page:

[**Releases**](https://github.com/andreasmlbngaol/stegasaurus/releases)

Available builds:

* `.apk` — Android
* `.deb` — Linux/Ubuntu Desktop
* `.msi` — Windows Desktop

**Web version:**
[**Stegasaurus**](`https://stegasaurus.pages.dev/`)

---

## Usage Flow

### 1. **First Launch: Generate Keys**

When Stegasaurus is opened for the first time, users land on the **Home Screen**. All features remain locked until the user generates a **public/private key pair**. Key generation occurs **only once**, and keys are stored in the platform's local storage. Users can only view their **public key**; the private key is never exposed.

### 2. **Encryption + Embedding Flow (Sender)**

1. Obtain the recipient's **public key** (available via the Home Screen's FAB: *"View Public Key"*).
2. Enter the text message you want to embed.
3. The app displays a hint with the exact message length; this is important and must be shared with the recipient.
4. Choose an image to act as the steganography carrier.
5. Tap **Encrypt**.
6. The app outputs a **PNG** image containing the encrypted message.

The sender must send **two things** to the recipient:

* The resulting **PNG file**
* The **message length**

### 3. **Extraction + Decryption Flow (Receiver)**

1. Obtain the sender's **public key** (same menu as above).
2. Enter the **message length** provided by the sender.
3. Upload the PNG file with the embedded message.
4. Tap **Decrypt**.

If the shared key and the provided message length match, the app reveals the original plaintext.

---

## Project Structure

Built using **Kotlin Multiplatform** with **Compose Multiplatform** UI. Cryptographic logic is shared across platforms, while each target uses its own cryptography provider.

---

## Technical Overview

* **X25519** is used to derive a `sharedSecret` by combining the user's private key with the other user's public key.
* **HKDF** derives the `encryptionKey` and `nonce` from the `sharedSecret`.
* **ChaCha20-Poly1305** encrypts the message using the derived key and nonce.
* **Steganography** uses **LSB (Least Significant Bit)** embedding.

---

## Platform Notes

* **Desktop** is the most stable and optimal distribution. Its PNG encoding performance is significantly better than Android.
* **Web** is far behind both Android and Desktop in stability and performance.

---

## Limitations & Known Issues

* The Web version has bugs in shared secret computation due to interoperability issues between Kotlin and JS.
* Desktop encoding is significantly faster due to the mature ecosystem and better default PNG encoders compared to Android, and much faster than Web.
* Cross-platform compatibility works reliably for: desktop→desktop, desktop→android, android→android, android→desktop. Web remains unreliable.

---

## Build Instructions (Developers)

### Desktop

* **Linux:** `./gradlew :composeApp:run`
  Ensure `gradlew` has execute permission (`chmod +x gradlew`).
* **Windows:** `.\gradlew :composeApp:run`

### Web

* **Linux:** `./gradlew :composeApp:jsBrowserDevelopmentRun`
* **Windows:** `.\gradlew :composeApp:jsBrowserDevelopmentRun`

### Android

* Same process as native Android development: press the **Run**/Play button in Android Studio or IntelliJ IDEA.

## Notes

* Web version is experimental and may contain bugs.
* Only image-based steganography is supported.
* Output images are always encoded as PNG.

---
