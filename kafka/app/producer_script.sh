#! /bin/sh
#build the consumer container
docker build ./producer -t producer
TOPIC1="topic_1"
TOPIC2="topic_2"
TOPIC3="topic_3"
TOPIC4="topic_4"
for i in {1..1}
do
    docker run --network host  producer $TOPIC1 100 1000
    echo "Producer number $i running and sending messages on topic $TOPIC1"
    done


