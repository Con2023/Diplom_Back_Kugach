package com.example.demo.configs.jwts

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import java.security.KeyFactory
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64

@Configuration
class JwtKeyConfig {
    @Value("\${jwt.private-key}")
    private lateinit var privateKeyResource: Resource

    @Value("\${jwt.public-key}")
    private lateinit var publicKeyResource: Resource

    @Bean
    fun privateKey(): RSAPrivateKey {
        val keyContent = privateKeyResource.inputStream.use { it.readAllBytes() }.toString(Charsets.UTF_8)
        val privateKeyPEM =
            keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replace("\n", "")
                .replace("\r", "")
                .trim()

        val decoded = Base64.getDecoder().decode(privateKeyPEM)
        val keySpec = PKCS8EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePrivate(keySpec) as RSAPrivateKey
    }

    @Bean
    fun publicKey(): RSAPublicKey {
        val keyContent = publicKeyResource.inputStream.use { it.readAllBytes() }.toString(Charsets.UTF_8)
        val publicKeyPEM =
            keyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replace("\n", "")
                .replace("\r", "")
                .trim()

        val decoded = Base64.getDecoder().decode(publicKeyPEM)
        val keySpec = X509EncodedKeySpec(decoded)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec) as RSAPublicKey
    }
}
