package com.ies.blelib.sensor;

import com.google.gson.Gson;

import android.bluetooth.BluetoothGattCharacteristic;
import static java.lang.Math.pow;

public class TiTemperatureSensor extends TiSensor<float[]> {

    public static final String UUID_SERVICE = 
            "f000aa00-0451-4000-b000-000000000000";
    private static final String UUID_DATA = 
            "f000aa01-0451-4000-b000-000000000000";
    private static final String UUID_CONFIG = 
            "f000aa02-0451-4000-b000-000000000000";
    
    @Override
    public String get_name() {
        return "Temperature";
    }

    @Override
    public String get_service_uuid() {
        return UUID_SERVICE;
    }

    @Override
    public String get_data_uuid() {
        return UUID_DATA;
    }

    @Override
    public String get_configure_uuid() {
        return UUID_CONFIG;
    }

    @Override
    public String get_value_string() {
        final float[] data = get_value();
        if (data == null) {
            return "";
        }
        return "ambient="+data[0]+"\ntarget="+data[1];
    }

    @Override
    public float[] parse(BluetoothGattCharacteristic c) {
        /* The IR Temperature sensor produces two measurements;
         * Object ( AKA target or IR) Temperature,
         * and Ambient ( AKA die ) temperature.
         *
         * Both need some conversion, and Object temperature is dependent on 
         * Ambient temperature.
         *
         * They are stored as [ObjLSB, ObjMSB, AmbLSB, AmbMSB] (4 bytes)
         * Which means we need to shift the bytes around to get the correct 
         * values.
         */
        double ambient = extractAmbientTemperature(c);
        double target = extractTargetTemperature(c, ambient);

        return new float[]{(float)ambient, (float)target};
    }

    private static double extractAmbientTemperature(
            BluetoothGattCharacteristic c) {
        int offset = 2;
        return shortUnsignedAtOffset(c, offset) / 128.0;
    }

    private static double extractTargetTemperature(
            BluetoothGattCharacteristic c, double ambient) {
        Integer twoByteValue = shortSignedAtOffset(c, 0);

        double Vobj2 = twoByteValue.doubleValue();
        Vobj2 *= 0.00000015625;

        double Tdie = ambient + 273.15;

        double S0 = 5.593E-14;  // Calibration factor
        double a1 = 1.75E-3;
        double a2 = -1.678E-5;
        double b0 = -2.94E-5;
        double b1 = -5.7E-7;
        double b2 = 4.63E-9;
        double c2 = 13.4;
        double Tref = 298.15;
        double S = S0*(1+a1*(Tdie - Tref)+a2*pow((Tdie - Tref),2));
        double Vos = b0 + b1*(Tdie - Tref) + b2*pow((Tdie - Tref),2);
        double fObj = (Vobj2 - Vos) + c2*pow((Vobj2 - Vos),2);
        double tObj = pow(pow(Tdie,4) + (fObj/S),.25);

        return tObj - 273.15;
    }
}
