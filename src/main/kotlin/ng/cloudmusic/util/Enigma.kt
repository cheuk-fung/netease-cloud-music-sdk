package ng.cloudmusic.util

import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.binary.Hex
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import java.math.BigInteger
import java.security.KeyFactory
import java.security.spec.RSAPublicKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object Enigma {
    private const val NONCE = "0CoJUm6Qyw8W8jud"
    private const val IV = "0102030405060708"
    private val MODULUS = BigInteger("00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7", 16)
    private val PUBLIC_EXPONENT = BigInteger("010001", 16)

    fun encryptRequestBody(body: String): Map<String, String> {
        val secretKey = RandomStringUtils.randomAlphanumeric(16)
        val params = encryptParams(body, secretKey)
        val encSecKey = encryptSecretKey(secretKey)
        return mapOf("params" to params, "encSecKey" to encSecKey)
    }

    private fun encryptParams(text: String, secretKey: String) = aesEncrypt(aesEncrypt(text, NONCE), secretKey)

    private fun aesEncrypt(text: String, secretKey: String): String {
        val secretKeySpec = SecretKeySpec(secretKey.toByteArray(), "AES")
        val ivParameterSpec = IvParameterSpec(IV.toByteArray())
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedText = cipher.doFinal(text.toByteArray())
        return Base64.encodeBase64String(encryptedText)
    }

    private fun encryptSecretKey(secretKey: String) = rsaEncrypt(secretKey.reversed())

    private fun rsaEncrypt(text: String): String {
        val rsaPublicKeySpec = RSAPublicKeySpec(MODULUS, PUBLIC_EXPONENT)
        val rsaPublicKey = KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec)
        val cipher = Cipher.getInstance("RSA/ECB/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey)
        val encryptedText = cipher.doFinal(text.toByteArray())
        return StringUtils.leftPad(Hex.encodeHexString(encryptedText), 256, '0')
    }
}
