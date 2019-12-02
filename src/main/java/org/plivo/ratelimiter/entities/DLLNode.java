package org.plivo.ratelimiter.entities;

import java.io.Serializable;

public class DLLNode {
    DLLNode prev;
    DLLNode next;
    long val;

    public DLLNode(DLLNode prev, DLLNode next, long val) {
        this.prev = prev;
        this.next = next;
        this.val = val;
    }

    public DLLNode getPrev() {
        return prev;
    }

    public DLLNode getNext() {
        return next;
    }

    public long getVal() {
        return val;
    }
}
