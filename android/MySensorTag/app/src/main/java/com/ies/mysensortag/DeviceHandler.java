package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sjtu.user.MainActivity;
import com.sjtu.util.Static;


public class DeviceHandler {
    private static final int REGISTERING = 1;
    private static final int REGIST_FINISH = 2;
    private static final int REGIST_NO = 3;
    private static final int REGIST_NOCON = 4;
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private final static String Tag_ = 
			SJTUServerReport.class.getSimpleName();
	private String postURL;
	private String postData;
	private String userId;
	private String token;
	private String authorization_code;
	private String result = "";
	private ProgressDialog dlg_progress_;
	private Context context;
	public DeviceHandler(Context context) {
		// TODO Auto-generated constructor stub
		this.context=context;
	}
	public HttpPost post_method(String url) {
		HttpPost postRequest = new HttpPost(url);
		
        postRequest.setHeader("X-auth-token", token);
        postRequest.addHeader("X-user-id", userId);
        return postRequest;
	}
	
	public HttpGet get_method(String url) {
		HttpGet getRequest = new HttpGet(url);
		
	    getRequest.setHeader("X-auth-token", token);
	    getRequest.addHeader("X-user-id", userId);
	    
	    return getRequest;
	}
	
	public void post(String url, String data) {
		
		String body = "";
		
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);            
        HttpClient httpclient = new DefaultHttpClient(params);
        mHandler.sendEmptyMessage(REGISTERING);
      
        try {
        	
        	HttpResponse response;
            if (data != null){
            	HttpPost postRequest = post_method(url);
            	postRequest.setEntity(new StringEntity(data));
            	response = httpclient.execute(postRequest);
            }else{
            	HttpGet getRequest = get_method(url);
            	response = httpclient.execute(getRequest);
            } 
            
            
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200 ){
            	System.out.println("just ok.....");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));
                String line = "";
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                Message msg = mHandler.obtainMessage();
                msg.what = REGIST_FINISH;
                msg.obj = body;                
                mHandler.sendMessage(msg);
                Log.i("shit", body);
            }else{
            	Message msg = mHandler.obtainMessage();
                msg.what = REGIST_NO;
                msg.obj = EntityUtils.toString(response.getEntity());
                //String temp=EntityUtils.toString(response.getEntity());
                mHandler.sendMessage(msg);
                Log.e("shit", (String)msg.obj);
            	System.out.println("vist servser failed.......");
            }
            
        }catch (Exception e) {
           // httpclient.getConnectionManager().shutdown();
        	System.out.println("connection failed......");
        	mHandler.sendEmptyMessage(REGIST_NOCON);
        }        
        result = body;	
	}
	
	//Post data
	public void BindDevice(String user_Id,
			String deviceId, String gtoken) {

		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/";

		token = gtoken;
		userId = user_Id;
		
		JSONObject jObject = new JSONObject();
		
		try {
			postData = jObject.put("device_id", deviceId).toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		Log.e("data", jObject.toString());
		DeviceThread reportDataThread = new DeviceThread();
		reportDataThread.start();		
	}
	
	
	public void CheckDevice(String user_Id,
			String deviceId, String gtoken) {
		//To get history data
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" +
				deviceId;
		token = gtoken;
		userId = user_Id;
		postData = null;
		
		DeviceThread getDataThread =new  DeviceThread();
		getDataThread.start();
	}
	
	public void AddDevice(String user_id, String deviceId, 
			String deviceName, String gtoken) {
		postURL = "http://202.121.178.239/api/" + "devices/";
		
		token = gtoken;
		userId = user_id;
		Log.i("token", token);
		Log.i("userId", userId);
		userId = user_id;
		
		JSONObject jObject = new JSONObject();
		
		try {
			jObject.put("device_id", deviceId);
			jObject.put("device_name", deviceName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		postData = jObject.toString();
		
		DeviceThread reportDataThread = new DeviceThread();
		reportDataThread.start();	
	}
	
	public void SearchDevice(String device_id) {
		postURL = "http://202.121.178.239/api/devices/" + device_id;
		token = "85912bf2fa0df64b22318c9ee663791c";
		userId = "b2598cb5daf15ff54f2d2381719007c3";
		
		postData = null;
		
		DeviceThread reportDataThread = new DeviceThread();
		reportDataThread.start();
	}

	public String Paser(String data){
		String mac="";
	       if(data != "" ){
	           JsonParser parser = new JsonParser();  
	           JsonElement jsonEl = parser.parse(data);
	           JsonObject  jobject = jsonEl.getAsJsonObject();
	           mac=jobject.get("device_id").toString();
	        		   
	       }
	       
	       return mac;
	}
	
	public class DeviceThread extends Thread{
		public void run() {
			try{
				Log.i(Tag_, "staring visit server.......");
				System.out.println(postURL);
				post(postURL,postData);
				Log.i(Tag_, "visit server ending........");
			}catch(Exception e){
				
			}
		}	
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ��������
			case REGISTERING:
				dlg_progress_ = ProgressDialog.show(context,
						"Registering", "Registering");
				break;
			case REGIST_FINISH:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}

					//Paser(result);
				break;
			case REGIST_NO:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				
				break;
			case REGIST_NOCON:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				
				break;
			default:
				break;
			}
		};
	};
}
