package com.ies.blelib.sensor;

import java.util.HashMap;

public class SensorDb {

    public static HashMap<String, BleSensor> map = new HashMap<String, BleSensor>();
    
    static {
        map.put(TiHumiditySensor.UUID_SERVICE, new TiHumiditySensor());
        map.put(TiTemperatureSensor.UUID_SERVICE, new TiTemperatureSensor());
        map.put(TiBarometerSensor.UUID_SERVICE, new TiBarometerSensor());
        //map.put(TiHumiditySensor.UUID_SERVICE, new TiHumiditySensor());
        //map.put(TiBarometerSensor.UUID_SERVICE, new TiBarometerSensor());
    }
    
    public static final BleSensor get(String uuid) {
        return map.get(uuid.toLowerCase());
    }
}
