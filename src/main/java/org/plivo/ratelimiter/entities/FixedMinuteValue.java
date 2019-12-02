package org.plivo.ratelimiter.entities;

public class FixedMinuteValue {
    private long servingRequests;
    private long rejectedRequests;
    private final static String SEPARATOR = ":";

    public FixedMinuteValue() {
    }

    private FixedMinuteValue(Long servingRequests, Long rejectedRequests) {
        this.servingRequests = servingRequests;
        this.rejectedRequests = rejectedRequests;
    }

    public long getServingRequests() {
        return servingRequests;
    }

    public long getRejectedRequests() {
        return rejectedRequests;
    }

    public void serveIncr(){
        this.servingRequests++;
    }

    public void rejectIncr(){
        this.rejectedRequests++;
    }

    public Long getTotalReq(){
        return this.servingRequests + this.rejectedRequests;
    }

    public static FixedMinuteValue fromString(String inputStr){
        if(inputStr == null){
            return null;
        }
        String[] splitArr = inputStr.split(SEPARATOR);
        return new FixedMinuteValue(Long.parseLong(splitArr[0]), Long.parseLong(splitArr[1]));
    }

    public String toString(){
        return servingRequests + SEPARATOR + rejectedRequests;
    }
}