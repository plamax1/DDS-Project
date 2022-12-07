#! /bin/sh
#build the consumer container
docker build ./producer -t producer
TOPIC1="topic_test"
TOPIC2="topic_test"
for i in {1..2}
do 
    docker run -d --network host  producer $TOPIC1 27 27
    echo "Producer number $i running and listening on topic $TOPIC1"
done

