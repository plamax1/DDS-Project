#! /bin/sh
#build the consumer container
docker build ./consumer -t consumer

for i in {1..5}
do 
    docker run --network host consumer  
    echo "Consumer number $i running"
done