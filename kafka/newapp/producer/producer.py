from time import sleep, time
from json import dumps
from kafka import KafkaProducer
from utils import generate_poisson
import sys
if len(sys.argv)<4:
    print("Missing topic and number of messages")
    print("Usage : consumer topicname message_rate n_messages")
    exit(0)

topic = sys.argv[1].strip()
lampda= int(sys.argv[2])
n_mes = int(sys.argv[3])
print("Starting producer on topic:-",topic,"-")
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)
sleeps= generate_poisson(lampda, n_mes)
for j in range(len(sleeps)+1):
    print("Iteration", j)
    data = {'counter-live': j}
    producer.send(topic, value=data, timestamp_ms = round(time()*1000))
    producer.flush()
    try:
        sleep(sleeps[j])
    except IndexError:
          print("All messages have been sent")
