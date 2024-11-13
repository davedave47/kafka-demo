# Kafka Demo

## Docker
- **Give permission**
    ```sh
     sudo chmod -R 777 ./kafka-data-1 ./kafka-data-2 ./kafka-data-3
- **Interact with container**
    ```sh
    docker exec -it kafka1
    cd /usr/bin
- **Create topics**
    ```sh
    kafka-topics.sh --create --topic <topic_name> --bootstrap-server <broker_address> --partitions <num_partitions> --replication-factor <rep_factor>
- **Describe topics**
    ```sh
    kafka-topics.sh --describe --topic <topic_name> --bootstrap-server <broker_address>
## Consumer

- **Output Directory:** `Kafka/src/main/resources/output`
- **Start Command:**
    ```sh
    cd Kafka
    mvn exec:java -Dexec.mainClass="org.example.Consumer"

## Producer
- **Start Command:**
    ```sh
    cd Kafka
    mvn exec:java -Dexec.mainClass="org.example.Producer"
