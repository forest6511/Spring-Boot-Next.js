package com.headblog.backend.infra.service.cookie

import com.headblog.backend.domain.model.auth.JwtToken
import java.time.Duration
import java.time.Instant
import java.util.*
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseCookie
import org.springframework.stereotype.Service

@Service
class CookieService {
    private val logger = LoggerFactory.getLogger(CookieService::class.java)

    companion object {
        const val ACCESS_TOKEN_COOKIE_NAME = "access_token"
        const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
    }

    fun createAuthTokenCookie(token: JwtToken, expiresAt: Date): ResponseCookie =
        createCookie(ACCESS_TOKEN_COOKIE_NAME, token.value, expiresAt).also {
            logger.debug("created auth cookie: {}", it)
        }

    fun createRefreshTokenCookie(token: JwtToken, expiresAt: Date): ResponseCookie =
        createCookie(REFRESH_TOKEN_COOKIE_NAME, token.value, expiresAt).also {
            logger.debug("created refresh cookie: {}", it)
        }

    private fun createCookie(
        name: String,
        value: String,
        expiresAt: Date
    ): ResponseCookie =
        ResponseCookie.from(name, value)
            // TODO クッキー設定の見直し
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(Duration.between(Instant.now(), expiresAt.toInstant()))
            .sameSite("Lax")
            .build().also {
                logger.debug("cookie created: name={}, expiresAt={}", name, expiresAt)
            }
}