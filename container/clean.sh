#!/bin/sh

# Bring down the Docker Compose services
docker compose down

# Wait for a few seconds
sleep 3

# Delete ./air, ./water, and ./earth directories

rm -rf ./air
rm -rf ./water
rm -rf ./earth
rm -rf ./cloud

