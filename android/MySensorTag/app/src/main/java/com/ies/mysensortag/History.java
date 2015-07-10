package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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

import com.baidu.mapapi.map.BitmapDescriptor;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ies.mysensortag.DataHandler.ReportDataThread;
import com.sjtu.DataType.ToReport;
import com.sjtu.userkey.AuthorInfor;
import com.sjtu.userkey.AuthorInforRepo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class History extends Activity {
	private AuthorInforRepo authordb;
	private AuthorInfor author;
	private ToReport getdata=new ToReport();
	public List<ToReport> DateStoreList = new ArrayList<ToReport>();
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private String postURL;
	private String mac;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history);
		mac=getIntent().getExtras().getString("mac");
		authordb = new AuthorInforRepo(this);
		author = authordb.getAuthor(1);
		Log.e("shit", mac+"\n"+author.UserId+"\n"+author.apikey);
		GetData(author.UserId,mac, author.apikey, 3);
		
	}

	public class GetDataThread extends Thread {
		public void run() {
			try {
				post(postURL);
			} catch (Exception e) {

			}
		}
	}

	public HttpGet get_method(String url) {
		HttpGet getRequest = new HttpGet(url);

		getRequest.setHeader("X-auth-token",author.apikey);
		getRequest.addHeader("X-user-id", author.UserId);

		return getRequest;
	}

	public void GetData(String user_Id, String deviceId, String token, int i) {
		String body = "";
		// To get history data
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" + deviceId
				+ "/data/" + "?" + "limit=" + String.valueOf(i);
		GetDataThread getDataThread = new GetDataThread();
		getDataThread.start();
	}

	public void post(String url) {

		String body = "";
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 3000);
		HttpClient httpclient = new DefaultHttpClient(params);

		try {

				HttpResponse response;			
				HttpGet getRequest = get_method(url);
				response = httpclient.execute(getRequest);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == 200) {
				System.out.println("insert successful.....");
				BufferedReader br = new BufferedReader(new InputStreamReader(
						(response.getEntity().getContent())));
				String line = "";
				while ((line = br.readLine()) != null) {
					body += line;
				}
				Log.e("shit", body);
			} else {
				System.out.println("vist servser failed.......");
				Log.e("ss", EntityUtils.toString(response.getEntity()));
			}

		} catch (ClientProtocolException cpe) {
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

			DateStoreList=Paser(body);
		// return reslut;
	}

	public List<ToReport> Paser(String data) {
		ToReport GetDataStore;
		List<ToReport> tempList = new ArrayList<ToReport>();
		if (data != "") {
			JsonParser parser = new JsonParser();
			JsonElement jsonEl = parser.parse(data);
			JsonArray jaArray = jsonEl.getAsJsonArray();

			for (int i = 0; i < jaArray.size(); i++) {
				JsonObject jobject = jaArray.get(i).getAsJsonObject();
					GetDataStore = new ToReport();
		           GetDataStore.set_time(jobject.get("time").toString().substring(1, jobject.get("time").toString().length()-1));
		           GetDataStore.set_sTemp(jobject.get("s_temperature").toString().substring(1, jobject.get("s_temperature").toString().length()-1));
		           GetDataStore.set_humidity(jobject.get("humidity").toString().substring(1, jobject.get("humidity").toString().length()-1));
		           GetDataStore.set_latitude(jobject.get("latitude").toString().substring(1, jobject.get("latitude").toString().length()-1));
		           GetDataStore.set_longitude(jobject.get("longitude").toString().substring(1, jobject.get("longitude").toString().length()-1));
		           GetDataStore.set_eTemp(jobject.get("e_temperature").toString().substring(1, jobject.get("e_temperature").toString().length()-1));
		           GetDataStore.set_pressure(jobject.get("pressure").toString().substring(1, jobject.get("pressure").toString().length()-1));
		           GetDataStore.set_pressure(jobject.get("indoor").toString().substring(1, jobject.get("indoor").toString().length()-1));

				tempList.add(GetDataStore);
			}
			
			for (ToReport temp : DateStoreList) {
				Log.d("time:", temp.get_time());
				Log.d("humidity:", temp.get_humidity());
			}

		}

		return tempList;

	}
}
