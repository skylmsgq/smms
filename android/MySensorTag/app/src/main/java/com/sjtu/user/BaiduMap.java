package com.sjtu.user;





import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.LabeledIntent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.ies.mysensortag.DataHandler;
import com.ies.mysensortag.R;
import com.sjtu.DataType.ToReport;

public class BaiduMap extends Activity {
	private MapView mMapView;
	private InfoWindow mInfoWindow;
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private String userId;
	private String postURL;
	private String key;
	private ToReport data=new ToReport();
	private static final int GETING = 1;
	private static final int GET_FINISH = 2;
	private static final int GET_NO = 3;
	private static final int GET_NOCON = 4;
	private ProgressDialog dlg_progress_;
	BitmapDescriptor bdA;
	com.baidu.mapapi.map.BaiduMap mBaiduMap = null;
	private LatLng ptCenter=new LatLng(36.5,120.5);
	private DataHandler datahand=new DataHandler();
	private List<ToReport> alldata=new ArrayList<ToReport>();
	
	
	
	 @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);
	        SDKInitializer.initialize(getApplicationContext());  
	        mMapView = new MapView(this, new BaiduMapOptions());
	        setContentView(R.layout.baidumap);  
	        mMapView = (MapView) findViewById(R.id.bmapView);
	        mBaiduMap = mMapView.getMap();
	        MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(12.0f);
	        ptCenter=new LatLng(31.083236,121.394725);	       
			mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(ptCenter));			
			mBaiduMap.animateMapStatus(u);
			GetAllData();
			bdA = BitmapDescriptorFactory
					.fromResource(R.drawable.icon_marka);
			mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(final Marker marker) {
					LatLng pt=marker.getPosition();
                    data=getdata(pt);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    LinearLayout show=new LinearLayout(getApplicationContext());
                    show.setOrientation(LinearLayout.VERTICAL);
                    show.setGravity(Gravity.CENTER);
                    show.setLayoutParams(lp);

					TextView title = new TextView(getApplicationContext());
					title.setTextColor(Color.WHITE);
					title.setTextSize(16);
                    LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


                    TextView overtemp = new TextView(getApplicationContext());
                    overtemp.setTextColor(Color.WHITE);
                    overtemp.setTextSize(15);
                    LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    TextView overhum = new TextView(getApplicationContext());
                    overhum.setTextColor(Color.WHITE);
                    overhum.setTextSize(15);
                    LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    TextView overpre = new TextView(getApplicationContext());
                    overpre.setTextColor(Color.WHITE);
                    overpre.setTextSize(15);
                    LinearLayout.LayoutParams lp4 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    if(data!=null)
                    {
                        title.setText(data.get_time());
                        overhum.setText("湿度："+(data.get_humidity().length()>5? data.get_humidity().substring(0,5):data.get_humidity())+" %");
                        overtemp.setText("温度："+(data.get_eTemp().length()>5? data.get_eTemp().substring(0,5):data.get_humidity())+"°C");
                        overpre.setText("气压："+(data.get_pressure().contains(".")? data.get_pressure().substring(0,data.get_pressure().indexOf(".")):data.get_pressure())+"Pa");
                    }
                    show.addView(title,lp1);
                    show.addView(overtemp,lp2);
                    show.addView(overhum,lp3);
                    show.addView(overpre,lp4);
                    show.setBackground(getResources().getDrawable(R.drawable.bt_rounded_bg2));
                    show.setAlpha(0.6f);
                    show.setPadding(10,10,10,10);
                    OnInfoWindowClickListener listener = null;
					listener = new OnInfoWindowClickListener() {
						public void onInfoWindowClick() {
							mBaiduMap.hideInfoWindow();
							}
					};
					mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(show), marker.getPosition(), 225, listener);
					mBaiduMap.showInfoWindow(mInfoWindow);
					return true;
				}

			});
			mBaiduMap.setOnMapClickListener(new OnMapClickListener()  
	        {
	            @Override  
	            public void onMapClick(LatLng arg0)  
	            {  
	                mBaiduMap.hideInfoWindow();  	  
	            }
				@Override
				public boolean onMapPoiClick(MapPoi arg0) {
					return false;
				}  
	        });  
	    }  
	 
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();
	        mMapView.onDestroy();
	    }  
	    @Override  
	    protected void onResume() {  
	        super.onResume();
	        mMapView.onResume();  
	        }  
	    @Override  
	    protected void onPause() {  
	        super.onPause();
	        mMapView.onPause();  
	        }
	    
	    public class GetAllDataThread extends Thread{
			public void run() {
				String body = "";
				
		        HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 5000);
		        HttpConnectionParams.setSoTimeout(params, 5000);            
		        HttpClient httpclient = new DefaultHttpClient(params);
		        mHandler.sendEmptyMessage(GETING);
		      
		        try {
		        		HttpResponse response;	            
		        		HttpGet getRequest = new HttpGet(postURL);		        		
		        	    getRequest.setHeader("X-auth-token", key);
		        	    getRequest.addHeader("X-user-id", userId);
		            	response = httpclient.execute(getRequest);
		            		            
		            int statusCode = response.getStatusLine().getStatusCode();
		            if(statusCode == 200 ){
		            	System.out.println("insert successful.....");
		                BufferedReader br = new BufferedReader(new InputStreamReader(
		                        (response.getEntity().getContent())));
		                String line = "";
		                while ((line = br.readLine()) != null) {
		                    body += line;
		                }
		                Log.e("ss",body);
                        Message msg = mHandler.obtainMessage();
                        msg.what = GET_FINISH;
                        msg.obj = body;
                        mHandler.sendMessage(msg);
		            }else{
		            	System.out.println("vist servser failed.......");
		            	Message msg = mHandler.obtainMessage();
                        msg.what = GET_NO;
                        msg.obj = EntityUtils.toString(response.getEntity());
                        
                        mHandler.sendMessage(msg);
		            	Log.e("ss",EntityUtils.toString(response.getEntity()));
		            }
		            
		        }catch (ClientProtocolException cpe) {
		            Log.e("ss", "ClientProtocolException:" + cpe.toString());
		            mHandler.sendEmptyMessage(GET_NOCON);
		            cpe.printStackTrace();
		        } catch (HttpHostConnectException hhce) {
		            Log.e("ss", "HttpHostConnectException:" + hhce.toString());
		            mHandler.sendEmptyMessage(GET_NOCON);
		            hhce.printStackTrace();
		        } catch (IOException e) {
		            Log.e("ss", "HttpHostConnectException:" + e.toString());
		            mHandler.sendEmptyMessage(GET_NOCON);
		            e.printStackTrace();
		        } catch (Exception e) { 
		        	mHandler.sendEmptyMessage(GET_NOCON);
		            e.printStackTrace();
		        } finally {
		            httpclient.getConnectionManager().shutdown();
		        }
			}	
		}
	    
	    public void GetAllData(){
			key = "85912bf2fa0df64b22318c9ee663791c";
			userId = "b2598cb5daf15ff54f2d2381719007c3";
			postURL = DEFAULT_SERVER_ADDRESS + userId + "/realtimedata/";			
			GetAllDataThread AllDataThread = new GetAllDataThread();
			AllDataThread.start();
		}
	    private Handler mHandler = new Handler()
	    {
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	            case GETING:
	            	dlg_progress_ = ProgressDialog.show(BaiduMap.this, 
	                        "GETTING DATA", 
	                        "GETTING DATA");
	                break;
	            case GET_FINISH:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	alldata=datahand.Paser((String)msg.obj);
	            	for(ToReport item:alldata){
	            		mBaiduMap.addOverlay(new MarkerOptions().position(new LatLng(Double.parseDouble(item.get_latitude()),
	            				Double.parseDouble(item.get_longitude())))
	        					.icon(BitmapDescriptorFactory
	        							.fromResource(R.drawable.icon_map)).perspective(true));
	            	}
	                break;
	            case GET_NO:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	Toast.makeText(BaiduMap.this,"找不到设备信息",Toast.LENGTH_SHORT).show();
	                break;
	            case GET_NOCON:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	Toast.makeText(BaiduMap.this,"NETWORK NOT GOOD",Toast.LENGTH_SHORT).show();
	                break;
	            default:
	                break;
	            }
	        };
	    };
	    private ToReport getdata(LatLng la){
	    	for(ToReport item:alldata){
	    		if((item.get_latitude().length()<String.valueOf(la.latitude).length()? 
	    				item.get_latitude().equals(String.valueOf(la.latitude).substring(0,item.get_latitude().length()))
	    				:item.get_latitude().substring(0, String.valueOf(la.latitude).length()).equals(String.valueOf(la.latitude)))
	    			&&(item.get_longitude().length()<String.valueOf(la.longitude).length()? 
		    				item.get_longitude().equals(String.valueOf(la.longitude).substring(0,item.get_longitude().length()))
		    				:item.get_longitude().substring(0, String.valueOf(la.longitude).length()).equals(String.valueOf(la.longitude)))) return item;
	    	}
	    	return null;
	    }
	    
}
