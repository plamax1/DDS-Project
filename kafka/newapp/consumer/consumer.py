from kafka import KafkaConsumer
from json import loads
from time import sleep, time
from random import randint
import numpy as np
import sys
topic=""
if(len(sys.argv)<2):
    topic="default topic"
else:
    topic= sys.argv[1]


latencies = []
groupid=str(randint(100000000,999999999))
consumer = KafkaConsumer(
    topic,
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id=groupid,
    value_deserializer=lambda x: loads(x.decode('utf-8'))
)
print("Consumer id: " , groupid, " listening on topic ", topic)
event_counter=0;

#dummy poll 
consumer.poll()
#go to end of the stream
consumer.seek_to_end()

# Event loop
for event in consumer:
    event_counter+=1
    event_data = event.value
    # Do whatever you want
    lat= round(time()*1000) - event.timestamp;
    latencies.append(int(lat))
    print(event_data, "Latency: " , lat)
    if event_counter>=10000:
        break
    #sleep(0.1)
sleep(2)
print("The standard deviation of the latecy is: ", np.std(latencies), " ms")