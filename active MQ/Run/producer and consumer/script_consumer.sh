#build the consumer container
docker build ./Consumer -t consumer
echo "Please digit the name of the topic"
read TOPIC1
echo "Please digit how much consumer you want to instantiate"
read N
for (( i = 0; i < $N; i++ ))
do 
    docker run -d --network host consumer $TOPIC1 #add -d to the command above to not show the consumer running
    echo "Consumer number $i running and listening on topic $TOPIC1"
done

#for i in {1..5}
#do 
#    docker run -d --network host  consumer $TOPIC2 
#    echo "Consumer number $i running and listening on topic $TOPIC2"
#done
