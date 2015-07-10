package com.sjtu.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SqhelperData extends SQLiteOpenHelper {
	static SqhelperData data;
	public static SqhelperData GetData(Context context){
		data=new SqhelperData(context);
		return data;
	}
	public SqhelperData(Context context) {
		super(context, "data", null, 1);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS data(_id integer primary key,username text,mac text," +
				"time text,longtitude text,latitude text,humidity text,pressure text);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
