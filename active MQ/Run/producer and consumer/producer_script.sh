#!/usr/bin/bash
#build the producer container

# Info
# USAGE: producer_script.sh -topic -n_producers -poisson_rate -num_msg

docker build -t activemq_producer:1.0 ./Producer

TOPIC1=$1
M=$4
lampda=$3
for j in `seq 1 $2`;
    do
    docker run -d --name "Aproducer_$1_$j" --network host activemq_producer:1.0 $TOPIC1 $M $lampda
    echo "Producer number $j running and producing $M messages on topic $TOPIC1 using a poisson rate of $lampda."
done