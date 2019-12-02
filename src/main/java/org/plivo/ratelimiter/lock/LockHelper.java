package org.plivo.ratelimiter.lock;

import java.util.HashMap;
import java.util.Map;

public class LockHelper<T>{

    // Or HashMap with synchronized block
    // MVCC approach, not Optimistic locking
    // Size is function of maximum users accessing
    private final Map<String, T> LOCKS = new HashMap<String, T>();

    public T getOrSetLock(String key, T t){
        if(!LOCKS.containsKey(key)){
            synchronized (LOCKS){
                if(!LOCKS.containsKey(key)){
                    LOCKS.put(key, t);
                }
            }
        }
        return LOCKS.get(key);
    }
}
