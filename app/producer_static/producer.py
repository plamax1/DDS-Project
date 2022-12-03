from time import sleep,time
from json import dumps
from kafka import KafkaProducer
producer = KafkaProducer(
    bootstrap_servers=['localhost:9092'],
    value_serializer=lambda x: dumps(x).encode('utf-8')
)
for j in range(9999):
    print("Iteration", j)
    data = {'counter-static': j}
    producer.send('topic_test', value=data, timestamp_ms = time() )
    sleep(0.5)