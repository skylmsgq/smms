package com.sjtu.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqhelperUser extends SQLiteOpenHelper{
	static SqhelperUser user;
	public SqhelperUser(Context context) {
		super(context, "user", null, 1);
		// TODO Auto-generated constructor stub
	}
	public static SqhelperUser GetUser(Context context){
		user=new SqhelperUser(context);
		return user;
	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("CREATE TABLE IF NOT EXISTS user1" +
				"(_id integer primary key,username text,password text,mail text);");
		
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	public void insertUserdata(UserData data){
		SQLiteDatabase mydb=user.getWritableDatabase();
		ContentValues cv=new ContentValues();
		cv.put("username", data.get_username());
		cv.put("password", data.get_password());
		cv.put("mail", data.get_mail());
		mydb.insert("user1", null, cv);
		mydb.close();
	}

	public boolean IsUserExist(String username){
		boolean exist=false;
		SQLiteDatabase db1=user.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from user1 where username=?", new String[]{username});
        if(cursor.getCount()>0){exist= true;}
        else exist=false;
        cursor.close();
        db1.close();
		return exist;
	}
	public boolean IsPasswordRight(String username,String Password){
		boolean right=false;
		String shit = null;
		SQLiteDatabase db1=user.getReadableDatabase();
		Cursor cursor = db1.rawQuery("select * from user1 where username=?", new String[]{username});
		 while(cursor.moveToNext()){  
			 shit=cursor.getString(cursor.getColumnIndex("password"));
		 }
        if(shit!=null&&shit.equals(Password)){
        		right= true;}
        else right=false;
        cursor.close();
        db1.close();
        return right;
	}
}
