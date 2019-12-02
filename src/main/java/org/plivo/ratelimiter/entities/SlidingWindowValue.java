package org.plivo.ratelimiter.entities;

import com.google.gson.Gson;

//Assumption : Nodes will be in sorted order given the argument of insertion
public class SlidingWindowValue {

    private DLLNode head;
    private DLLNode tail;
    private long servingRequests;
    private long rejectedRequests;

    private static final Gson GSON = new Gson();

    public SlidingWindowValue() {
    }

    // 1 minute
    private final long SLIDING_WINDOW = 1 * 30 * 1000;


    public Long getServingRequests() {
        return servingRequests;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }

    public static SlidingWindowValue fromString(String inputStr) {
        return GSON.fromJson(inputStr, SlidingWindowValue.class);
    }

    public void reset() {
        long startPoint = System.currentTimeMillis() - SLIDING_WINDOW;
        if(tail.val < startPoint){
            // Last node is old
            //reset all
            resetAll();
        }else if(head.val > startPoint){
            // No action
            System.out.println("All items are latest and falling in window");
        }else{
            // Binary search is better
            DLLNode temp = head;
            int counter = 1;
            while(temp.next.val < startPoint){
                temp = temp.next;
                counter++;
            }
            head = temp.next;
            servingRequests -= counter;
        }

    }

    public void rejectIncr() {
        rejectedRequests++;
    }

    public void serveIncr(long currentTimeMillis) {
        DLLNode newNode = new DLLNode(tail, null, currentTimeMillis);
        if(head == null && tail == null){
            head = newNode;
        }else{
            tail.next = newNode;
        }
        newNode.prev = tail;
        tail = newNode;
        servingRequests++;
    }

    public Long getTotalReq() {
        return servingRequests + rejectedRequests;
    }

    private void resetAll(){
        head = null;
        tail = null;
        servingRequests = 0;
        rejectedRequests = 0;
    }

    public String string(){
        return GSON.toJson(this);
    }
}

