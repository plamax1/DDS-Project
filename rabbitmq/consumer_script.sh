#! /bin/bash
#build the consumer container

docker build -t consumer:1.0 ./Consumer

i=0;
t=0;
while [ $i -le 0 ];
do
    read -p "Define a topic: " topic
    read -p "How many consumers you want to create as a subscriber of $topic? : " createNumber
    export topic=$topic
    for j in `seq 1 $createNumber`;
    do
        ((t=t+1))
        docker run -d --network host -e topic consumer:1.0
        echo "Consumer number $t running and listening on topic $topic"
    done

    read -p "Need more consumers to generate? (Y/N): " go

    if [ $go == "N" ];
    then 
        ((i=i+1))
    fi
done
