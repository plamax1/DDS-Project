#!/usr/bin/bash
# Info
# USAGE: producer_script.sh -topic -n_producers -msg_rate -num_msgs

#build the producer container
docker build ./producer -t producer

for (( i = 0; i < $2; i++ ))
do
    docker run -d --network host  producer $1 $3 $4
    echo "Producer number $i running and sending messages on topic $1"
done