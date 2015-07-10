package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

public class SJTUServerReport {
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	
    private final static String TAG_ = 
            SJTUServerReport.class.getSimpleName();
    
	private String server_address_;
	private String post_data_;
	private int server_errors_;
	
	public SJTUServerReport() {
		this(DEFAULT_SERVER_ADDRESS);
	}
	
	public SJTUServerReport(String url){
        if (url == null || url.length() == 0) {
            server_address_ = DEFAULT_SERVER_ADDRESS;
        } else {
            server_address_ = url;
        }
	}
    //get url
    public String get_url() {
        URL url;
        try {
            url = new URL(server_address_);
        } catch ( MalformedURLException mue ) {
            System.err.println(mue);
            return null;
        }
        return url.toString();
    }
    
    //Post Data
    protected String post(String url, String data) {
        String body = "";
        
        Log.i(TAG_, "url - " + url+ "\n" +
                "  data - " + data);
       
        HttpParams params = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(params, 3000);
        HttpConnectionParams.setSoTimeout(params, 3000);            
        HttpClient httpclient = new DefaultHttpClient(params);
        
        try {
            HttpPost postRequest = new HttpPost(url);

            postRequest.setEntity(new StringEntity(data));           
            HttpResponse response = httpclient.execute(postRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            
            if(statusCode == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (response.getEntity().getContent())));
                String line = "";
                while ((line = br.readLine()) != null) {
                    body += line;
                }
                Log.i(TAG_, "response: " + body);
                
                server_errors_ = 0;
            }else{
            	//visit database error
            	Log.i(TAG_, "user name has exited!");
            }
            
        } catch (ClientProtocolException cpe) {
            Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
            cpe.printStackTrace();
            server_errors_ ++;
        } catch (HttpHostConnectException hhce) {
            Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
            hhce.printStackTrace();
            server_errors_ ++;
        } catch (IOException e) {
            Log.e(TAG_, "HttpHostConnectException:" + e.toString());
            e.printStackTrace();
            server_errors_ ++;
        } catch (Exception e) { 
            e.printStackTrace();
            server_errors_ ++;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
  
        return body;
    }
}  
