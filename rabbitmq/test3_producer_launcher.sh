#! /bin/bash
#USAGE: test3.sh -n_producers -poisson_rate -num_msgs

#generate n_producers on different topics and the same message behaviour. 
#Naming the topics starting from "a".

for i in `seq 1 $1`;
    do
    declare -i v=$((96 + $i))
    #convert letter ASCII number into string
    topicName=$(printf \\$(printf '%03o' $v))
    bash ./producer_script.sh $topicName 1 $2 $3
done

