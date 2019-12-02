Steps to execute.
1. Install jre/jdk if not already done
2. Download the jar attached to your system
3. Run the following command  "java -cp <Download path>/rateLimiter-1.0-SNAPSHOT.jar org.plivo.ratelimiter.Driver"

Implementation details:

1. Driver is the executable class  - test
2. RateLimiter is for
3. Strategy -> FixedWindow (Minute based), Sliding Window
4. Store -> Persistence layer for storing the state for user. Single implementation of Inmemory level is implemented. Distributed Maps /Shared not covered.
5. LockingHelper -> For handling concurrency writes/reads for a user. This is a generic helper function. Used across strategies in their state management.

Description:

MinuteBasedFixedWindowStrategy -> This is the strategy class to be used for running in fixed intervals. Currently limited to minute based. Test cases use this for demo.
SlidingWindow implementation is done. However facing some SerDe issues. Logic is complete. However test cases do not cover this strategyLocks are blocking in nature. No timeouts implemented.

Test case:
1. Spawns threads for user ABC(configured -15),XPQR(Not configured, default should apply - 10).
2. Prints the status (Within Limit) of both the users in 5 second time Frame.
3. ABC starts with 5 threads, no exception case
4. Later spawns more threads to hit a negative case for ABC
5. Prints the status for a minute and the status should get rectified after a minute since the window lapses.


