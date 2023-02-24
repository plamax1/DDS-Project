#! /bin/sh
# Info
# USAGE: consumer_script.sh -topic -n_consumers

#build the consumer container
docker build ./consumer -t consumer
TOPIC1=$1

for i in {1..$2}
do 
    docker run --network host  consumer $1 
    echo "Consumer number $i running and listening on topic $1"
done
