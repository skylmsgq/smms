package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ies.blelib.BeaconScanInfo;
import com.sjtu.userkey.AuthorInfor;
import com.sjtu.userkey.AuthorInforRepo;
import com.sjtu.util.DeviceData;
import com.sjtu.util.SqhelperDevice;
import com.sjtu.util.Static;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableListAdapter extends BaseExpandableListAdapter {
	private final static String TAG_ = "Expand";
	private Context context;
	private List<String> expandableListTitle;
	private HashMap<String, List<String[]>> expandableListDetail;
	private SqhelperDevice devicedb;
	List<String[]> myon;
	List<String[]> other;
	List<String[]> newone;
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private AlertDialog.Builder builder;
	private AlertDialog alertDialog;
	private EditText pop_edit1;
	private Button pop_ok;
	private DeviceData devicedata = new DeviceData();
	private ArrayList<BeaconScanInfo> beacon_list_;
	private AuthorInforRepo authordb;
	private AuthorInfor author;
	DeviceHandler deviceHandler;
	private String postURL;
	private String postURL2;
	private String postData;
	private String userId;
	private String userId2;
	private String token;
	private String token2;
	private String mac;
	private String mac2;
	private String devicename;
	private ProgressDialog dlg_progress_;
    private static final int ADDING = 1;
    private static final int ADD_FINISH = 2;
    private static final int ADD_NO = 3;
    private static final int ADD_NOCON = 4;
    private static final int BINDING = 5;
    private static final int BIND_FINISH =6;
    private static final int BIND_NO = 7;
    private static final int BIND_NOCON = 8;
    private static final int CHECKING = 9;
    private static final int CHECK_FINISH =10;
    private static final int CHECK_NO = 11;
    private static final int CHECK_NOCON = 12;
    private static final int SEARCHING = 13;
    private static final int SEARCH_FINISH =14;
    private static final int SEARCH_NO = 15;
    private static final int SEARCH_NOCON = 16;
    AddThread AddDataThread;
    BindThread BindDataThread;
    Intent device_intent ;
    CheckThread checkDataThread;
    SearchThread SearchDataThread;
	final String[] temp = new String[3];
	public ExpandableListAdapter(Context context) {
		this.context = context;
		devicedb = SqhelperDevice.GetDevice(context);
		beacon_list_ = new ArrayList<BeaconScanInfo>();
		newone = new ArrayList<String[]>();
		myon = new ArrayList<String[]>();
		other = new ArrayList<String[]>();
		authordb = new AuthorInforRepo(context);
		author = authordb.getAuthor(1);
		deviceHandler = new DeviceHandler(context);
		set_detail();
	}

	@Override
	public Object getChild(int listPosition, int expandedListPosition) {
		return this.expandableListDetail.get(
				this.expandableListTitle.get(listPosition)).get(
				expandedListPosition);
	}

	@Override
	public long getChildId(int listPosition, int expandedListPosition) {
		return expandedListPosition;
	}

	@Override
	public View getChildView(int listPosition, final int expandedListPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final String[] expandedListText = (String[]) getChild(listPosition,
				expandedListPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_item, null);
		}
		TextView expand_mac = (TextView) convertView
				.findViewById(R.id.expand_mac);
		TextView expand_devicename = (TextView) convertView
				.findViewById(R.id.expand_devicename);
		TextView expand_rssid = (TextView) convertView
				.findViewById(R.id.expand_rssid);
		ImageView expand_pic=(ImageView)convertView.findViewById(R.id.expand_pic);
		if(expandedListText[2]!=null){
			if(expandedListText[2].length()>0){
			if(Double.parseDouble(expandedListText[2])>-50)
				expand_pic.setImageResource(R.drawable.ic_signal_wifi_4_bar_black_48dp);
			else if(Double.parseDouble(expandedListText[2])>-70)
				expand_pic.setImageResource(R.drawable.ic_signal_wifi_3_bar_black_48dp);
			else if(Double.parseDouble(expandedListText[2])>-90)
				expand_pic.setImageResource(R.drawable.ic_signal_wifi_2_bar_black_48dp);
			else expand_pic.setImageResource(R.drawable.ic_signal_wifi_1_bar_black_48dp);
			}
//			else expand_pic.setVisibility(0);
		}
		expand_devicename.setText(expandedListText[0]);
		expand_devicename.setTextColor(Color.WHITE);
		expand_mac.setTextColor(Color.WHITE);
		expand_rssid.setTextColor(Color.WHITE);
		expand_mac.setText(expandedListText[1]);
		expand_rssid.setText(expandedListText[2]);
		return convertView;
	}

	@Override
	public int getChildrenCount(int listPosition) {
		if (expandableListDetail == null) {
			return 0;
		}
		return this.expandableListDetail.get(
				this.expandableListTitle.get(listPosition)).size();
	}

	@Override
	public Object getGroup(int listPosition) {
		return this.expandableListTitle.get(listPosition);
	}

	@Override
	public int getGroupCount() {
		return this.expandableListTitle.size();
	}
	public void clear() {
		beacon_list_.clear();
		myon.clear();
		other.clear();
		newone.clear();
		notifyDataSetChanged();
	}
	@Override
	public long getGroupId(int listPosition) {
		return listPosition;
	}
	private BeaconScanInfo get_scan_object(String address) {
		for (BeaconScanInfo item : beacon_list_) {
			if (item.get_address().equals(address)) {
				return item;
			}
		}
		return null;
	}
	private void set_detail() {
		expandableListDetail = new HashMap<String, List<String[]>>();
		expandableListDetail.put("新的设备", newone);
		expandableListDetail.put("我的设备", myon);
		expandableListDetail.put("其他设备", other);
		expandableListTitle = new ArrayList<String>(
				expandableListDetail.keySet());
	}
	@Override
	public View getGroupView(int listPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		String listTitle = (String) getGroup(listPosition);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) this.context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.list_group, null);
		}
		TextView listTitleTextView = (TextView) convertView
				.findViewById(R.id.listTitle);
		listTitleTextView.setTypeface(null, Typeface.BOLD);
		listTitleTextView.setText(listTitle);
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int listPosition, int expandedListPosition) {
		return true;
	}

	private OnChildClickListener device_list_click_listener_ = new OnChildClickListener() {

		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			final String[] DeviceDetail = (String[]) getChild(groupPosition,
					childPosition);
            Log.e("shit",getGroup(groupPosition).toString());
            if(getGroup(groupPosition).toString().equals("我的设备")){
                Intent device_intent = new Intent(context,
                        com.ies.mysensortag.DeviceActivity.class);
                device_intent.putExtra(DeviceActivity.BT_DEV_OBJ,
                        get_intent_device(DeviceDetail[1]));
                device_intent.putExtra("name",DeviceDetail[0]);
                device_intent.putExtra("mac", DeviceDetail[1]);
                context.startActivity(device_intent);
            }else if(getGroup(groupPosition).toString().equals("新的设备")){
                set_pop(DeviceDetail);
                alertDialog.show();
            }
			return false;
		}
	};

	public void update_device(BluetoothDevice device, int rssi,
			byte[] scanRecord) {
		BeaconScanInfo bsi = get_scan_object(device.getAddress());
		if (bsi == null) {
			bsi = new BeaconScanInfo(device, device.getName(),
					device.getAddress(), rssi);
			beacon_list_.add(bsi);	
			mac2=bsi.get_address();

                if(devicedb.isResited(author.apikey, bsi.get_address())==0){
                    if(!bsi.get_name().equals(null)){
                        if(bsi.get_name().equals("SensorTag")){
                        CheckDevice(author.UserId, bsi.get_address(), author.apikey);
                    }
                    else{
                        devicedata.set_username("Other");
                        devicedata.set_mac(bsi.get_address());
                        devicedata.set_devicename(bsi.get_name());
                        devicedb.insertDevicedata(devicedata);
                    }}
                }
		}
		else{ 
			beacon_list_.get(beacon_list_.indexOf(bsi)).set_rssi(rssi);
		}

		Log.v(TAG_, "1");
		bsi.is_expired();
		sensor_change();
		notifyDataSetChanged();
	}
	public void refresh_disappeared_device() {
		boolean need_refresh = false;
		for (Iterator<BeaconScanInfo> it = beacon_list_.iterator(); it.hasNext();) {
            BeaconScanInfo bsi = it.next();
            if (bsi.is_expired()) {
                it.remove();
                need_refresh = true;
            }
        }
		if (need_refresh) {
			sensor_change();
			notifyDataSetChanged();
		}
		
	}



	private void sensor_change() {
		newone.clear();
		myon.clear();
		other.clear();
		for (BeaconScanInfo item : beacon_list_) {
			String[] temp2 = new String[3];
			temp2[1] = item.get_address();
			temp2[2] = String.valueOf(item.get_rssi());
			switch (devicedb.isResited(author.apikey, temp2[1])) {
			case 0:
				temp2[0] = item.get_name();
				newone.add(temp2);				
				break;
			case 1:
				temp2[0] = devicedb.get_devicename(temp2[1]);
				myon.add(temp2);
				break;
			case 2:
				temp2[0] = devicedb.get_devicename(temp2[1]);
				other.add(temp2);
				break;
			default:
				break;
			}
		}
	}

	private BluetoothDevice get_intent_device(String mac) {
		for (BeaconScanInfo item : beacon_list_) {
			if (item.get_address().equals(mac))
				return item.get_device();
		}
		return null;
	}

	public void set_pop(final String[] dd) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.popupwindow, null);
		builder = new AlertDialog.Builder(context);
		builder.setView(layout);
		alertDialog = builder.create();
		pop_edit1 = (EditText) layout.findViewById(R.id.pop_edit1);
		pop_edit1.setText(dd[0]);
		pop_ok = (Button) layout.findViewById(R.id.pop_regist);
		pop_ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				devicedata.set_username(author.apikey);
				devicedata.set_mac(dd[1]);
				devicedata.set_devicename(pop_edit1.getText().toString());
				devicedb.insertDevicedata(devicedata);
				mac=dd[1];
				devicename=pop_edit1.getText().toString();
				AddDevice(author.UserId,
						mac,devicename, author.apikey);
			}
		});
	}


	public OnChildClickListener get_item_click_listener() {
		return device_list_click_listener_;
	}
	public class AddThread extends Thread{
		public void run() {
			try{
				HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 3000);
		        HttpConnectionParams.setSoTimeout(params, 3000);            
		        HttpClient httpclient = new DefaultHttpClient(params);
		        mHandler.sendEmptyMessage(ADDING);		      
		        try {
		        	String body="";
		        	HttpResponse response;
		        	HttpPost postRequest = new HttpPost(postURL);		    		
		            postRequest.setHeader("X-auth-token", token);
		            postRequest.addHeader("X-user-id", userId);
	            	postRequest.setEntity(new StringEntity(postData));
	            	response = httpclient.execute(postRequest);		    	    		            		            
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
		                msg.what = ADD_FINISH;
		                msg.obj = body;                
		                mHandler.sendMessage(msg);
		                Log.i("shit", body);
		            }else{
		            	Message msg = mHandler.obtainMessage();
		                msg.what = ADD_NO;
		                msg.obj = EntityUtils.toString(response.getEntity());
		                
		                mHandler.sendMessage(msg);
		                Log.e("shit", (String)msg.obj);
		            	System.out.println("vist servser failed.......");
		            }
		            
		        }catch (Exception e) {
		           // httpclient.getConnectionManager().shutdown();
		        	System.out.println("connection failed......");
		        	mHandler.sendEmptyMessage(ADD_NOCON);
		        }finally {
                    httpclient.getConnectionManager().shutdown();
                }
			}catch(Exception e){				
			}
		}	
	}
	public class BindThread extends Thread{
		public void run() {
			try{
				HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 3000);
		        HttpConnectionParams.setSoTimeout(params, 3000);            
		        HttpClient httpclient = new DefaultHttpClient(params);
		        mHandler.sendEmptyMessage(BINDING);		      
		        try {
		        	String body="";
		        	HttpResponse response;
		        	HttpPost postRequest = new HttpPost(postURL);		    		
		            postRequest.setHeader("X-auth-token", token);
		            postRequest.addHeader("X-user-id", userId);
	            	postRequest.setEntity(new StringEntity(postData));
	            	response = httpclient.execute(postRequest);		    	    		            		            
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
		                msg.what = BIND_FINISH;
		                msg.obj = body;                
		                mHandler.sendMessage(msg);
		                Log.i("shit", body);
		            }else{
		            	Message msg = mHandler.obtainMessage();
		                msg.what = BIND_NO;
		                msg.obj = EntityUtils.toString(response.getEntity());
		                
		                mHandler.sendMessage(msg);
		                Log.e("shit", (String)msg.obj);
		            	System.out.println("vist servser failed.......");
		            }
		            
		        }catch (Exception e) {
		        	System.out.println("connection failed......");
		        	mHandler.sendEmptyMessage(BIND_NOCON);
		        }finally {
                    httpclient.getConnectionManager().shutdown();
                }
			}catch(Exception e){				
			}
        }
	}
				
	
	public void AddDevice(String user_id, String deviceId, 
			String deviceName, String gtoken) {
		postURL = "http://202.121.178.239/api/" + "devices/";
		
		token = gtoken;
		userId = user_id;
		Log.i("token", token);
		Log.i("userId", userId);
		
		JSONObject jObject = new JSONObject();
		
		try {
			jObject.put("device_id", deviceId);
			jObject.put("device_name", deviceName);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		postData = jObject.toString();
		
		AddDataThread = new AddThread();
		AddDataThread.start();	
	}
	public void BindDevice(String user_Id,
			String deviceId, String gtoken) {

		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/";

		token = gtoken;
		userId = user_Id;
		
		JSONObject jObject = new JSONObject();
		
		try {
			jObject.put("device_id", deviceId).toString();
		} catch (Exception e) {
			// TODO: handle exception
		}
        postData =jObject.toString();
                Log.e("data", jObject.toString());
		BindDataThread=new BindThread();
		BindDataThread.start();		
	}
	public void SearchDevice(String device_id) {
		postURL2 = "http://202.121.178.239/api/devices/" + device_id;
		token2 = "85912bf2fa0df64b22318c9ee663791c";
		userId2 = "b2598cb5daf15ff54f2d2381719007c3";	
		SearchDataThread = new SearchThread();
		SearchDataThread.start();
	}
	public class SearchThread extends Thread{
		public void run() {
			try{
				HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 3000);
		        HttpConnectionParams.setSoTimeout(params, 3000);            
		        HttpClient httpclient = new DefaultHttpClient(params);
		        mHandler.sendEmptyMessage(SEARCHING);		      
		        try {
		        	String body="";
		        	HttpResponse response;		        	
		            	HttpGet getRequest = new HttpGet(postURL2);		        		
		        	    getRequest.setHeader("X-auth-token", token2);
		        	    getRequest.addHeader("X-user-id", userId2);
		            	response = httpclient.execute(getRequest); 		        		            		    	    		            		            
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
		                msg.what = SEARCH_FINISH;
		                msg.obj = body;                
		                mHandler.sendMessage(msg);
		                Log.i("shit", body);
		            }else{
		            	Message msg = mHandler.obtainMessage();
		                msg.what = SEARCH_NO;
		                msg.obj = EntityUtils.toString(response.getEntity());
		                
		                mHandler.sendMessage(msg);
		                Log.e("shit","++++"+ (String)msg.obj);
		            	System.out.println("vist servser failed.......");
		            }
		            
		        }catch (Exception e) {
		           // httpclient.getConnectionManager().shutdown();
		        	System.out.println("connection failed......");
		        	mHandler.sendEmptyMessage(SEARCH_NOCON);
		        }finally {
                    httpclient.getConnectionManager().shutdown();
                }
			}catch(Exception e){				
			}
		}	
	}
	public void CheckDevice(String user_Id,
			String deviceId, String gtoken) {
		//To get history data
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" +
				deviceId;
		token = gtoken;
		userId = user_Id;
		
		checkDataThread = new CheckThread();
		checkDataThread.start();
	}
	public class CheckThread extends Thread{
		public void run() {
			try{
				HttpParams params = new BasicHttpParams();
		        HttpConnectionParams.setConnectionTimeout(params, 3000);
		        HttpConnectionParams.setSoTimeout(params, 3000);            
		        HttpClient httpclient = new DefaultHttpClient(params);
		        mHandler.sendEmptyMessage(CHECKING);		      
		        try {
		        	String body="";
		        	HttpResponse response;		        	
		            	HttpGet getRequest = new HttpGet(postURL);		        		
		        	    getRequest.setHeader("X-auth-token", token);
		        	    getRequest.addHeader("X-user-id", userId);
		            	response = httpclient.execute(getRequest); 		        		            		    	    		            		            
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
		                msg.what = CHECK_FINISH;
		                msg.obj = body;                
		                mHandler.sendMessage(msg);
		                Log.i("shit", body);
		            }else{
		            	Message msg = mHandler.obtainMessage();
		                msg.what = CHECK_NO;
		                msg.obj = EntityUtils.toString(response.getEntity());
		                
		                mHandler.sendMessage(msg);
		                Log.e("shit","++++"+ (String)msg.obj);
		            	System.out.println("vist servser failed.......");
		            }
		            
		        }catch (Exception e) {
		           // httpclient.getConnectionManager().shutdown();
		        	System.out.println("connection failed......");
		        	mHandler.sendEmptyMessage(CHECK_NOCON);
		        }finally {
                    httpclient.getConnectionManager().shutdown();
                }
			}catch(Exception e){				
			}
		}	
	}
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case ADDING:
				dlg_progress_ = ProgressDialog.show(context,
						"添加设备", "添加设备信息");
				break;
			case ADD_FINISH:
				AddDataThread.interrupt();
				BindDevice(author.UserId,mac, author.apikey);
				break;
			case ADD_NO:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				AddDataThread.interrupt();
				Toast.makeText(context, (String)msg.obj, Toast.LENGTH_SHORT).show();
				break;
			case ADD_NOCON:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				AddDataThread.interrupt();
				
				break;
			case BINDING:
				break;
			case BIND_FINISH:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				device_intent = new Intent(context,
						com.ies.mysensortag.DeviceActivity.class);
				device_intent.putExtra(DeviceActivity.BT_DEV_OBJ,
						get_intent_device(mac));
				device_intent.putExtra("name",devicename);
				device_intent.putExtra("mac", mac);
				context.startActivity(device_intent);
					//Paser(result);
				break;
			case BIND_NO:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				
				break;
			case BIND_NOCON:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				
				break;
			case CHECKING:
				dlg_progress_ = ProgressDialog.show(context,
						"发现设备", "发现设备");
				break;
			case CHECK_FINISH:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				checkDataThread.interrupt();
				String[] tempdata=Paser((String)msg.obj);
				devicedata.set_username(author.apikey);
				devicedata.set_mac(tempdata[0]);
				devicedata.set_devicename(tempdata[1]);
				devicedb.insertDevicedata(devicedata);
				sensor_change();
				Log.e("Ss", "CHECK_YES");
				notifyDataSetChanged();
					//Paser(result);
				break;
			case CHECK_NO:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				checkDataThread.interrupt();
				SearchDevice(mac2);
				Log.e("Ss", "CHECK_NO");
				break;
			case CHECK_NOCON:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				checkDataThread.interrupt();
				
				break;
			case SEARCHING:
				dlg_progress_ = ProgressDialog.show(context,
						"验证设备", "验证设备");
				break;
			case SEARCH_FINISH:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				SearchDataThread.interrupt();
                devicedata.set_username("Other");
				devicedata.set_mac(Paser((String)msg.obj)[0]);
				devicedata.set_devicename(Paser((String)msg.obj)[1]);
				devicedb.insertDevicedata(devicedata);
				sensor_change();
				notifyDataSetChanged();
				Log.e("Ss", "SEARCH_YES");
				break;
			case SEARCH_NO:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				Log.e("Ss", "SEARCH_NO");
				SearchDataThread.interrupt();
				break;
			case SEARCH_NOCON:
				if (dlg_progress_ != null) {
					dlg_progress_.dismiss();
				}
				SearchDataThread.interrupt();
				
				break;
			default:
				break;
			}
		};
	};
	public String[] Paser(String data){
		String[] mac=new String[2];
	       if(data != "" ){
	           JsonParser parser = new JsonParser();  
	           JsonElement jsonEl = parser.parse(data);
	           JsonObject  jobject = jsonEl.getAsJsonObject();
	           mac[0]=jobject.get("device_id").toString();
	           mac[1]=jobject.get("device_name").toString();
	           mac[0]=mac[0].substring(1, mac[0].length()-1);
	           mac[1]=mac[1].substring(1, mac[1].length()-1);
	       }
	       
	       return mac;
	}
}