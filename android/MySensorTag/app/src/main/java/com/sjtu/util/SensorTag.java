package com.sjtu.util;
/**
 * Created by marvin on 15-1-26.
 */
public class SensorTag {
    //Labels table name
    public static final String TABLE = "SensorTag";

    // Labels Table Columns names
    public  static final String  KEY_ID =  "id";
    public static final String KEY_username="username";
    public static final String KEY_mac= "mac";
    public static final String KEY_time = "time";
    public static final String KEY_Lon = "longitude";
    public static final String KEY_Lat = "latitude";
    public static final String KEY_ObjTemp = "ObjTemp";
    public static final String KEY_AmbTemp = "AmbTemp";
    public static final String KEY_humidity = "humidity";
    public static final String KEY_pressure = "pressure";

    // property help us to keep data
    public int SensorTag_ID;
    public String username;
    public String mac;
    public String time;
    public String longitude;
    public String latitude;
    public String ObjTemp;
    public String AmbTemp;
    public String humidity;
    public String pressure;
    
}