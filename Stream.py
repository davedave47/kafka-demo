from pyspark.sql import SparkSession
from pyspark.sql.functions import udf, col, lit
from Calculate import *
from Environment import *
# Initialize the SparkSession

spark = SparkSession.builder.appName("Stream").getOrCreate()

# Read from Kafka topics
air_stream_in = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "192.168.1.222:29092") \
    .option("subscribe", "air") \
    .option("startingOffsets", "earliest") \
    .load()

water_stream_in = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "192.168.1.222:29093") \
    .option("subscribe", "water") \
    .option("startingOffsets", "earliest") \
    .load()

earth_stream_in = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", "192.168.1.222:29094") \
    .option("subscribe", "earth") \
    .option("startingOffsets", "earliest") \
    .load()

# Deserialize the data
deserialize_air_udf = udf(deserialize_air, air_schema)
deserialize_water_udf = udf(deserialize_water, water_schema)
deserialize_earth_udf = udf(deserialize_earth, earth_schema)

air_stream_in = air_stream_in.withColumn("value", deserialize_air_udf(col("value")))
water_stream_in = water_stream_in.withColumn("value", deserialize_water_udf(col("value")))
earth_stream_in = earth_stream_in.withColumn("value", deserialize_earth_udf(col("value")))

# Add a constant column to group all data into a single group
air_stream_in = air_stream_in.withColumn("group_key", lit(1))
water_stream_in = water_stream_in.withColumn("group_key", lit(1))
earth_stream_in = earth_stream_in.withColumn("group_key", lit(1))

# Impute data using applyInPandas
air_stream_out = air_stream_in.groupBy("group_key").applyInPandas(impute, schema=air_schema)
water_stream_out = water_stream_in.groupBy("group_key").applyInPandas(impute, schema=water_schema)
earth_stream_out = earth_stream_in.groupBy("group_key").applyInPandas(impute, schema=earth_schema)

# Rename columns to include source prefixes
air_stream_out = air_stream_out \
    .withColumnRenamed("station", "air_station") \
    .withColumnRenamed("temperature", "air_temperature") \
    .withColumnRenamed("moisture", "air_moisture") \
    .withColumnRenamed("light", "air_light") \
    .withColumnRenamed("total_rainfall", "air_total_rainfall") \
    .withColumnRenamed("rainfall", "air_rainfall") \
    .withColumnRenamed("wind_direction", "air_wind_direction") \
    .withColumnRenamed("pm25", "air_pm25") \
    .withColumnRenamed("pm10", "air_pm10") \
    .withColumnRenamed("co", "air_co") \
    .withColumnRenamed("nox", "air_nox") \
    .withColumnRenamed("so2", "air_so2")

water_stream_out = water_stream_out \
    .withColumnRenamed("station", "water_station") \
    .withColumnRenamed("ph", "water_ph") \
    .withColumnRenamed("DO", "water_DO") \
    .withColumnRenamed("temperature", "water_temperature") \
    .withColumnRenamed("salinity", "water_salinity")

earth_stream_out = earth_stream_out \
    .withColumnRenamed("station", "earth_station") \
    .withColumnRenamed("temperature", "earth_temperature") \
    .withColumnRenamed("moisture", "earth_moisture") \
    .withColumnRenamed("ph", "earth_ph") \
    .withColumnRenamed("salinity", "earth_salinity") \
    .withColumnRenamed("water_root", "earth_water_root") \
    .withColumnRenamed("water_leaf", "earth_water_leaf") \
    .withColumnRenamed("water_level", "earth_water_level") \
    .withColumnRenamed("voltage", "earth_voltage")

# Optionally, you can join all streams together (if needed)
joined_stream = air_stream_out.join(water_stream_out, on="time", how="inner") \
                              .join(earth_stream_out, on='time', how="inner")

def write_to_hdfs(pdf, epoch_id):
    print("Writing to HDFS...")
    
    if pdf.count() == 0:
        return
    
    pdf = pdf.coalesce(1)
     # Format the data as desired
    formatted_rdd = pdf.rdd.map(format)
    # Convert the formatted RDD to a DataFrame
    formatted_df = formatted_rdd.toDF(StringType())
    
    # Write the formatted DataFrame to HDFS
    formatted_df.write.mode("append").text("hdfs://localhost:8020/spark/output")
        
    
query = joined_stream.writeStream \
    .foreachBatch(write_to_hdfs) \
    .trigger(processingTime = "5 seconds") \
    .start()

query.awaitTermination()
