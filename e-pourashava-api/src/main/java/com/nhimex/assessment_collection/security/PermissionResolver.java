package com.nhimex.assessment_collection.security;

import com.nhimex.assessment_collection.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionResolver {

    public static final String CACHE_NAME = "user-permissions";

    private final PermissionRepository permissionRepository;
    private final CacheManager cacheManager;

    @SuppressWarnings("unchecked")
    public List<String> resolve(Long userId) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            List<String> cached = cache.get(userId, List.class);
            if (cached != null) {
                return cached;
            }
        }

        List<String> codes = new ArrayList<>(permissionRepository.findCodesByUserId(userId));
        if (cache != null) {
            cache.put(userId, codes);
        }
        return codes;
    }

    public void evictUser(Long userId) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null && userId != null) {
            cache.evict(userId);
        }
    }

    public void evictAll() {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache != null) {
            cache.clear();
        }
    }
}
