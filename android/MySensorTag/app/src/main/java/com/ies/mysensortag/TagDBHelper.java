package com.ies.mysensortag;
/**
 * Created by marvin on 15-1-26.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TagDBHelper extends SQLiteOpenHelper{
	//version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 1;
 
    // Database Name
    private static final String DATABASE_NAME = "SensorTag.db";
 
    public  TagDBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here
 
        String CREATE_TABLE_STUDENT = "CREATE TABLE " + SensorTag.TABLE  + "("
                + SensorTag.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + SensorTag.KEY_time + " TEXT , "
                + SensorTag.KEY_Lon + " TEXT , "
                + SensorTag.KEY_Lat + " TEXT ,"
                + SensorTag.KEY_ObjTemp + " TEXT , "
                + SensorTag.KEY_AmbTemp + " TEXT ,"
                + SensorTag.KEY_humidity + " TEXT ,"
                + SensorTag.KEY_pressure + " TEXT ,"
                + SensorTag.KEY_indoor+" TEXT)";
 
        db.execSQL(CREATE_TABLE_STUDENT);
 
    }
 
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + SensorTag.TABLE);
 
        // Create tables again
        onCreate(db);
 
    }
}
