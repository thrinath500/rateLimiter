package org.plivo.ratelimiter.config;

import java.util.HashMap;
import java.util.Map;

public class UserAPIRateLimitConfig {
    private static final String DEFAULT_KEY = "_DEFAULT_";
    private final Map<String, Long> userIdToRateLimitMap;

    public UserAPIRateLimitConfig(Map<String, Long> configMap) {
        // Error handling
        if(!configMap.containsKey(DEFAULT_KEY)){
            throw new RuntimeException("DEFAULT key is not present");
        }
        this.userIdToRateLimitMap = new HashMap<String, Long>(configMap);
    }

    public void add(String customerId, Long maxLimit){
        this.userIdToRateLimitMap.put(customerId, maxLimit);
    }

    // Returns null in case of not found
    public Long get(String customerId){
        return this.userIdToRateLimitMap.containsKey(customerId) ?
            this.userIdToRateLimitMap.get(customerId):
            this.userIdToRateLimitMap.get(DEFAULT_KEY);
    }

}
