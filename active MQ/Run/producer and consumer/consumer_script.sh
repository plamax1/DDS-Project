#build the consumer container

# Info
# USAGE: consumer_script.sh -topic -n_consumer

docker build -t activemq_consumer:1.0 ./Consumer

#export topic=$1
TOPIC1=$1
for j in `seq 1 $2`;
    do
    docker run -d --name "Aconsumer_$1_$j" --network host activemq_consumer:1.0 $TOPIC1
    echo "Consumer number $j running and listening on topic $1"
done