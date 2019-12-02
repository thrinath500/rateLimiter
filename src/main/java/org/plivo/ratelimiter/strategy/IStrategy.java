package org.plivo.ratelimiter.strategy;

import org.plivo.ratelimiter.exception.RateLimitException;

public interface IStrategy {

    void accept(String customerId) throws RateLimitException;

    boolean isWithinLimit(String customerId);

}
