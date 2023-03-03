from kafka import KafkaConsumer
from json import loads
from time import sleep, time
from random import randint
import numpy as np
import sys
from monitor import send_latencies, mean_lat
topic=""
END_CODE = "end"
first_message_timestamp=0;

if len(sys.argv)<2:
    print("Missing arguments")
    print("Usage : python consumer.py -topicname")
    exit(0)
#Args
topic= sys.argv[1].strip()

latencies = []
#Generate random consumer id
groupid=str(randint(100000000,999999999))
#Consumer instantiation
consumer = KafkaConsumer(
    topic,
    bootstrap_servers=['localhost:9092'],
    auto_offset_reset='earliest',
    enable_auto_commit=True,
    group_id=groupid,
    value_deserializer=lambda x: loads(x.decode('utf-8'))
)
print("Consumer id: " , groupid, " listening on topic-", topic,"-")
event_counter=0;
#dummy poll 
consumer.poll()
#go to end of the stream
consumer.seek_to_end()
thro={}
# Event loop
msg_size =0
counter=0
for event in consumer:
    counter+=1
    event_data = event.value
    #print(event_data)
    if first_message_timestamp==0:
        first_message_timestamp=event.timestamp;
    lat= round(time()*1000) - event.timestamp;
    sec=round(time())
   #[if_true] if [expression] else [if_false]
    if sec in thro.keys():
        thro[sec]+=1  
    else:
        thro[sec]=0
    latencies.append(int(lat))
    #print(event_data, "Latency: " , lat)
    if event_counter%100==0:
        send_latencies(groupid, latencies)
        latencies=[]
    if event_data == END_CODE:
        print("The mean throughput is ", counter/((event.timestamp-first_message_timestamp)*1000), "msg/s")
        break
    #sleep(0.1)
coll=0
for i in thro.keys():
    coll+=thro[i]
#print("The mean throughput is ", coll/len(thro.keys()), "msg/s")
#print(thro)
print("The mean of the latecy for consumer ", groupid, " is : ", mean_lat(groupid), " ms")