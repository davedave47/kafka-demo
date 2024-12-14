from math import isnan
from Environment import *
import pandas as pd
import random
from pyspark.sql import Row

MIN_VALUE = -2147483648

def impute(pdf):
    print("Imputing data...")
    new_pdf = pd.json_normalize(pdf["value"])
    columns = list(new_pdf.columns)
    columns.remove("time")
    columns.remove("station")
    for index, row in new_pdf.iterrows():
        for col in columns:
            if isnan(row[col]):
                avg = new_pdf.loc[:index-1, col].mean()
                std = new_pdf.loc[:index-1, col].std()
                new_pdf.at[index, col] = avg + random.uniform(-std, std)
                print("Imputed value for", col, "at", index, ":", new_pdf.at[index, col])
            if row[col] == MIN_VALUE:
                avg = new_pdf.loc[:index-1, col].mean()
                std = new_pdf.loc[:index-1, col].std()
                new_pdf.at[index, col] = round(avg + random.uniform(-std, std))
                print("Imputed value for", col, "at", index, ":", new_pdf.at[index, col])
                
    return new_pdf

def format(row:Row):
    columns = row.asDict().keys()
    
    time=row["time"].strftime("%d/%m/%Y %H:%M:%S")
    air_string = "air={time=" + time
    water_string = "water={time=" + time
    earth_string = "earth={time=" + time
    for col in columns:
        if col.startswith("air_"):
            if isinstance(row[col], float):
                air_string += f", {col[4:]}={row[col]:.1f}"
            else:
                air_string += f", {col[4:]}={row[col]}"
        elif col.startswith("water_"):
            if isinstance(row[col], float):
                water_string += f", {col[6:]}={row[col]:.1f}"
            else:
                water_string += f", {col[6:]}={row[col]}"
        elif col.startswith("earth_"):
            if isinstance(row[col], float):
                earth_string += f", {col[6:]}={row[col]:.1f}"
            else:
                earth_string += f", {col[6:]}={row[col]}"
    
    air_string += "}"
    water_string += "}"
    earth_string += "}"
    
    return air_string + ", " + water_string + ", " + earth_string
                
    
                