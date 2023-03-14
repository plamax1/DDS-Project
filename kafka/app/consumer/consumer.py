from kafka import KafkaConsumer
from json import loads
from time import sleep, time
from random import randint
import numpy as np
import sys
import json
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
msg_lat={}
n_msg={}
first_msg_time={}
last_msg_time={}
for event in consumer:
    payload= json.loads(event.value)
    event_type= payload.get('type')
    prod_id=str(payload.get('id'))
    #print('Received msg of type: ', event_type, ' from producer: ', prod_id)
    if(event_type=='init'):
        print('Producer id received init msg: ', prod_id)
        first_msg_time[prod_id]=round(time()*1000)
        last_msg_time[prod_id]=0
        n_msg[prod_id]= 1
        msg_lat[prod_id]= []
        msg_lat[prod_id].append(round(time()*1000) - event.timestamp)
        continue
    if event_type=='end':
        print("received end message from producer", prod_id)
        last_msg_time[prod_id]=round(time()*1000)
        print(" Received ", n_msg[prod_id], " messages from producer ", prod_id)
        print("The mean throughput for producer ", prod_id, " is ", n_msg[prod_id]*1000/((last_msg_time[prod_id]-first_msg_time[prod_id])), "msg/s")
        print("The mean latency is for producer ", prod_id ," is ", (sum(msg_lat[prod_id]))/n_msg[prod_id], "ms")
        continue
    #print('receiving ordinary message: ', event_type, " nmsg: ", n_msg[prod_id])
    n_msg[prod_id]+= 1
    msg_lat[prod_id].append(round(time()*1000) - event.timestamp)


