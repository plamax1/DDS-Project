from time import sleep, time
from json import dumps
from kafka import KafkaProducer
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)
for j in range(200):
    print("Iteration", j)
    data = {'counter-live': j}
    producer.send('topic_test', value=data, timestamp_ms = round(time()*1000))
    producer.flush()
    sleep(0.1)