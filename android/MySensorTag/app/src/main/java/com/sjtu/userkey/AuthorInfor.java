package com.sjtu.userkey;

public class AuthorInfor {
	//Labels table name
	public static final String TABLE = "AuthorInfor";
	
	//Labels Table Columns name
	public static final String KEY_ID =  "id";
	public static final String KEY_APIKEY = "apikey";
	public static final String KEY_UserID = "UserId";
	public static final String KEY_DeviceID = "DeviceId";
	public static final String KEY_ShareKey = "ShareKey";
	
	//property help to keep data
	public int Author_ID;
	public String apikey;
	public String UserId;
	public String DeviceId;
	public String ShareKey;	
	
	public void set_apike(String apikey) {
		this.apikey = apikey;		
	}
	
	public void set_UserId(String userId) {
		this.UserId = userId;
	}
	
	public void set_DeviceId(String deviceId) {
		this.DeviceId = deviceId;
	}
	
	public void set_ShareKey(String shareKey) {
		this.ShareKey = shareKey;
	}
}
