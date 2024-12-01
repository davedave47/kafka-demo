# Docker

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

## Start Producer
- Command: `mvn exec:java -Dexec.mainClass="org.example.Producer"`

## Start KafkaStream
- Command: `mvn exec:java -Dexec.mainClass="org.example.KafkaStream"`

## Start Consumer
- Command: `mvn exec:java -Dexec.mainClass="org.example.Consumer"`

## IO Files
- Location: `/src/main/resources`