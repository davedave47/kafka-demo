mkdir -p ./air ./water ./earth ./namenode ./datanode
sudo chmod -R 777 ./air ./water ./earth ./namenode ./datanode

docker compose up -d

sleep 10

docker exec -it kafka-air sh -c "kafka-topics --create --topic air --partitions 2 --bootstrap-server 192.168.1.222:29092"
docker exec -it kafka-water sh -c "kafka-topics --create --topic water --partitions 1 --bootstrap-server 192.168.1.222:29093"
docker exec -it kafka-earth sh -c "kafka-topics --create --topic earth --partitions 1 --bootstrap-server 192.168.1.222:29094"

docker exec -it hdfs-namenode hdfs dfs -mkdir -p /spark/output  
docker exec -it hdfs-namenode hdfs dfs -chmod -R 777 /spark/output
