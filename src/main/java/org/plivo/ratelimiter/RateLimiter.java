package org.plivo.ratelimiter;

import org.plivo.ratelimiter.exception.RateLimitException;
import org.plivo.ratelimiter.strategy.IStrategy;

public class RateLimiter {

    private IStrategy strategy;

    public RateLimiter(IStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean isAPIRateWithinLimit(String customerId){
        return strategy.isWithinLimit(customerId);
    }

    // This class is needed to abstract the implementation of the underlying configs to the user
    // Future can be enhanced with timeout on the blocking time.
    // request(customerId, timeout)
    public final void request(String customerId) throws RateLimitException {
        strategy.accept(customerId);
    }
}
