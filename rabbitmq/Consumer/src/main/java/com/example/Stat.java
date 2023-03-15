package com.example;

import com.rabbitmq.client.Delivery;

public class Stat {
    private int msgNumber;
    private long delaySum;
    private long startTransmissionTime;
    private String UUID;
    private final static String endMessage = "end"; // must be the same declared on sender side.

    public Stat(String UUID) {
        setMsgNumber(0);
        setDelaySum(0);
        this.UUID = UUID;
    }

    public void updateStat(Delivery delivery, String message) {
        try {
            long rcvInstant = (long) delivery.getProperties().getHeaders().get("timestamp");
            long delay = System.nanoTime() - rcvInstant;

            /*
             * // VERBOSE
             * verbose(delivery, message, delay);
             */

            incMsgNumber();
            incDelaySum(delay);
        } catch (NullPointerException e) {
            System.out.println("Message does not contain the param timestamp or user-id !!");
        }
    }

    public void computeFinalStat() {
        // Print average
        double avgDelay = computeAverageDelay();
        System.out.println("[" + getUUID() + "] '" + endMessage + "' RECEIVED! The average delay is "
                + (double) ((avgDelay) / (double) (10 * 10 * 10 * 10 * 10 * 10)) + "ms.");

        // compute total elapsed time
        long difference = computeTimeDifference();

        // Print msg/s
        float msgRate = computeMessageRate(difference);
        System.out
                .println("[" + getUUID() + "] Received " + getMsgNumber() + " messages in "
                        + difference / (10 * 10 * 10 * 10 * 10 * 10)
                        + "ms ( + " + difference / (10 * 10 * 10 * 10 * 10 * 10 * 10 * 10 * 10) + "s). So you received "
                        + String.format("%.02f", msgRate) + " messages/s.");

        // keep listening from this producer, so restart Stat
        setDelaySum(0);
        setMsgNumber(0);
        setStartTransmissionTime(0);
    }

    public String getUUID() {
        return this.UUID;
    }

    public long getStartTransmissionTime() {
        return startTransmissionTime;
    }

    public void setStartTransmissionTime(long startTransmissionTime) {
        this.startTransmissionTime = startTransmissionTime;
    }

    public long getDelaySum() {
        return delaySum;
    }

    public void setDelaySum(long delaySum) {
        this.delaySum = delaySum;
    }

    public void incDelaySum(long delay) {
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

    public double computeAverageDelay() {
        return (double) (((double) getDelaySum()) / ((double) getMsgNumber()));
    }

    public long computeTimeDifference() {
        return System.nanoTime() - getStartTransmissionTime();
    }

    public float computeMessageRate(long difference) {// msg/s
        return ((float) this.getMsgNumber()) * 1000000000 / ((float) difference);
    }

    public void verbose(Delivery delivery, String message, long delay) {
        System.out.println("[" + getUUID() + "] - " + getMsgNumber() + " - Delay: ("
                + ((float) delay / (float) (10 * 10 * 10 * 10 * 10 * 10)) + "ms) Received '" +
                delivery.getEnvelope().getRoutingKey() + "': '" + message + "'");
    }
}
