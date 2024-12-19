docker compose up -d

sleep 5

docker cp ./HDFS/ProcessFileServer.jar hdfs-datanode1:/hadoop/dfs/data/ProcessFileServer.jar
docker cp ./HDFS/ProcessFileServer.jar hdfs-datanode2:/hadoop/dfs/data/ProcessFileServer.jar
docker cp ./HDFS/ProcessFileServer.jar hdfs-datanode3:/hadoop/dfs/data/ProcessFileServer.jar

docker exec hdfs-datanode1 bash -c "java -cp /hadoop/dfs/data/ProcessFileServer.jar org.example.rmi.ProcessFileServer >> /hadoop/dfs/data/datanode1.log 2>&1 &"
docker exec hdfs-datanode2 bash -c "java -cp /hadoop/dfs/data/ProcessFileServer.jar org.example.rmi.ProcessFileServer >> /hadoop/dfs/data/datanode2.log 2>&1 &"
docker exec hdfs-datanode3 bash -c "java -cp /hadoop/dfs/data/ProcessFileServer.jar org.example.rmi.ProcessFileServer >> /hadoop/dfs/data/datanode3.log 2>&1 &"

docker cp ./input.txt hdfs-namenode:/hadoop/dfs/name/input.txt

docker exec hdfs-namenode bash -c "hdfs dfs -mkdir -p /data"
docker exec hdfs-namenode bash -c "hdfs dfs -put /hadoop/dfs/name/input.txt /data/input.txt"

sudo chmod -R 777 ./datanode1 ./datanode2 ./datanode3 ./namenode