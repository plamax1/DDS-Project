#! /bin/bash
#build the producer container

# Info
# USAGE: producer_script.sh -topic -n_producers -poisson_rate -num_msgs -constant_rate

docker build -t rabbitmq_producer:1.0 ./Producer

export topic=$1
export messagesToSend=$4
export poisson=$3
rate=0
export rate=$5

for j in `seq 1 $2`;
    do
    docker run -d --name "Rproducer_$1_$j" --network host  -e topic -e messagesToSend -e poisson -e rate rabbitmq_producer:1.0
    echo "Producer number $j running and sending messages on topic $1"
done