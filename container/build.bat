@echo off

docker compose down

timeout /t 10 /nobreak

docker exec -it kafka-air sh -c "kafka-topics --create --topic air --partitions 2 --bootstrap-server kafka-air:9092"
docker exec -it kafka-water sh -c "kafka-topics --create --topic water --partitions 1 --bootstrap-server kafka-water:9092"
docker exec -it kafka-earth sh -c "kafka-topics --create --topic earth --partitions 1 --bootstrap-server kafka-earth:9092"

docker exec -it kafka-cloud sh -c "kafka-topics --create --topic environment --partitions 1 --bootstrap-server kafka-cloud:9092"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_air --partitions 1 --bootstrap-server kafka-cloud:9092"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_water --partitions 1 --bootstrap-server kafka-cloud:9092"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_earth --partitions 1 --bootstrap-server kafka-cloud:9092"
