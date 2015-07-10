package com.ies.mysensortag;

/**
 * Created by marvin on 15-1-26.
 */
import java.util.ArrayList;
import java.util.HashMap;

import com.sjtu.DataType.ToReport;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SensorDataRepo {
    private TagDBHelper tagDBHelper;
    
    //Create SensorTag table
    public SensorDataRepo(Context context){
    	tagDBHelper = new TagDBHelper(context);
    }
    
    //Insert Information
    public void insert(ToReport sensorTag){ 	
    	//Open connection to write data
    	SQLiteDatabase db =  tagDBHelper.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	values.put(SensorTag.KEY_time, sensorTag.get_time());
    	values.put(SensorTag.KEY_Lon, sensorTag.get_longitude());
    	values.put(SensorTag.KEY_Lat, sensorTag.get_latitude());
    	values.put(SensorTag.KEY_AmbTemp, sensorTag.get_eTemp());
    	values.put(SensorTag.KEY_ObjTemp, sensorTag.get_sTemp());
    	values.put(SensorTag.KEY_humidity, sensorTag.get_humidity());
    	values.put(SensorTag.KEY_pressure, sensorTag.get_pressure());
    	values.put(SensorTag.KEY_indoor, sensorTag.get_indoor());
    	//Inserting Row
    	try{
    	      db.insert(SensorTag.TABLE, null, values);
    		   System.out.println("Insert Information success!");
    	}catch (Exception e) {
			// TODO: handle exception
    		System.out.println("Insert Data failed!");
		}
			

       db.close(); 	
    }
    
    //Delete Information
    public void delete(int SensorTag_id) {
    	
		SQLiteDatabase db = tagDBHelper.getWritableDatabase();
		//Delete SensorTag Data  by SensorTag Id
		db.delete(SensorTag.TABLE, SensorTag.KEY_ID + "=  ?", 
				new String [ ] { String.valueOf(SensorTag_id)});
		
		db.close();
	}
    
    //Update SensorTag table
    public void update(SensorTag sensorTag) {
    	//Open connection to write data
    	SQLiteDatabase db =  tagDBHelper.getWritableDatabase();
    	ContentValues values = new ContentValues();
    	
    	values.put(SensorTag.KEY_time, sensorTag.time);
    	values.put(SensorTag.KEY_Lon, sensorTag.longitude);
    	values.put(SensorTag.KEY_Lat, sensorTag.latitude);
    	values.put(SensorTag.KEY_AmbTemp, sensorTag.AmbTemp);
    	values.put(SensorTag.KEY_ObjTemp, sensorTag.ObjTemp);
    	values.put(SensorTag.KEY_humidity, sensorTag.humidity);
    	values.put(SensorTag.KEY_pressure, sensorTag.pressure);
    	values.put(SensorTag.KEY_indoor, sensorTag.indoor);
       //Update Raw
       db.update(SensorTag.TABLE, values, SensorTag.KEY_ID + "= ?", 
    		   new String [ ] {String.valueOf(sensorTag.SensorTag_ID)});
       //Close database connection 
       db.close();			
	}
    
    //Get SensorTag  Data by Id
    public SensorTag getSensorDataById(int Id) {
    	SQLiteDatabase db = tagDBHelper.getReadableDatabase();
    	
    	String selectQuery =  "SELECT * FROM "+ SensorTag.TABLE +
    	" WHERE " +  SensorTag.KEY_ID + "= ?";
		
    	SensorTag sensorTag = new SensorTag();
    	
    	Cursor cursor = db.rawQuery(selectQuery, 
    			new String [ ] {String.valueOf(Id)});
    	
    	if(cursor.moveToFirst()){
    		do{
    			sensorTag.SensorTag_ID = cursor.getInt(
    					cursor.getColumnIndex(SensorTag.KEY_ID));
    			sensorTag.time = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_time));
    			sensorTag.longitude = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_Lon));
    			sensorTag.latitude = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_Lat));
    			sensorTag.AmbTemp = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_AmbTemp));
    			sensorTag.ObjTemp = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_ObjTemp));
    			sensorTag.humidity = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_humidity));
    			sensorTag.pressure = cursor.getString(
    					cursor.getColumnIndex(SensorTag.KEY_pressure));
    		}while (cursor.moveToNext());
    	}
    	
    	cursor.close();
    	db.close();
    	return sensorTag;	
	}
    
    public ArrayList<HashMap<String, String>>  getAllhunidity() {
    	
    	SQLiteDatabase db = tagDBHelper.getReadableDatabase();
        //Open connection to read only
    	String selectQuery =  "SELECT " + 
    	SensorTag.KEY_ID + "," +
    	SensorTag.KEY_time + ", " + 
    	SensorTag.KEY_Lon + "," +
    	SensorTag.KEY_Lat + "," +
    	SensorTag.KEY_AmbTemp + "," +
    	SensorTag.KEY_ObjTemp + "," +
    	SensorTag.KEY_humidity + "," +
    	SensorTag.KEY_pressure + " ," +
    	"FROM " + SensorTag.TABLE;
 
        //Student student = new Student();
        ArrayList<HashMap<String, String>> HumiList = new ArrayList<HashMap<String, String>>();
 
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
 
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> student = new HashMap<String, String>();
                student.put(SensorTag.KEY_ID, cursor.getString(
                		cursor.getColumnIndex(SensorTag.KEY_ID)));
                student.put(SensorTag.KEY_humidity, cursor.getString(
                		cursor.getColumnIndex(SensorTag.KEY_humidity)));
               HumiList.add(student);
 
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close();
        return HumiList;
 
    }
}
