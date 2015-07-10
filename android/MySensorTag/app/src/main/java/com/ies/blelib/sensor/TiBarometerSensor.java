package com.ies.blelib.sensor;

import static java.lang.Math.pow;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.provider.ContactsContract.Contacts.Data;


/**
 * Created by steven on 9/3/13.
 */
public class TiBarometerSensor extends TiSensor<Double> {

    public static final String UUID_SERVICE = "f000aa40-0451-4000-b000-000000000000";
    public final String UUID_DATA = "f000aa41-0451-4000-b000-000000000000";
    public final String UUID_CONFIG = "f000aa42-0451-4000-b000-000000000000";
    public final String UUID_CALIBRATION = "f000aa43-0451-4000-b000-000000000000";

    private static final byte[] CALIBRATION_DATA = new byte[] { 2 };

    private final int[] calibration = new int[8];

    @Override
    public String get_name() {
        return "Pressure";
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
		//final double data = get_value();
        return ""+get_value();
	}
	
	@Override
    public void enable(BluetoothGatt gatt, boolean enable) {

        gatt_char_write(gatt, UUID_CONFIG, CALIBRATION_DATA);
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
        gatt_char_read(gatt, UUID_CALIBRATION);
        try {
            Thread.sleep(500);
        } catch (Exception e) {

        }
        super.enable(gatt, enable);
    }
    @Override
    public boolean onCharacteristicRead(BluetoothGattCharacteristic c) {
        super.onCharacteristicRead(c);

        if ( !c.getUuid().toString().equals(UUID_CALIBRATION) )
            return false;

        for (int i=0; i<4; ++i) {
            calibration[i] = TiSensor.shortUnsignedAtOffset(c, i * 2);
            calibration[i+4] = TiSensor.shortSignedAtOffset(c, 8 + i * 2);
        }

        return true;
    }

    @Override
    public Double parse(BluetoothGattCharacteristic c) {
        // c holds the calibration coefficients

         Integer t_r;	// Temperature raw value from sensor
         Integer p_r;	// Pressure raw value from sensor
         Double t_a; 	// Temperature actual value in unit centi degrees celsius
         Double S;	// Interim value in calculation
         Double O;	// Interim value in calculation
         Double p_a; 	// Pressure actual value in unit Pascal.

        t_r = TiSensor.shortSignedAtOffset(c, 0);
        p_r = TiSensor.shortUnsignedAtOffset(c, 2);

        t_a = (100 * (calibration[0] * t_r / pow(2,8) + calibration[1] * pow(2,6))) / pow(2,16);
        S = calibration[2] + calibration[3] * t_r / pow(2,17) + ((calibration[4] * t_r / pow(2,15)) * t_r) / pow(2,19);
        O = calibration[5] * pow(2,14) + calibration[6] * t_r / pow(2,3) + ((calibration[7] * t_r / pow(2,15)) * t_r) / pow(2,4);
        p_a = (S * p_r + O) / pow(2,14);

        return p_a;
        
    }



	

}
