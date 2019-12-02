package org.plivo.ratelimiter.strategy;

import org.plivo.ratelimiter.config.UserAPIRateLimitConfig;
import org.plivo.ratelimiter.entities.SlidingWindowValue;
import org.plivo.ratelimiter.exception.RateLimitException;
import org.plivo.ratelimiter.lock.LockHelper;
import org.plivo.ratelimiter.storage.IStore;

import java.util.Optional;

public class SlidingWindowStrategy implements IStrategy {

    private static final LockHelper<String> LOCKS = new LockHelper<String>();
    private UserAPIRateLimitConfig rateLimitConfig;
    private IStore store;

    public SlidingWindowStrategy(UserAPIRateLimitConfig rateLimitConfig, IStore store) {
        this.rateLimitConfig = rateLimitConfig;
        this.store = store;
    }

    private String getKey(String customerId){
        return customerId;
    }

    @Override
    public void accept(String customerId) throws RateLimitException{
        String key = null;
        SlidingWindowValue value = null;

        // crux to lock only per customerId
        // Blocking call
        synchronized (LOCKS.getOrSetLock(customerId, customerId)){
            try{
                key = getKey(customerId);
                value = Optional.ofNullable(SlidingWindowValue.fromString(store.get(key))).orElse(new SlidingWindowValue());
                long currSize = value.getServingRequests();
                if(rateLimitConfig.get(customerId) > currSize){
                    value.serveIncr(System.currentTimeMillis());
                }else{
                    value.reset();
                    if(rateLimitConfig.get(customerId) <= currSize){
                        value.rejectIncr();
                        throw new RateLimitException("RateLimitException ::: Permissible value : " + rateLimitConfig.get(customerId) +
                                " is less than requested rate of : " + value.getTotalReq());
                    }else{
                        value.serveIncr(System.currentTimeMillis());
                    }
                }
            }finally {
                if(value != null && key != null){
                    store.persist(key, value.string());
                }
            }
        }
    }

    @Override
    public boolean isWithinLimit(String customerId) {
        SlidingWindowValue value = Optional.ofNullable(SlidingWindowValue.fromString(store.get(getKey(customerId)))).
                orElse(new SlidingWindowValue());

        // either serving requests has capacity or the rejected requests is positive
        return (rateLimitConfig.get(customerId) >= value.getServingRequests()) || value.getRejectedRequests() ==0 ;
    }
}
