package org.plivo.ratelimiter.storage;

public interface IStore {

    String get(String key);

    void persist(String key, String value);
}
