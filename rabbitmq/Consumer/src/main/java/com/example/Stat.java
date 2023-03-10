package com.example;

import com.rabbitmq.client.Delivery;

public class Stat {
    private int msgNumber;
    private int delaySum;
    private long startTransmissionTime;
    private String UUID;
    private final static String endMessage = "end"; // must be the same declared on sender side.

    public Stat(String UUID){
        setMsgNumber(0);
        setDelaySum(0);
        this.UUID = UUID;
    }

    public String getUUID(){
        return this.UUID;
    }

    public void printAndUpdateStat(Delivery delivery, String message) {
        try{
        long rcvInstant = (long) delivery.getProperties().getHeaders().get("timestamp");
        long delay = System.nanoTime() - rcvInstant;
        System.out.println("From: " + getUUID() + "| " +  getMsgNumber() + "] Delay:[" + delay + " ns (" + delay/(10*10*10*10*10*10) + "ms)] Received '" +
                delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");
        incMsgNumber();
        incDelaySum((int) delay);
        }
        catch(NullPointerException e){
            System.out.println("Message does not contain the param timestamp or user-id !!");
        }
    }

    public void computeFinalStat() {
        // Print average
        float avgDelay = computeAverageDelay();
        System.out.println(endMessage + " RECEIVED! The average delay is " + (double)((double)avgDelay)/((double)(10*10*10*10*10*10)) + "ms.");

        // Print msg/s
        long difference = computeTimeDifference();
        float msgRate = computeMessageRate(difference);
        System.out.println("You received " + getMsgNumber() + " messages in " + difference /(10*10*10*10*10*10) +"ms ( + " + difference /(10*10*10*10*10*10*10*10*10) + "s). So you received " + String.format("%.02f", msgRate) + " messages/s.");

        // keep listening from this producer, so restart Stat
        this.delaySum=0;
        this.msgNumber=0;
        this.startTransmissionTime=0;
    }
    
    public long getStartTransmissionTime() {
        return startTransmissionTime;
    }

    public void setStartTransmissionTime(long startTransmissionTime) {
        this.startTransmissionTime = startTransmissionTime;
    }

    public int getDelaySum() {
        return delaySum;
    }

    public void setDelaySum(int delaySum) {
        this.delaySum = delaySum;
    }

    public void incDelaySum(int delay) {
        this.delaySum += delay;
    }

    public int getMsgNumber() {
        return msgNumber;
    }

    public void setMsgNumber(int msgNumber) {
        this.msgNumber = msgNumber;
    }

    public void incMsgNumber() {
        this.msgNumber = msgNumber + 1;
    }

    public float computeAverageDelay(){
        return (float) (((float) getDelaySum()) / ((float) getMsgNumber()));
    }

    public long computeTimeDifference(){
        return System.nanoTime() - getStartTransmissionTime();
    }

    public float computeMessageRate(long difference){// msg/s
        return ((float)this.getMsgNumber())*1000000000/((float)difference);
    }
}
