# DDS-Project
 DDS-Project2223
Comparison and a performance evaluation of three solutions implementing the publisher-subscriber message pattern.
## ActiveMQ Service
### Start ActiveMQ Background
```bash
cd active\ MQ/Run/docker\ active\ MQ
docker-compose up 
```
### Consumer
It is recommended to instatiate first the consumers, then the producers.

```bash
cd active\ MQ/Run/producer\ and\ consumer
bash script_consumer.sh
```
Then follow the instructions on the command line.
### Producer
```bash
cd active\ MQ/Run/producer\ and\ consumer
bash script_producer.sh
```
Then follow the instructions on the command line.



## Kafka Service
### Start Kafka Background

```bash
cd kafka/zookeeper_kafka
docker-compose up 
```

### Producer
You can start n_producers sending messages on the same topic

```bash
cd kafka/app
bash producer_script.sh -topic -n_producers -msg_rate -num_msgs
```
### Consumer
You can start n_consumers sending listening on the same topic

```bash
cd kafka/app
bash consumer_script.sh -topic -n_consumers
```
If you want to send/listen messages on more than one topic, simply execute the script more than once.

## RabbitMQ Service
A topic exchange is used. So it is important to follow the topic routing key format, see [rabbitmq topic page](https://www.rabbitmq.com/tutorials/tutorial-five-java.html).
### Start the broker
```bash
cd rabbitmq/Broker
docker-compose up --build 
```
### Start the Consumers
```bash
cd rabbitmq
bash consumer_script.sh -topic -n_consumer
```
Insert the topic a Consumer should be listening on, and the number of consumer to run with this instruction.
After creation, consumers remains active and listening.
Consumers calculate statistics for each producer from whom they receive messages.
### Start the Producers
```bash
cd rabbitmq
bash producer_script.sh -producer_script.sh -topic -n_producers -msg_rate -num_msgs
```
