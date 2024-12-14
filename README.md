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
    mvn clean install 
- **Download pyspark, pandas, setuptools**
    ```sh
    pip install pyspark setuptools pandas
- **Specify PYSPARK_PYTHON, SPARK_HOME and JAVA_HOME**
    ```sh
    export JAVA_HOME=/path/to/your/jvm
    export SPARK_HOME=$(python -c "import pyspark; print(pyspark.__path__[0])")
    export PYSPARK_PYTHON=/path/to/your/python

## Producer
- **Start Command:**
    ```sh
    mvn exec:java -D exec.mainClass="org.example.Producer"

## Stream
- **Start Command:**
    ```sh
    spark-submit --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.2.1 Stream.py > spark_logs.txt 2>&1

## HDFS
- **Web Interface:**
    > http://localhost:9870
