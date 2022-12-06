# DDS-Project
 DDS-Project2223
Comparison and a performance evaluation of three solutions implementing the publisher-subscriber message pattern.
## Kafka Service
### Start zookeeper and the broker

```bash
cd kafka/zookeeper_kafka
docker-compose up 
```

### Start producer and consumer

```bash
cd kafka/app
docker-compose up --build 
```
### Interactive producer 
The producer is waiting from stdin the number of messages to send

```bash
cd kafka/app
docker-compose --profile interactive up interactive_producer --build -d
```
Execute the consumer separately
```bash
cd kafka/app
docker-compose up --build consumer 
```
The to get the container_id
```bash
docker ps
```
And to attach to the stdin 

```bash
docker attach "container_id"
```
Now insert the number of messages to be sent

### Execution with script
Go into the folder new app and lauch script.sh with the following command


```bash
bash script.sh
```

this will lauch only consumers and not producer
