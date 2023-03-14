from time import sleep, time
from json import dumps
from kafka import KafkaProducer
from utils import generate_poisson
import sys
import json
import numpy as np
from random import randint
END_CODE="end"
producer_id=str(randint(100000000,999999999))
payload = {}
#Args 

if len(sys.argv)<4:
    print("Missing arguments")
    print("Usage : python producer.py -topicname -message_rate -n_messages")
    exit(0)

topic = sys.argv[1].strip()
lampda= int(sys.argv[2])
n_mes = int(sys.argv[3])
print("Starting ", sys.argv[0] , " on topic: ",topic)

#Producer instantiation

producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)
#Generate sleep vector
sleeps= generate_poisson(lampda, n_mes+1)

#Producer loop
j=0
target_time=0
print('sleep len: ', len(sleeps))
print('sleep duration', np.sum(sleeps))
payload['type']='init'
payload['id']=producer_id

producer.send(topic, value=json.dumps(payload), timestamp_ms = round(time()*1000))
while j<len(sleeps):
    if(time()*1000>target_time):
        #print('in if')
        payload['type']='data'
        #print(data+str(j))
        producer.send(topic, value=json.dumps(payload), timestamp_ms = round(time()*1000))
        target_time=time()*1000+sleeps[j]*1000
        j+=1
payload['type']='end'
producer.send(topic, value=json.dumps(payload), timestamp_ms = round(time()*1000))
producer.flush()
#print("Time to execute: ", (finish-start)/1000, " sec")
#print("Sleep time : ", slp, " sec")
