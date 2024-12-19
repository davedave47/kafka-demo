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
## Directory
> cd HDFS
## Compute to Data
```sh
mvn exec:java -Dexec.mainClass="org.example.ComputeToData
```

## Data to Compute
```sh
mvn exec:java -Dexec.mainClass="org.example.DataToCompute
```


## HDFS
- **Web Interface:**
    > http://localhost:9870
