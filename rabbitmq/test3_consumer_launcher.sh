#! /bin/bash
#USAGE: test3_consumer_launcher.sh -n_consumers

#generate n_consumers on different topics 
#Naming the topics starting from "a".

for i in `seq 1 $1`;
    do
    declare -i v=$((96 + $i))
    #convert letter ASCII number into string
    topicName=$(printf \\$(printf '%03o' $v))
    bash ./consumer_script.sh $topicName 1
done

