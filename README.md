# Docker

## Clean
- For Linux:
    ```sh
    clean.sh

- For Windows:
    ```sh
    clean.bat

## Build
- For Linux:
    ```sh
    build.sh
- For Windows:
    ```sh
    build.bat

## Restart
- For Linux:
    ```sh
    restart.sh
- For Windows:
    ```sh
    restart.bat

# Project

## Start Producer
    ```sh
    mvn exec:java -Dexec.mainClass="org.example.Producer"
    ```

## Start Stream
    ```sh
    `mvn exec:java -Dexec.mainClass="org.example.KafkaStream"`
    ```
## Start Consumer
    ```sh
    `mvn exec:java -Dexec.mainClass="org.example.Consumer"`
    ```
## Output
- Location: `/src/main/resources`