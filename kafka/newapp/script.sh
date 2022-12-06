#! /bin/sh
#build the consumer container
docker build ./consumer -t consumer
TOPIC1="topic_test"
TOPIC2="topic_test"
for i in {1..5}
do 
    docker run -d --network host  consumer $TOPIC1 
    echo "Consumer number $i running and listening on topic $TOPIC1"
done

for i in {1..5}
do 
    docker run -d --network host  consumer $TOPIC2 
    echo "Consumer number $i running and listening on topic $TOPIC2"
done