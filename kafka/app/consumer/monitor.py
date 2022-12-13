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
    return np.std(latencies[consumer_id])
    #print("The standard deviation of the latecy is: ", np.std(latencies), " ms")
