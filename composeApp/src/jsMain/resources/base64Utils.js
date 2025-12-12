/**
 * Convert ByteArray (dari Kotlin) ke Base64 string
 * @param {number[]} bytes - Array of byte values (0-255)
 * @returns {string} Base64 encoded string
 */
export function bytesToBase64(bytes) {
    let binaryString = '';
    for (let i = 0; i < bytes.length; i++) {
        binaryString += String.fromCharCode(bytes[i] & 0xFF);
    }
    return btoa(binaryString);
}

/**
 * Convert Base64 string ke ByteArray
 * @param {string} base64 - Base64 encoded string
 * @returns {number[]} Array of byte values (0-255)
 */
export function base64ToBytes(base64) {
    const binaryString = atob(base64);
    const bytes = [];
    for (let i = 0; i < binaryString.length; i++) {
        bytes.push(binaryString.charCodeAt(i) & 0xFF);
    }
    return bytes;
}