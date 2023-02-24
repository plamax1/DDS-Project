#! /bin/sh
# Info
# USAGE: producer_script.sh -topic -n_producers -msg_rate -num_msgs

#build the producer container
docker build ./producer -t producer

for i in {1..$2}
do
    docker run -d --network host  producer $1 $2 $3
    echo "Producer number $i running and sending messages on topic $1"
    done

