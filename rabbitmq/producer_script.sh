#! /bin/bash
#build the producer container

# Info
# USAGE: producer_script.sh -topic -n_producers -poisson_rate -num_msgs -constant_rate


docker build -t producer:1.0 ./Producer

export topic=$1
export messagesToSend=$4
export poisson=$3
rate=0
export rate=$5

for j in `seq 1 $2`;
    do
    docker run -d --name "producer_$1_$j" --network host  -e topic -e messagesToSend -e poisson -e rate producer:1.0
    echo "Producer number $j running and producing $messagesToSend messages on topic $topic using a poisson rate of $poisson. Constant rate is set to [ $rate ]"
done