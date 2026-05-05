package com.example.demo.utils.data

import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object RequestUtils {
    fun getCurrentRequest(): HttpServletRequest? {
        val attributes = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        return attributes?.request
    }

    fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        return if (!xForwardedFor.isNullOrEmpty()) {
            xForwardedFor.split(",").first().trim()
        } else {
            request.remoteAddr
        }
    }

    fun getUserAgent(request: HttpServletRequest): String? = request.getHeader("User-Agent")
}
