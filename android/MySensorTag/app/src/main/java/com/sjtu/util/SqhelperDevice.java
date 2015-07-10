package com.sjtu.util;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqhelperDevice extends SQLiteOpenHelper{
	static SqhelperDevice device;
	public static SqhelperDevice GetDevice(Context context){
		device=new SqhelperDevice(context);
		return device;
	}
	public void insertDevicedata(DeviceData data){
		SQLiteDatabase mydb=device.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("username", data.get_username());
		cv.put("devicename", data.get_devicename());
		cv.put("mac", data.get_mac());
		cv.put("password", data.get_pass());
		mydb.insert("device", null, cv);
		mydb.close();
	}
	public List<DeviceData> getDevicedata(String username){
		List<DeviceData> list=new ArrayList<DeviceData>();
		SQLiteDatabase db1=device.getReadableDatabase();
		DeviceData data=new DeviceData();
		Cursor cursor = db1.rawQuery("select * from device where username=?", 
				new String[]{username});
		while(cursor.moveToNext()){  
			 data.set_mac(cursor.getString(cursor.getColumnIndex("mac")));
			 data.set_devicename(cursor.getString(cursor.getColumnIndex("devicename")));
			 list.add(data);
		 }
		cursor.close();
		db1.close();
		return list;
	}
	public String get_mac(String username){
		String temp="nothing";
		SQLiteDatabase db1=device.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from device where username=?", 
				new String[]{username});
		while(cursor.moveToNext()){  
			 temp=cursor.getString(cursor.getColumnIndex("mac"));
		 }
        cursor.close();
    	db1.close();
        return temp;
	}
	public String get_devicename(String mac){
		String temp="nothing";
		SQLiteDatabase db1=device.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from device where mac=?", 
				new String[]{mac});
		while(cursor.moveToNext()){  
			 temp=cursor.getString(cursor.getColumnIndex("devicename"));
		 }
        cursor.close();
    	db1.close();
    	return temp;
	}
	
	public int isResited(String username,String mac){
		int i=0;
		SQLiteDatabase db1=device.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from device where username=?", 
				new String[]{username});
		while(cursor.moveToNext()){  
			 if(mac.equals(cursor.getString(cursor.getColumnIndex("mac")))) {
				 i=1;
				 break;
			 }
		 }
		if(i!=1){
				Cursor cursor2=db1.rawQuery("select * from device where mac=?", 
				new String[]{mac});
				 if(cursor2.getCount()>0) i=2;
				 else i=0;
				 cursor2.close();
		}
		cursor.close();
		db1.close();
		return i;
	}
	public boolean isPasswordRight(String mac,String pass){
		boolean flag=false;
		SQLiteDatabase db1=device.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from device where mac=?", 
				new String[]{mac});
		while(cursor.moveToNext()){  
			 if(pass.equals(cursor.getString(cursor.getColumnIndex("password")))) {
				 flag=true;
				 break;
			 }
		 }
		return flag;
	}
	public SqhelperDevice(Context context) {
		super(context, "device", null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS device" +
				"(_id integer primary key,username text,devicename text,mac text,password text);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
