#! /bin/sh
#build the consumer container
docker build ./Producer -t producer
echo "Please digit the name of the topic"
read TOPIC1
echo "Please digit how much producers you want to instantiate"
read N
echo "Please digit how much messages you want to send for each producer"
read M
echo "Please digit the value of the lampda"
read lampda
for (( i = 0; i < $N; i++ ))
do
    docker run -d --network host producer $TOPIC1 $M $lampda #topic numberOfMessages lampda
    #add -d to the command above to not show the consumer running
    echo "Producer number $i running and sending messages on topic $TOPIC1"
    done
