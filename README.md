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

## RabbitMQ Service
A topic exchange is used. So it is important to follow the topic routing key format, see [rabbitmq topic page](https://www.rabbitmq.com/tutorials/tutorial-five-java.html).
### Start the broker
```bash
cd rabbitmq/Broker
docker-compose up --build 
```
### Start the Consumers
```bash
cd rabbitmq/Consumer
./consumer_script.sh
```
Insert the topic a Consumer should be listening on, and the number of consumer to run with this instruction.
### Start the Producer
```bash
cd rabbitmq/Consumer
./consumer_script.sh
```
Insert the topic of the messages to produce, and the number of messages to send.
