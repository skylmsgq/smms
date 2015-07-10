package com.sjtu.userkey;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper{
	
	private static final int DATABASE_VERSION = 1;
	// Database Name
    private static final String DATABASE_NAME = "crud.db";

    public DBHelper(Context context ) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_STUDENT = "CREATE TABLE " + AuthorInfor.TABLE  + "("
                + AuthorInfor.KEY_ID  + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + AuthorInfor.KEY_APIKEY+ " TEXT, "
                + AuthorInfor.KEY_UserID+ " TEXT, "
                 + AuthorInfor.KEY_DeviceID + " TEXT, "
                + AuthorInfor.KEY_ShareKey  + " TEXT )";

        db.execSQL(CREATE_TABLE_STUDENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed, all data will be gone!!!
        db.execSQL("DROP TABLE IF EXISTS " + AuthorInfor.TABLE);

        // Create tables again
        onCreate(db);

    } 

}
