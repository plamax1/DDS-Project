#! /bin/sh
#build the consumer container
docker build ./producer -t producer
TOPIC1="topic_test"
TOPIC2="topic_test"
for i in {1..2}
do
    docker run -d --network host  producer $TOPIC1 1000 1000
    echo "Producer number $i running and sending messages on topic $TOPIC1"
    done