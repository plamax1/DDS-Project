from time import sleep, time
from json import dumps
from kafka import KafkaProducer
import numpy as np

latencies={}


producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)

def send_latencies(consumer_id, lat):
    if consumer_id in latencies.keys():
        latencies[consumer_id].extend(lat)
    else:
        latencies[consumer_id]=lat

def mean_lat(consumer_id):
    #print("The standard deviation of the latecy is: ", np.std(latencies), " ms")
    return np.mean(latencies[consumer_id])
    


#generate 10000 events with a lampda of 5.0 events per unit time
import random
def generate_poisson(lampda, n):
    #n is the number of events to generate.
    #time span is n/mu
    time_span=n/lampda
    events=[]
    sleeps=[]
    #place n events uniformly distributed and place in array events.
    for j in range(0,n)  :
        events.append(random.random())
    #sort the array
    events.sort()

    #at this point the events array contains n events distributed from 
    #(0.0 to 1.0). The next step is to scale the timing of all events by multiplying 
    #by n/mu which is the total time span of events.
    for j in range(0,n)  :
        events[j]*=time_span
    #The events array now contains an array of event times which are approximate the #Poisson distribution with a mean arrival rate of mu per time interval.
    #This has been tested using a chi squared test and closely approximates the 
    #Poisson Distribution.
    #But we are interested in returning the sleep time between events

    for i in range(1,(len(events))):
        #print(i)
        sleeps.append(events[i]-events[i-1])
    return sleeps