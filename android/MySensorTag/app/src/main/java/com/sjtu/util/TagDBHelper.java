package com.sjtu.util;
/**
 * Created by marvin on 15-1-26.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TagDBHelper extends SQLiteOpenHelper{
	//version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 1;
    private static TagDBHelper sensor;
    // Database Name
    private static final String DATABASE_NAME = "SensorTag.db";
 
    public  TagDBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public static TagDBHelper getsensor(Context context){
    	sensor=new TagDBHelper(context);
    	return sensor;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
 
        String CREATE_TABLE_STUDENT = "CREATE TABLE " + SensorTag.TABLE  + "("
                + SensorTag.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + SensorTag.KEY_username + " TEXT ,"
                + SensorTag.KEY_mac + " TEXT ,"
                + SensorTag.KEY_time + " TEXT , "
                + SensorTag.KEY_Lon + " TEXT , "
                + SensorTag.KEY_Lat + " TEXT ,"
                + SensorTag.KEY_ObjTemp + " TEXT , "
                + SensorTag.KEY_AmbTemp + " TEXT ,"
                + SensorTag.KEY_humidity + " TEXT ,"
                + SensorTag.KEY_pressure + " TEXT )";
 
        db.execSQL(CREATE_TABLE_STUDENT);
 
    }
 
    public void insert(SensorTag sensorTag){ 	
    	//Open connection to write data
    	SQLiteDatabase db =  sensor.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(SensorTag.KEY_mac, sensorTag.mac);
    	values.put(SensorTag.KEY_time, sensorTag.time);
    	values.put(SensorTag.KEY_username, sensorTag.username);
    	values.put(SensorTag.KEY_Lon, sensorTag.longitude);
    	values.put(SensorTag.KEY_Lat, sensorTag.latitude);
    	values.put(SensorTag.KEY_AmbTemp, sensorTag.AmbTemp);
    	values.put(SensorTag.KEY_ObjTemp, sensorTag.ObjTemp);
    	values.put(SensorTag.KEY_humidity, sensorTag.humidity);
    	values.put(SensorTag.KEY_pressure, sensorTag.pressure);
    	
    	//Inserting Row
    	try{
    	      db.insert(SensorTag.TABLE, null, values);
    		  Log.e("shit", "ohh,shit");
    	}catch (Exception e) {
			// TODO: handle exception
    		System.out.println("Insert Data failed!");
    		Log.e("shit", "no!!!!!!!!!!shit");
		}
			

       db.close(); 	
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + SensorTag.TABLE);
 
        // Create tables again
        onCreate(db);
 
    }
}
