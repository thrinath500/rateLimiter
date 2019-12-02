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

    public static void main(String[] args) throws InterruptedException {

        // Strategy
        IStrategy strategy = new MinuteBasedFixedWindowStrategy(loadUserConfig(), new InMemoryStore());
//        IStrategy strategy = new SlidingWindowStrategy(loadUserConfig(), new InMemoryStore());

        final RateLimiter rateLimiter = new RateLimiter(strategy);
        final String testCustomerId = "ABC";
        final String defaultCustomerId = "XPQR";

        // Testing for ABC
        System.out.println(new Date() + " Spawning 5 threads for customer : " + testCustomerId);
        System.out.println(new Date() + " Spawning 15 threads for customer : " + defaultCustomerId);
        System.out.println("\n");

        spawnTestForCustomer(testCustomerId, rateLimiter, 5);
        spawnTestForCustomer(defaultCustomerId, rateLimiter, 15);

        Thread.sleep(2 * 1000);

        System.out.println("\n");
        System.out.println("Printing stats for customer : " + defaultCustomerId + " every 5 secs");
        printStatsForCustomer(defaultCustomerId, rateLimiter, 2);
        System.out.println("\n");
        System.out.println("Printing stats for customer : " + testCustomerId + " every 5 secs");
        printStatsForCustomer(testCustomerId, rateLimiter, 2);

        System.out.println("\n");

        Thread.sleep(2 * 1000);

        System.out.println(new Date() + " Spawning more 20 threads for customer : " + testCustomerId);
        spawnTestForCustomer(testCustomerId, rateLimiter, 20);

        System.out.println("\n");

        Thread.sleep(2 * 1000);

        System.out.println("Printing stats for customer : " + testCustomerId + " every 5 secs");
        printStatsForCustomer(testCustomerId, rateLimiter, 12);

        System.out.println("\n");

        System.out.println("Printing stats for customer : " + defaultCustomerId + " every 5 secs");
        printStatsForCustomer(testCustomerId, rateLimiter, 2);

        System.exit(0);
    }

    private static void printStatsForCustomer(String testCustomerId, final RateLimiter rateLimiter, int counter){
        int testCounter = 0;
        while(testCounter < counter){
            System.out.println(new Date()  + " ######### " + testCounter + ". WITHIN Limit  status is : " + rateLimiter.isAPIRateWithinLimit(testCustomerId));
            try {
                Thread.sleep(5 * 1000);
            } catch (InterruptedException e) {
                // No action
            }
            testCounter++;
        }
    }

    private static void spawnTestForCustomer(final String testCustomerId, final RateLimiter rateLimiter, int threadCount){
        for (int i =1; i<= threadCount; i++){
            Thread thread = new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        rateLimiter.request(testCustomerId);
                        System.out.println(new Date() + "###### Accepted for : " + Thread.currentThread().getName());
                    } catch (RateLimitException e) {
                        System.out.println(e.getMessage() + " for thread : " + Thread.currentThread().getName());
                    }
//                        try {
//                            Thread.sleep(5 * 1000);
//                        } catch (InterruptedException e) {
//                            // No action
//                        }
                }
            }, "thread" + i + " : " + testCustomerId);
            thread.start();
        }
    }

    private static UserAPIRateLimitConfig loadUserConfig(){
        System.out.println("Loading config as follows:::::::::");
        // Config
        Map<String, Long> userConfig = new HashMap<>();
        userConfig.put("ABC", 15l);
        userConfig.put("XYZ", 20l);
        userConfig.put("_DEFAULT_", 10l);
        UserAPIRateLimitConfig rateLimitConfig = new UserAPIRateLimitConfig(userConfig);
        System.out.println(userConfig);
        System.out.println();
        return rateLimitConfig;
    }
}
