import * as X25519 from '@stablelib/x25519';
import * as hex from '@stablelib/hex';
import { HKDF } from '@stablelib/hkdf';
import { SHA256 } from '@stablelib/sha256';
import { ChaCha20Poly1305 } from '@stablelib/chacha20poly1305';

export function generateKeyPair() {
    console.log("=== GENERATING KEY PAIR ===");
    try {
        const keyPair = X25519.generateKeyPair();
        console.log("Generated key pair:", keyPair);

        const publicKeyHex = hex.encode(keyPair.publicKey);
        const privateKeyHex = hex.encode(keyPair.secretKey);

        console.log("Public Key (hex):", publicKeyHex);
        console.log("Public Key length:", publicKeyHex.length);
        console.log("Private Key (hex):", privateKeyHex);
        console.log("Private Key length:", privateKeyHex.length);

        return {
            publicKey: publicKeyHex,
            privateKey: privateKeyHex,
        };
    } catch (e) {
        console.error("Error generating key pair:", e);
        throw e;
    }
}

function sharedSecret(myPrivHex, otherPubHex) {
    console.log("=== CALCULATING SHARED SECRET ===");
    try {
        console.log("My Private Key (hex):", myPrivHex);
        console.log("Other Public Key (hex):", otherPubHex);

        const myPriv = hex.decode(myPrivHex);
        console.log("My Private Key (decoded):", myPriv);
        console.log("My Private Key length:", myPriv.length);

        const otherPub = hex.decode(otherPubHex);
        console.log("Other Public Key (decoded):", otherPub);
        console.log("Other Public Key length:", otherPub.length);

        const shared = X25519.sharedKey(myPriv, otherPub);
        console.log("Shared Secret (raw):", shared);
        console.log("Shared Secret (hex):", hex.encode(shared));
        console.log("Shared Secret length:", shared.length);

        return shared;
    } catch (e) {
        console.error("Error calculating shared secret:", e);
        throw e;
    }
}

function deriveKeyAndNonce(sharedSecret, info) {
    console.log("=== DERIVING KEY AND NONCE ===");
    try {
        console.log("Shared Secret (hex):", hex.encode(sharedSecret));
        console.log("Shared Secret length:", sharedSecret.length);
        console.log("Info (raw):", info);
        console.log("Info (hex):", hex.encode(info));
        console.log("Info length:", info.length);

        // Use empty Uint8Array for salt instead of null
        const salt = new Uint8Array(0);
        console.log("Creating HKDF with SHA256, sharedSecret, salt, info");

        const hkdfInstance = new HKDF(SHA256, sharedSecret, salt, info);
        console.log("HKDF instance created:", hkdfInstance);

        const key = hkdfInstance.expand(32);
        console.log("Key (raw):", key);
        console.log("Key (hex):", hex.encode(key));
        console.log("Key length:", key.length);

        const nonce = hkdfInstance.expand(12);
        console.log("Nonce (raw):", nonce);
        console.log("Nonce (hex):", hex.encode(nonce));
        console.log("Nonce length:", nonce.length);

        return { key, nonce };
    } catch (e) {
        console.error("Error deriving key and nonce:", e);
        throw e;
    }
}

export function encrypt(plainMessage, recipientPublicKey, senderPrivateKey) {
    console.log("\n========== ENCRYPT START ==========");
    try {
        console.log("Plain Message:", plainMessage);
        console.log("Recipient Public Key (hex):", recipientPublicKey);
        console.log("Sender Private Key (hex):", senderPrivateKey);

        // 1. Generate sender public key from private key
        console.log("\n--- Step 1: Generate Sender Public Key ---");
        const senderPriv = hex.decode(senderPrivateKey);
        console.log("Sender Private (decoded):", senderPriv);
        console.log("Sender Private length:", senderPriv.length);

        const senderPubBytes = X25519.scalarMultBase(senderPriv);
        console.log("Sender Public Key (raw):", senderPubBytes);
        console.log("Sender Public Key (hex):", hex.encode(senderPubBytes));
        console.log("Sender Public Key length:", senderPubBytes.length);

        // 2. Shared Secret
        console.log("\n--- Step 2: Calculate Shared Secret ---");
        const shared = sharedSecret(senderPrivateKey, recipientPublicKey);

        // 3. Derive Key + nonce (info = senderPub + recipientPub)
        console.log("\n--- Step 3: Build Info and Derive Key/Nonce ---");
        const recipientPubBytes = hex.decode(recipientPublicKey);
        console.log("Recipient Public Key (decoded):", recipientPubBytes);
        console.log("Recipient Public Key length:", recipientPubBytes.length);

        const info = new Uint8Array(senderPubBytes.length + recipientPubBytes.length);
        info.set(senderPubBytes, 0);
        info.set(recipientPubBytes, senderPubBytes.length);
        console.log("Info concatenated (senderPub + recipientPub):", info);
        console.log("Info (hex):", hex.encode(info));

        const { key, nonce } = deriveKeyAndNonce(shared, info);

        // 4. Encrypt plaintext
        console.log("\n--- Step 4: Encrypt Message ---");
        const cipher = new ChaCha20Poly1305(key);
        console.log("ChaCha20Poly1305 cipher created with key");

        const message = new TextEncoder().encode(plainMessage);
        console.log("Message (bytes):", message);
        console.log("Message (hex):", hex.encode(message));
        console.log("Message length:", message.length);

        const ciphertext = cipher.seal(nonce, message);
        console.log("Ciphertext (raw):", ciphertext);
        console.log("Ciphertext (hex):", hex.encode(ciphertext));
        console.log("Ciphertext length:", ciphertext.length);

        // 5. Combine: senderPub (32) + nonce (12) + ciphertext
        console.log("\n--- Step 5: Combine Result ---");
        const result = new Uint8Array(senderPubBytes.length + nonce.length + ciphertext.length);
        result.set(senderPubBytes, 0);
        console.log("Set senderPubBytes at offset 0, length:", senderPubBytes.length);

        result.set(nonce, senderPubBytes.length);
        console.log("Set nonce at offset", senderPubBytes.length, ", length:", nonce.length);

        result.set(ciphertext, senderPubBytes.length + nonce.length);
        console.log("Set ciphertext at offset", senderPubBytes.length + nonce.length, ", length:", ciphertext.length);

        console.log("Final result (raw):", result);
        console.log("Final result (hex):", hex.encode(result));
        console.log("Final result length:", result.length);
        console.log("========== ENCRYPT END ==========\n");

        // Return as plain array of numbers for Kotlin
        return Array.from(result.values());
    } catch (e) {
        console.error("=== ENCRYPT ERROR ===", e);
        throw e;
    }
}

export function decrypt(encryptedData, recipientPrivateKey) {
    console.log("\n========== DECRYPT START ==========");
    try {
        console.log("encryptedData type:", typeof encryptedData);
        console.log("encryptedData:", encryptedData);
        console.log("encryptedData constructor:", encryptedData?.constructor?.name);

        // Convert input array to Uint8Array if needed
        let encryptedBytes;
        if (encryptedData instanceof Uint8Array) {
            encryptedBytes = encryptedData;
        } else if (Array.isArray(encryptedData)) {
            encryptedBytes = new Uint8Array(encryptedData);
        } else if (encryptedData && typeof encryptedData === 'object' && encryptedData.length !== undefined) {
            // Handle array-like objects (includes Kotlin ByteArray)
            encryptedBytes = new Uint8Array(encryptedData.length);
            for (let i = 0; i < encryptedData.length; i++) {
                encryptedBytes[i] = encryptedData[i];
            }
        } else {
            throw new Error("Invalid encryptedData type: " + typeof encryptedData + ", value: " + JSON.stringify(encryptedData));
        }

        console.log("Encrypted Data length:", encryptedBytes.length);
        console.log("Encrypted Data (hex):", hex.encode(encryptedBytes));
        console.log("Recipient Private Key (hex):", recipientPrivateKey);

        if (encryptedBytes.length < 32 + 12 + 16) {
            throw new Error('Encrypted data too short. Expected at least ' + (32 + 12 + 16) + ', got ' + encryptedBytes.length);
        }

        // 1. Extract senderPub, nonce, ciphertext
        console.log("\n--- Step 1: Extract Parts ---");

        const senderPubBytes = encryptedBytes.slice(0, 32);
        console.log("Sender Public Key (raw):", senderPubBytes);
        console.log("Sender Public Key (hex):", hex.encode(senderPubBytes));
        console.log("Sender Public Key length:", senderPubBytes.length);

        const nonce = encryptedBytes.slice(32, 44);
        console.log("Nonce (raw):", nonce);
        console.log("Nonce (hex):", hex.encode(nonce));
        console.log("Nonce length:", nonce.length);

        const ciphertext = encryptedBytes.slice(44);
        console.log("Ciphertext (raw):", ciphertext);
        console.log("Ciphertext (hex):", hex.encode(ciphertext));
        console.log("Ciphertext length:", ciphertext.length);

        const senderPubHex = hex.encode(senderPubBytes);
        console.log("Sender Public Key (hex):", senderPubHex);

        // 2. Shared secret
        console.log("\n--- Step 2: Calculate Shared Secret ---");
        const recipientPriv = hex.decode(recipientPrivateKey);
        console.log("Recipient Private (decoded):", recipientPriv);
        console.log("Recipient Private length:", recipientPriv.length);

        const recipientPubBytes = X25519.scalarMultBase(recipientPriv);
        console.log("Recipient Public Key (raw):", recipientPubBytes);
        console.log("Recipient Public Key (hex):", hex.encode(recipientPubBytes));
        console.log("Recipient Public Key length:", recipientPubBytes.length);

        const shared = sharedSecret(recipientPrivateKey, senderPubHex);

        // 3. Derive key (info = senderPub + recipientPub)
        console.log("\n--- Step 3: Build Info and Derive Key ---");
        const info = new Uint8Array(senderPubBytes.length + recipientPubBytes.length);
        info.set(senderPubBytes, 0);
        info.set(recipientPubBytes, senderPubBytes.length);
        console.log("Info concatenated (senderPub + recipientPub):", info);
        console.log("Info (hex):", hex.encode(info));

        const { key } = deriveKeyAndNonce(shared, info);

        // 4. Decrypt
        console.log("\n--- Step 4: Decrypt Message ---");
        const cipher = new ChaCha20Poly1305(key);
        console.log("ChaCha20Poly1305 cipher created with key");

        const plaintext = cipher.open(nonce, ciphertext);
        console.log("Plaintext (raw):", plaintext);

        if (!plaintext) {
            throw new Error('Decryption failed - cipher.open returned null');
        }

        console.log("Plaintext (hex):", hex.encode(plaintext));
        console.log("Plaintext length:", plaintext.length);

        const decoded = new TextDecoder().decode(plaintext);
        console.log("Decoded message:", decoded);
        console.log("========== DECRYPT END ==========\n");

        return decoded;
    } catch (e) {
        console.error("=== DECRYPT ERROR ===", e);
        throw e;
    }
}