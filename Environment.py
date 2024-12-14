import struct
from datetime import datetime
from pyspark.sql import Row
from pyspark.sql.types import StructType, StructField, StringType, FloatType, IntegerType, TimestampType

air_schema = StructType([
    StructField("time", TimestampType(), True),
    StructField("station", StringType(), True),
    StructField("temperature", FloatType(), True),
    StructField("moisture", FloatType(), True),
    StructField("light", IntegerType(), True),
    StructField("total_rainfall", FloatType(), True),
    StructField("rainfall", IntegerType(), True),
    StructField("wind_direction", IntegerType(), True),
    StructField("pm25", FloatType(), True),
    StructField("pm10", FloatType(), True),
    StructField("co", IntegerType(), True),
    StructField("nox", IntegerType(), True),
    StructField("so2", IntegerType(), True)
])

water_schema = StructType([
    StructField("time", TimestampType(), True),
    StructField("station", StringType(), True),
    StructField("ph", FloatType(), True),
    StructField("DO", FloatType(), True),
    StructField("temperature", FloatType(), True),
    StructField("salinity", FloatType(), True)
])

earth_schema = StructType([
    StructField("time", TimestampType(), True),
    StructField("station", StringType(), True),
    StructField("temperature", FloatType(), True),
    StructField("moisture", FloatType(), True),
    StructField("ph", FloatType(), True),
    StructField("salinity", FloatType(), True),
    StructField("water_root", IntegerType(), True),
    StructField("water_leaf", IntegerType(), True),
    StructField("water_level", IntegerType(), True),
    StructField("voltage", FloatType(), True)
])

def deserialize_air(data: bytes):
    try:
        if data is None:
            return None
        
        index = 0
        temperature = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        moisture = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        light = int.from_bytes(data[index:index+4], 'big')
        index += 4
        total_rainfall = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        rainfall = int.from_bytes(data[index:index+4], 'big')
        index += 4
        wind_direction = int.from_bytes(data[index:index+4], 'big')
        index += 4
        pm25 = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        pm10 = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        co = int.from_bytes(data[index:index+4], 'big')
        index += 4
        nox = int.from_bytes(data[index:index+4], 'big')
        index += 4
        so2 = int.from_bytes(data[index:index+4], 'big')
        index += 4

        time_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        time = data[index:index+time_len].decode('utf-8')
        index += time_len

        station_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        station = data[index:index+station_len].decode('utf-8')
        index += station_len

        time = datetime.strptime(time, '%d/%m/%Y %H:%M:%S')
        return Row(time=time, station=station, temperature=temperature, moisture=moisture, light=light, total_rainfall=total_rainfall, rainfall=rainfall, wind_direction=wind_direction, pm25=pm25, pm10=pm10, co=co, nox=nox, so2=so2)
    except Exception as e:
        raise Exception("Error when deserializing environment data.") from e
    
def deserialize_water(data: bytes):
    try:
        if data is None:
            return None
        
        index = 0
        ph = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        DO = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        temperature = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        salinity = struct.unpack('>f', data[index:index+4])[0]
        index += 4

        time_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        time = data[index:index+time_len].decode('utf-8')
        index += time_len

        station_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        station = data[index:index+station_len].decode('utf-8')
        index += station_len

        time = datetime.strptime(time, '%d/%m/%Y %H:%M:%S')
        return Row(time=time, station=station, ph=ph, DO=DO, temperature=temperature, salinity=salinity)
    except Exception as e:
        raise Exception("Error when deserializing environment data.") from e

def deserialize_earth(data: bytes):
    try:
        if data is None:
            return None
        
        index = 0
        temperature = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        moisture = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        ph = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        salinity = struct.unpack('>f', data[index:index+4])[0]
        index += 4
        water_root = int.from_bytes(data[index:index+4], 'big')
        index += 4
        water_leaf = int.from_bytes(data[index:index+4], 'big')
        index += 4
        water_level = int.from_bytes(data[index:index+4], 'big')
        index += 4
        voltage = struct.unpack('>f', data[index:index+4])[0]
        index += 4

        time_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        time = data[index:index+time_len].decode('utf-8')
        index += time_len

        station_len = int.from_bytes(data[index:index+4], 'big')
        index += 4
        station = data[index:index+station_len].decode('utf-8')
        index += station_len

        time = datetime.strptime(time, '%d/%m/%Y %H:%M:%S')
        return Row(time=time, station=station, temperature=temperature, moisture=moisture, ph=ph, salinity=salinity, water_root=water_root, water_leaf=water_leaf, water_level=water_level, voltage=voltage)
    except Exception as e:
        raise Exception("Error when deserializing environment data.") from e