#!/usr/bin/bash
#build the consumer container
docker build ./Consumer -t consumer
read -p "Please digit the name of the topic" TOPIC1
read -p "Please digit how much consumer you want to instantiate" N
for (( i = 0; i < $N; i++ ))
do 
    docker run -d --network host -e TOPIC1 consumer #add -d to the command above to not show the consumer running
    echo "Consumer number $i running and listening on topic $TOPIC1"
done

#for i in {1..5}
#do 
#    docker run -d --network host  consumer $TOPIC2 
#    echo "Consumer number $i running and listening on topic $TOPIC2"
#done