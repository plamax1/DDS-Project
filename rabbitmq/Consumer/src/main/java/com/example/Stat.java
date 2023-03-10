package com.example;

public class Stat {
    private int msgNumber;
    private int delaySum;
    private long startTransmissionTime;

    public Stat(){
        setMsgNumber(0);
        setDelaySum(0);
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
