#! /bin/bash
#build the producer container

docker build -t producer:1.0 .

i=0;
t=0;
while [ $i -le 0 ];
do
    ((t=t+1))
    read -p "Define a topic: " topic
    export topic=$topic
    read -p "How many messages should it send about $topic? : " messagesToSend
    export messagesToSend=$messagesToSend
    read -p "What is the message to send [$topic]? : " messageContent
    export messageContent=$messageContent
    read -p "Wanna use the poisson rate? (N or [int value]) : " poisson
    export poisson=$poisson
    
    docker run -d --network host -e topic -e messagesToSend -e messageContent -e poisson producer:1.0
    echo "Producer number $t running and producing $messagesToSend on topic $topic"
    
    read -p "Need more producer to generate? (Y/N): " go
    if [ $go == "N" ];
    then 
        ((i=i+1))
    fi
done
