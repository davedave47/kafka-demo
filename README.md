# Docker

## Configuration
Please replace `<machine-ip>` with your actual machine IP address in the `docker-compose.yml` file

## Clean
- For Linux: `clean.sh`
- For Windows: `clean.bat`

## Build
- For Linux: `build.sh`
- For Windows: `build.bat`

## Restart
- For Linux: `restart.sh`
- For Windows: `restart.bat`

# Project

## Prerequisite
- **Download dependencies and compile**
    ```sh
    mvn clean install compile

## Producer
- **Start Command:**
    ```sh
    mvn exec:java -Dexec.mainClass="org.example.Producer"

## Stream
- **Start Command:**
    ```sh
    mvn exec:java -Dexec.mainClass="org.example.KafkaStream"

## Consumer
- **Start Command:**
    ```sh
    mvn exec:java -Dexec.mainClass="org.example.Consumer"

## IO Files
- **Source:** `/src/main/resources/Dataset`
- **Output** `src/main/resources/output/output.txt`