package org.plivo.ratelimiter;

import org.plivo.ratelimiter.config.UserAPIRateLimitConfig;
import org.plivo.ratelimiter.exception.RateLimitException;
import org.plivo.ratelimiter.storage.InMemoryStore;
import org.plivo.ratelimiter.strategy.IStrategy;
import org.plivo.ratelimiter.strategy.MinuteBasedFixedWindowStrategy;
import org.plivo.ratelimiter.strategy.SlidingWindowStrategy;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Driver {

    public static void main(String[] args) {

        // Strategy
        IStrategy strategy = new MinuteBasedFixedWindowStrategy(loadUserConfig(), new InMemoryStore());
//        IStrategy strategy = new SlidingWindowStrategy(loadUserConfig(), new InMemoryStore());

        final RateLimiter rateLimiter = new RateLimiter(strategy);
        final String testCustomerId = "ABC";

        spawnTestForCustomer(testCustomerId, rateLimiter, 1);

        printStatsForCustomer(testCustomerId, rateLimiter);
    }

    private static void printStatsForCustomer(String testCustomerId, final RateLimiter rateLimiter){
        int testCounter = 0;
        while(true){
            System.out.println(testCounter + ". Test status is : " + rateLimiter.isAPIRateWithinLimit(testCustomerId));
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                // No action
            }
            testCounter++;
        }
    }

    private static void spawnTestForCustomer(final String testCustomerId, final RateLimiter rateLimiter, int threadCount){
        for (int i =0; i< threadCount; i++){
            System.out.println(new Date());
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    while(true){
                        try {
                            rateLimiter.request(testCustomerId);
                            System.out.println("Accepted for thread : " + Thread.currentThread().getName());
                        } catch (RateLimitException e) {
                            System.out.println(e.getMessage() + " for thread : " + Thread.currentThread().getName());
                        }
                        try {
                            Thread.sleep(10*1000);
                        } catch (InterruptedException e) {
                            // No action
                        }
                    }
                }
            }, "thread" + i + " : " + testCustomerId);
            thread.start();
            try {
                Thread.sleep(1 * 1000);
            } catch (InterruptedException e) {
//                No Action
            }
        }
    }

    private static UserAPIRateLimitConfig loadUserConfig(){
        // Config
        Map<String, Long> userConfig = new HashMap<>();
        userConfig.put("ABC", 5l);
        userConfig.put("XYZ", 20l);
        userConfig.put("_DEFAULT_", 10l);
        UserAPIRateLimitConfig rateLimitConfig = new UserAPIRateLimitConfig(userConfig);
        return rateLimitConfig;
    }
}
