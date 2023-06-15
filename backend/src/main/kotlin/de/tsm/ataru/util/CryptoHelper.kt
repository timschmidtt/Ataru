package de.cewe.deskstar.util

import java.security.MessageDigest
import java.security.SecureRandom
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.StringUtils

/**
 * Convenience class to handle crypto.
 *
 * @author Holger Cremer (Holger.Cremer@cewe.de)
 * @since 15.05.2014
 */
object CryptoHelper {
  // reseed the PRNG after this amount of bytes
  private const val NEW_SEED_AFTER = 10000
  private val PRNG: SecureRandom = SecureRandom.getInstance("SHA1PRNG")
  private var secureByteCounter = 0

  /**
   * Uses an underline [SecureRandom] to create random bytes. This implementation resseds the RNG after [.NEW_SEED_AFTER] amount
   * of bytes to avoid some security problems with very long sequences.
   *
   * @param bytes the bytes to fill with random
   */
  @Synchronized
  fun nextSecureBytes(bytes: ByteArray) {
    secureByteCounter += bytes.size
    if (secureByteCounter > NEW_SEED_AFTER) {
      secureByteCounter = 0
      PRNG.setSeed(PRNG.generateSeed(128))
    }
    PRNG.nextBytes(bytes)
  }

  val sHA1Digist: MessageDigest = MessageDigest.getInstance("SHA-1")

  /**
   * From a byte[] returns a base 64 representation
   *
   * @param data byte[]
   * @return String
   */
  fun byteToBase64(data: ByteArray): String = StringUtils.newStringUtf8(Base64.encodeBase64(data))

  /**
   * From a byte[] returns a base 64 representation with URL safe characters.
   *
   * @param data byte[]
   * @return String
   */
  fun byteToBase64UrlSafe(data: ByteArray): String = StringUtils.newStringUtf8(Base64.encodeBase64(data, false, true))

  /**
   * From a base 64 representation, returns the corresponding byte[]
   *
   * @param data String The base64 representation
   * @return byte[]
   */
  fun base64ToByte(data: String): ByteArray = Base64.decodeBase64(data)

  /**
   * Masks the given string by return the first four chars and adding "...". Useful to censor passwords and token.
   *
   * @param str the input string
   * @return the masked string.
   */
  fun maskString(str: String?, showFirst: Int): String {
    var showFirst = showFirst
    if (str == null) {
      return "..."
    }
    showFirst = Math.min(str.length, showFirst)
    return str.substring(0, showFirst) + "..."
  }
}
