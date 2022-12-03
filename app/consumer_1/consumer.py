from kafka import KafkaConsumer
from json import loads
from time import sleep, time
import numpy as np
latencies = []
consumer = KafkaConsumer(
    'topic_test',
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id='group_1',
    value_deserializer=lambda x: loads(x.decode('utf-8'))
)
event_counter=0;
for event in consumer:
    event_counter+=1
    event_data = event.value
    # Do whatever you want
    lat= round(time()*1000) - event.timestamp;
    latencies.append(int(lat))
    print(event_data, "Latency: " , lat)
    if event_counter>=100:
        break
    #sleep(0.1)
sleep(2)
print("The standard deviation of the latecy is: ", np.std(latencies), " ms")