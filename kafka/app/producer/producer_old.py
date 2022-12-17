from time import sleep, time
from json import dumps
from kafka import KafkaProducer
from utils import generate_poisson
import sys
import numpy as np
if len(sys.argv)<4:
    print("Missing topic and number of messages")
    print("Usage : consumer topicname message_rate n_messages")
    exit(0)

topic = sys.argv[1].strip()
lampda= int(sys.argv[2])
n_mes = int(sys.argv[3])
print("Starting ",sys.argv[0] , " on topic:-",topic,"-")
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)
#sleeps= generate_poisson(lampda, n_mes)
sleeps= generate_poisson(lampda, n_mes+1)
print("Sleeps lenght: ", len(sleeps))
print("Sleeps vector sum lenght: ", np.sum(sleeps))
start = round(time()*1000)
slp=0
for j in range(len(sleeps)):
#for j in range(len(sleeps)+1):
#for j in range(100000):
    #print("Iteration", j)
    data = {'counter-live': j}
    producer.send(topic, value=data, timestamp_ms = round(time()*1000))
    try:
        sleep(sleeps[j])
    #sleep(sleeps[j])
        slp+=sleeps[j]
    #    #sleep(0)
    except IndexError:
        print("All messages have been sent")
producer.flush()
finish = round(time()*1000)
print("Time to execute: ", (finish-start)/1000, " sec")
print("Sleep time : ", slp, " sec")