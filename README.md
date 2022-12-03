# DDS-Project
 DDS-Project2223
Comparison and a performance evaluation of three solutions implementing the publisher-subscriber message pattern.
## Kafka Serice
### Start zookeeper and the broker

```bash
cd kafka/zookeeper_kafka
docker compose up --build -d
```

### Start producer and consumer

```bash
cd kafka/app
docker compose up --build -d
```
