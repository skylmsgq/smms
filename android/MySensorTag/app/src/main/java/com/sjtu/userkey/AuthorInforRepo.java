package com.sjtu.userkey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class AuthorInforRepo {
	private DBHelper dbHelper;
	
	public AuthorInforRepo(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public void Add(AuthorInfor authorInfor){
		//Open connection to write data
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(AuthorInfor.KEY_APIKEY, authorInfor.apikey);
		values.put(AuthorInfor.KEY_UserID, authorInfor.UserId);
		values.put(AuthorInfor.KEY_DeviceID, authorInfor.DeviceId);
		values.put(AuthorInfor.KEY_ShareKey, authorInfor.ShareKey);
		
		//Inserting Raw
		Cursor cursor=db.rawQuery("select * from "+AuthorInfor.TABLE,null);
		if(cursor.getCount()>0){
			db.update(AuthorInfor.TABLE, values,"id=1",null);
		}
		else{
			db.insert(AuthorInfor.TABLE, null, values);
			System.out.println("Insert Key Information Succeed!");}
		db.close();
	}
	
	public void delete(int Id) {
		 
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(AuthorInfor.TABLE, AuthorInfor.KEY_ID+ "= ?", 
        		new String[] { String.valueOf(Id) });
        db.close(); // Closing database connection
    }
 
    public void update(AuthorInfor authorInfor) {
 
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

		values.put(AuthorInfor.KEY_APIKEY, authorInfor.apikey);
		values.put(AuthorInfor.KEY_UserID, authorInfor.UserId);
		values.put(AuthorInfor.KEY_DeviceID, authorInfor.DeviceId);
		values.put(AuthorInfor.KEY_ShareKey, authorInfor.ShareKey);
 
        // It's a good practice to use parameter ?, instead of concatenate string
        db.update(AuthorInfor.TABLE, values, AuthorInfor.KEY_ID + "= ?", 
        		new String[] { String.valueOf(authorInfor.Author_ID) });
        db.close(); // Closing database connection
    }
    
    public AuthorInfor  getAuthor(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "select * from "+AuthorInfor.TABLE+" where id=?";// It's a good practice to use parameter ?, instead of concatenate string

        AuthorInfor authorInfor = new AuthorInfor();
        
        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );
        if (cursor.moveToFirst()) {
            do {
                authorInfor.Author_ID =cursor.getInt(cursor.getColumnIndex(AuthorInfor.KEY_ID));
                authorInfor.apikey = cursor.getString(cursor.getColumnIndex(AuthorInfor.KEY_APIKEY));
                authorInfor.UserId  = cursor.getString(cursor.getColumnIndex(AuthorInfor.KEY_UserID));
                authorInfor.DeviceId = cursor.getString(cursor.getColumnIndex(AuthorInfor.KEY_DeviceID));
                authorInfor.ShareKey= cursor.getString(cursor.getColumnIndex(AuthorInfor.KEY_ShareKey)); 
            } while (cursor.moveToNext());
        }
 
        cursor.close();
        db.close();
        return authorInfor;
    }

}




