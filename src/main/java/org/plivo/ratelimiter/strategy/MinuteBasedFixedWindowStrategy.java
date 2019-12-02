package org.plivo.ratelimiter.strategy;

import org.plivo.ratelimiter.config.UserAPIRateLimitConfig;
import org.plivo.ratelimiter.entities.FixedMinuteValue;
import org.plivo.ratelimiter.exception.RateLimitException;
import org.plivo.ratelimiter.lock.LockHelper;
import org.plivo.ratelimiter.storage.IStore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public class MinuteBasedFixedWindowStrategy implements IStrategy{

    private static final LockHelper<String> LOCKS = new LockHelper<String>();
    private UserAPIRateLimitConfig rateLimitConfig;
    private IStore store;
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    private final String SEPARATOR = ":";

    public MinuteBasedFixedWindowStrategy(UserAPIRateLimitConfig rateLimitConfig, IStore store) {
        this.rateLimitConfig = rateLimitConfig;
        this.store = store;
    }

    private String getKey(String customerId){
        return customerId + SEPARATOR + simpleDateFormat.format(new Date());
    }

    @Override
    public void accept(String customerId) throws RateLimitException{
        String key = null;
        FixedMinuteValue value = null;

        // crux to lock only per customerId
        // Blocking call
        synchronized (LOCKS.getOrSetLock(customerId, customerId)){
            try{
                key = getKey(customerId);
                value = Optional.ofNullable(FixedMinuteValue.fromString(store.get(key))).orElse(new FixedMinuteValue());
                if(rateLimitConfig.get(customerId) <= value.getServingRequests()){
                    value.rejectIncr();
                    throw new RateLimitException("RateLimitException :: Permissible value : " + rateLimitConfig.get(customerId) +
                            " is less than requested rate of : " + value.getTotalReq());
                }else{
                    value.serveIncr();
                }
            }finally {
                if(value != null && key != null){
                    store.persist(key, value.toString());
                }
            }
        }
    }

    @Override
    public boolean isWithinLimit(String customerId) {
        return Optional.ofNullable(FixedMinuteValue.fromString(store.get(getKey(customerId)))).
                orElse(new FixedMinuteValue()).
                getRejectedRequests() == 0;
    }
}
