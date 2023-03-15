#!/usr/bin/bash
# Info
# USAGE: consumer_script.sh -topic -n_consumers

#build the consumer container
docker build ./consumer -t consumer
TOPIC1=$1

for (( i = 0; i < $2; i++ )) 
do 
    docker run --network host -d consumer $1 
    echo "Consumer number $i running and listening on topic $1"
done
