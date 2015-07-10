package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;







import javax.sql.DataSource;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.R.integer;
import android.R.string;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sjtu.DataType.ToReport;

public class DataHandler {
	
	private final static String Tag_ = 
			SJTUServerReport.class.getSimpleName();
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private String userId;
	private String postURL;
	private String postData;
	private String key;
	private String result = "";
	public List<ToReport> DateStoreList = new ArrayList<ToReport>();
	public  UpsertParam upsertParam = new UpsertParam();

	public HttpPost post_method(String url) {
		HttpPost postRequest = new HttpPost(url);
		
        postRequest.setHeader("X-auth-token", key);
        postRequest.addHeader("X-user-id", userId);

        return postRequest;
	}
	
	public HttpGet get_method(String url) {
		HttpGet getRequest = new HttpGet(url);
		
	    getRequest.setHeader("X-auth-token", key);
	    getRequest.addHeader("X-user-id", userId);
	    
	    return getRequest;
	}
	
	public void post(String url, String data) {
		
		String body = "";
		
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);            
        HttpClient httpclient = new DefaultHttpClient(params);
        
      
        try {
        	
        	HttpResponse response;
            if (data != null){
            	HttpPost postRequest = post_method(url);
            	postRequest.setEntity(new StringEntity(data));
            	System.out.println(data);
            	response = httpclient.execute(postRequest);
            }else{
            	HttpGet getRequest = get_method(url);
            	response = httpclient.execute(getRequest);
            } 
            
            
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == 200 ){
            	System.out.println("insert successful.....");
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));
                String line = "";
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                System.out.println(body);
            }else{
            	System.out.println("vist servser failed.......");
            	Log.e("ss",EntityUtils.toString(response.getEntity()));
            }
            
        }catch (ClientProtocolException cpe) {
            Log.e("ss", "ClientProtocolException:" + cpe.toString());
            cpe.printStackTrace();
        } catch (HttpHostConnectException hhce) {
            Log.e("ss", "HttpHostConnectException:" + hhce.toString());
            hhce.printStackTrace();
        } catch (IOException e) {
            Log.e("ss", "HttpHostConnectException:" + e.toString());
            e.printStackTrace();
        } catch (Exception e) { 
            e.printStackTrace();
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        
        result = body;
		if(result != null & data == null){
			Paser(result);
		}
  //      return  reslut;
	}
	
	//Post data
	public void PostData(UpsertParam data,String user_Id,
			String deviceId, String token) {
		
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" +
		deviceId + "/data/";
		key = token;
		userId = user_Id;
				
		Gson gson = new Gson();
		postData = gson.toJson(data);
		Log.e("DataHandler", postData);       
		ReportDataThread reportDataThread = new ReportDataThread();
		reportDataThread.start();		
	}
	
	//Get data
	public void GetData(String user_Id,
			String deviceId, String token, int i) {

		//To get history data
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" +
				deviceId + "/data/" + "?" + "limit=" + String.valueOf(i);

		key = token;
		userId = user_Id;
		postData = null;
		ReportDataThread getDataThread =new  ReportDataThread();
		getDataThread.start();
	}
	
	public void GetAllData(){
		key = "85912bf2fa0df64b22318c9ee663791c";
		userId = "b2598cb5daf15ff54f2d2381719007c3";
		postURL = DEFAULT_SERVER_ADDRESS + userId + "/realtimedata/";

		postData = null;
		
		ReportDataThread getAllDataThread = new ReportDataThread();
		getAllDataThread.start();
	}
	
	public List<ToReport> Paser(String data){
		   ToReport GetDataStore;
	       if(data != "" ){
	           JsonParser parser = new JsonParser();  
	           JsonElement jsonEl = parser.parse(data);
	           JsonArray  jaArray = jsonEl.getAsJsonArray();
	           
	           for(int i = 0; i < jaArray.size(); i++ ){
	        	   GetDataStore =new ToReport();
		           JsonObject jobject = jaArray.get(i).getAsJsonObject();
		           if(!jobject.get("time").toString().equals("null")&&!jobject.get("s_temperature").toString().equals("null")&&
		        		   !jobject.get("e_temperature").toString().equals("null")&&!jobject.get("latitude").toString().equals("null")
		        		   &&!jobject.get("longitude").toString().equals("null")){
		           GetDataStore.set_time(jobject.get("time").toString().substring(1, jobject.get("time").toString().length()-1));
		           GetDataStore.set_sTemp(jobject.get("s_temperature").toString().substring(1, jobject.get("s_temperature").toString().length()-1));
		           GetDataStore.set_humidity(jobject.get("humidity").toString().substring(1, jobject.get("humidity").toString().length()-1));
		           GetDataStore.set_latitude(jobject.get("latitude").toString().substring(1, jobject.get("latitude").toString().length()-1));
		           GetDataStore.set_longitude(jobject.get("longitude").toString().substring(1, jobject.get("longitude").toString().length()-1));
		           GetDataStore.set_eTemp(jobject.get("e_temperature").toString().substring(1, jobject.get("e_temperature").toString().length()-1));
		           GetDataStore.set_pressure(jobject.get("pressure").toString().substring(1, jobject.get("pressure").toString().length()-1));
		           GetDataStore.set_indoor(jobject.get("indoor").toString().substring(1, jobject.get("indoor").toString().length()-1));
		           DateStoreList.add(GetDataStore);	}	           
	           }

	           for(ToReport temp:DateStoreList){
	        	  Log.d("time:", temp.get_time());
	        	  Log.d("humidity:", temp.get_humidity());
                   Log.d("pressure:", temp.get_pressure());
	           }

	       } 
	       
		return DateStoreList;

    }
	//The thread of posting data
	public class ReportDataThread extends Thread{
		public void run() {
			try{
				Log.i(Tag_, "staring report data.......");
				System.out.println(postURL);
				post(postURL,postData);
				Log.i(Tag_, "visit server ending........");
			}catch(Exception e){
				
			}
		}	
	}
	
	public class UpsertParam {
		public List<ToReport> device_data = new ArrayList<ToReport>();
		
		public void add_data(ToReport data) {
			device_data.add(data);
		}
	}
}
