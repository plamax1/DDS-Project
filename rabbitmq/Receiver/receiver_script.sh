#! /bin/bash
#build the receiver container

docker build -t receiver:1.0 .

i=0;
t=0;
while [ $i -le 0 ];
do
    read -p "Define a topic: " topic
    read -p "How many receiver you want to create as a subscriber of $topic? : " createNumber
    export topic=$topic
    for j in `seq 1 $createNumber`;
    do
        ((t=t+1))
        docker run -d --network host -e topic receiver:1.0
        echo "Consumer number $t running and listening on topic $topic"
    done

    read -p "Need more subscribers to generate? (Y/N): " go

    if [ $go == "N" ];
    then 
        ((i=i+1))
    fi
done
