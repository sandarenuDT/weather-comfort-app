package com.fidenz.weathercomfort.controller;

import com.fidenz.weathercomfort.config.CacheConfig;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheDebugController {

    private final CacheManager cacheManager;

    public CacheDebugController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/status")
    public Map<String, Object> status() {
        CaffeineCache springCache = (CaffeineCache) cacheManager.getCache(CacheConfig.WEATHER_CACHE);
        Cache<Object, Object> nativeCache = springCache.getNativeCache();
        CacheStats stats = nativeCache.stats();

        return Map.of(
                "cacheName", CacheConfig.WEATHER_CACHE,
                "currentSize", nativeCache.estimatedSize(),
                "hitCount", stats.hitCount(),
                "missCount", stats.missCount(),
                "hitRate", stats.hitRate()
        );
    }
}
