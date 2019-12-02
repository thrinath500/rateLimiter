package org.plivo.ratelimiter.storage;

import java.util.HashMap;
import java.util.Map;

public class InMemoryStore implements IStore {

    // Can we use Concurrent hash map?
    private final Map<String, String> storageMap;

    public InMemoryStore() {
        this.storageMap = new HashMap<String, String>();
    }

    public String get(String key) {
        return this.storageMap.get(key);
    }

    public void persist(String key, String value) {
        this.storageMap.put(key, value);
    }

    //TODO : TTL Handling

}
