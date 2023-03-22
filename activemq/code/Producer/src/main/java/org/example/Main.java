package org.example;

import org.example.messaging.MProducer;
import org.example.models.Message;

import java.util.ArrayList;
import java.util.Collections;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //String topic = "d";
        String topic = args[0];
        System.out.println("Name of the topic: " + topic);
        //String lampda = "5000";
        String lampda = args[2];
        //String number = "5000";
        String number = args[1];
        System.out.println("Number of messages you want to send with topic " + topic + ": " + number);
        //int number = sc.nextInt();
        long timestamp = 0;
        long targetTime = 0;
        long time = 0;

        ArrayList<Double> sleeps = new ArrayList<Double>();
        sleeps = poisson(Integer.parseInt(lampda), Integer.parseInt(number));
        ArrayList<Long> longSleeps = new ArrayList<Long>();

        for (int i=0; i<sleeps.size(); i++){
            longSleeps.add((long)(sleeps.get(i) * 1000000000));
        }
        ArrayList<Message>Messages = new ArrayList<Message>();
        for (int j=0; j<Integer.parseInt(number)-1; j++) {
            Message i = new Message("Hello");
            Messages.add(i);
        }


        MProducer.initialize("tcp://localhost:61616", topic);

        for (int j=0; j<Messages.size(); j++) {
            MProducer.add(Messages.get(j));
            timestamp = System.nanoTime();
            targetTime = timestamp + longSleeps.get(j);
            while(System.nanoTime()<targetTime) {
            }
            //Thread.sleep(0, longSleeps.get(j));

        }
        Message i = new Message("end");
        MProducer.add(i);

        MProducer.terminate();






        //long z = 0;
        //for (int i=0; i<sleeps.size()/1000; i++){
        //    z = Math.round(sleeps.get(i)*1000);
        //    System.out.print(z + ", ");

        //}

        /*for (int i=0; i<sleeps.size(); i++) {
        }
        int j=0;
        double targetTime = 0;
        while (j<Integer.parseInt(number)-1) {
            Date date1 = new Date();
            double timestamp = date1.getTime();

            if (timestamp > targetTime) {
                Message i = new Message("Hello");
                if (MProducer.inError()) {
                    System.out.println(MProducer.error());
                } else {
                    MProducer.add(i);
                }
                double z = sleeps.get(j);
                targetTime = timestamp + z;
                j++;
            }


        }
        Message i = new Message("end");
        MProducer.add(i);

        MProducer.terminate();


        /*for (int j=0; j<Integer.parseInt(number)-1; j++) {
            //System.out.println();
            Message i = new Message("Hello");
            if (MProducer.inError()) {
                System.out.println(MProducer.error());
            } else {
                MProducer.add(i);
                //System.out.println("message " + i.id + " sent");
                if (j<Integer.parseInt(number)-1) {
                    long z = Math.round(sleeps.get(j) * 1000);
                    //System.out.println("z is : " + z);
                   // Thread.sleep(longSleeps.get(j));
                    Thread.sleep(z);
                    //sum1 = sum1 + z;
                    //System.out.println("lo sleep è: " + z);
                }
            }
        }
        //System.out.println("The sum of z is: " + sum1);*/



        /*Message i = new Message("end");
        MProducer.add(i);

        MProducer.terminate();*/

    }

    public static ArrayList poisson(int lampda, int n) {
        double time_span= n/lampda;
        ArrayList<Double>events = new ArrayList<Double>();
        ArrayList<Double> sleeps= new ArrayList<Double>();
        for (int j=0; j<n; j++) {
            events.add(Math.random());
        }
        Collections.sort(events);
        for(int j=0; j<n; j++) {
            events.set(j,events.get(j)*time_span);
        }
        for(int i=1; i<events.size(); i++) {
            sleeps.add(events.get(i)-events.get(i-1));
        }
        return sleeps;
    }

    /*public static void sendWait(long waitTime) {
        //Date date = new Date();
        //long timestamp = date.getTime()*1000;
        long timestamp = System.nanoTime();
        long targetTime = timestamp + waitTime;
        long time = System.nanoTime();
        while(time<targetTime){
            //Date date1 = new Date();
            time= System.nanoTime();
        }
    }*/

}
