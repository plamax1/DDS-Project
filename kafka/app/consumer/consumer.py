from kafka import KafkaConsumer
from json import loads
from time import sleep, time
from random import randint
import numpy as np
import sys
from monitor import send_latencies, mean_lat
topic=""
END_CODE = "end_msg"

if(len(sys.argv)<3):
    topic="topic_test"
    n_msg=100000
else:
    topic= sys.argv[1].strip()
    n_msg=int(sys.argv[2].strip())


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
print("Consumer id: " , groupid, " listening on topic-", topic,"-")
event_counter=0;
print("Prova")
#dummy poll 
consumer.poll()
#go to end of the stream
consumer.seek_to_end()
thro={}
# Event loop
msg_size =0

for event in consumer:
    event_data = event.value
    #print(event_data)

    # Do whatever you want
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
        break
    #sleep(0.1)
sleep(2)
coll=0
for i in thro.keys():
    coll+=thro[i]
print("The mean throughput is ", coll/len(thro.keys()), "msg/s")
#print(thro)
print("The mean of the latecy for consumer ", groupid, " is : ", mean_lat(groupid), " ms")