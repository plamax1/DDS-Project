# Tests
For testing the performances of our services we used 2 approaches. 
1: Testing the services with a constante message rate, and a network topology of incrasing complexity
2: Testing the services with a fixed topology and evaluating the the performance with an increasing message rate.
As metrics we are using: 
Latency (ms)
Throughput (msg/s)

## Constant message rate, various topology
We defined a constant message rate for the producer of λ=5000 msg/s
### Simple complexity
2 producers, producing messages on 2 topics, 4 consumers, 2 consumers for each topic
### Medium complexity
4 producers, each one producing a different topic, 8 consumers, 2 for each topic
### High complexity
4 producers, each one producing on a different topic, 24 consumers, each consumer subscripted to different topic
 

## Fixed Topology, increasing message rate
As topology we defined the one of medium complexity, so with 4 producers, each one producing a different topic, 8 consumers, 2 for each topic.

### λ=3000 msg/s

### λ=5000 msg/s

### λ=10000 msg/s