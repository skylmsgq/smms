package com.sjtu.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqhelperRemember extends SQLiteOpenHelper{
	static SqhelperRemember remember;
	public SqhelperRemember(Context context) {
		super(context, "remember", null, 1);
		// TODO Auto-generated constructor stub
	}
	public static SqhelperRemember GetRemember(Context context){
		remember=new SqhelperRemember(context);
		return remember;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS remember" +
				"(_id integer primary key,username text,remember int);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	public void insertRememberdata(String username,String check){
		SQLiteDatabase mydb= remember.getWritableDatabase();
		
		ContentValues cv=new ContentValues();
		cv.put("username", username);
		cv.put("remember", check);
		Cursor cursor=mydb.rawQuery("select * from remember",null);
		if(cursor.getCount()>0){
		mydb.update("remember", cv,"_id=1",null);}
		else mydb.insert("remember", null, cv);
		cursor.close();
		mydb.close();
	}
	public boolean isRemember(){
		boolean rem;
		SQLiteDatabase db1=remember.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from remember where remember=?", 
				new String[]{"1"});
		if(cursor.getCount()>0) rem=true;
		else rem=false;
		cursor.close();
		db1.close();
		return rem;
	}
	public String get_username(){
		String temp="nothing";
		SQLiteDatabase db1=remember.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from remember where remember=?", 
				new String[]{"1"});
		while(cursor.moveToNext()){  
			 temp=cursor.getString(cursor.getColumnIndex("username"));
		 }
        cursor.close();
    	db1.close();
        return temp;
	}

}
