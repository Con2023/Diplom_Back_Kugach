package com.example.demo.utils

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Service
class RateLimitingService {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    fun tryConsume(key: String): Boolean {
        val bucket =
            buckets.computeIfAbsent(key) {
                Bucket
                    .builder()
                    .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(1))))
                    .build()
            }
        return bucket.tryConsume(1)
    }
}
