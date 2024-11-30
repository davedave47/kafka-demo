#!/bin/sh

# Bring down the Docker Compose services
docker compose down

# Wait for a few seconds
sleep 10

# Delete contents of ./air, ./water, and ./earth directories
rm -rf ./air/*
rm -rf ./water/*
rm -rf ./earth/*
rm -rf ./cloud/*

# Bring up the Docker Compose services in detached mode
docker compose up -d

sleep 10

# Execute an interactive shell in the kafka-air container
docker exec -it kafka-air sh -c "kafka-topics --create --topic air --partitions 2 --bootstrap-server 192.168.1.222:29092"
docker exec -it kafka-water sh -c "kafka-topics --create --topic water --partitions 1 --bootstrap-server 192.168.1.222:29093"
docker exec -it kafka-earth sh -c "kafka-topics --create --topic earth --partitions 1 --bootstrap-server 192.168.1.222:29094"

docker exec -it kafka-cloud sh -c "kafka-topics --create --topic environment --partitions 1 --bootstrap-server 192.168.1.222:29095"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_air --partitions 1 --bootstrap-server 192.168.1.222:29095"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_water --partitions 1 --bootstrap-server 192.168.1.222:29095"
docker exec -it kafka-cloud sh -c "kafka-topics --create --topic processed_earth --partitions 1 --bootstrap-server 192.168.1.222:29095"


