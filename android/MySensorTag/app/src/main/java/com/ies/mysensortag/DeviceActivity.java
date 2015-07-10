package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ies.blelib.sensor.BleSensor;
import com.ies.blelib.sensor.SensorDb;
import com.ies.blelib.sensor.TiBarometerSensor;
import com.ies.blelib.sensor.TiHumiditySensor;
import com.ies.blelib.sensor.TiSensor;
import com.ies.blelib.sensor.TiTemperatureSensor;
import com.sjtu.DataType.ToReport;
import com.sjtu.user.BaiduMap;
import com.sjtu.userkey.AuthorInfor;
import com.sjtu.userkey.AuthorInforRepo;
import com.sjtu.util.GPS_Server;
import com.sjtu.util.PageFragment;
import com.sjtu.util.SensorTag;

public class DeviceActivity extends ActionBarActivity implements OnGetGeoCoderResultListener{
    
    private final static String TAG_ = 
            DeviceActivity.class.getSimpleName();
    
    private final static String STATUS_DISCONNECTED = "Disconnected";
    private final static String STATUS_CONNECTED   = "Connected";
    private final static String STATUS_DISCOVERIED   = "Discoveried";     
    private final static int UI_EVENT_UPDATE_DEVICE = 1;
    private final static int UI_EVENT_UPDATE_RSSI = 2;
    private final static int UI_EVENT_UPDATE_SERVICE = 3;
    private final static int UI_EVENT_UPDATE_SENSOR_VALUE = 4;
    private static final int GETING = 5;
    private static final int GET_FINISH = 6;
    private static final int GET_NO = 7;
    private static final int GET_NOCON = 8;
    private static final int WEATHERING = 9;
    private static final int WEATHER_FINISH = 10;
    private static final int WEATHER_NO = 11;
    private static final int WEATHER_NOCON = 12;
	private static final String DEFAULT_SERVER_ADDRESS = 
			"http://202.121.178.239/api/users/";
	private String postURL;
    public final static String BT_DEV_OBJ = "bt_dev_obj";
    private TextView hum;
    private TextView huan;
    private TextView biao;
    private TextView press;
    private TextView place;
    private ToReport getdata=new ToReport();
	public List<ToReport> DateStoreList = new ArrayList<ToReport>();
    int i=0;
	private AuthorInforRepo authordb;
	private AuthorInfor author;
    private SensorTag sensortag=new SensorTag();
    private SensorDataRepo sensordata;
    private TextView DNtext;
    private String[] temp=new String[4];
    private BluetoothDevice ble_dev_;
    private BluetoothGatt   ble_gatt_;
    private SensorListAdapter sensor_list_adapter_;
    private ProgressDialog dlg_progress_;
    private Context context_;
    private GPS_Server gps;
    private static String status_ = STATUS_DISCONNECTED;
    private String mac;
    private GeoCoder geo;
    private TextView indoor;
    private TextView tvtem;
    private TextView tvtime;
    GetDataThread getDataThread;
    GetWeatherThread getWeatherThread;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    ViewPager pager;
    private Toolbar toolbar;
    private ListView mDrawerList;
    private String titles[] = new String[]{"Sensor Tag1"};
    private String tvdata[]=new String[3];
    private PageFragment pageFragment;
    int model=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.activity_main);
        pageFragment=(PageFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment1);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.main_navdrawer);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_ab_drawer);
        }
        mac=getIntent().getExtras().getString("mac");
        ble_dev_ = getIntent().getExtras().getParcelable(BT_DEV_OBJ);
        drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(drawerToggle);
        String[] values = new String[]{
                this.getResources().getString(R.string.humidity),
                this.getResources().getString(R.string.tempature),
                this.getResources().getString(R.string.pressure),
                this.getResources().getString(R.string.mapview) };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        mDrawerList.setAdapter(adapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        model=0;
                        if(!tvdata[2].equals(null))tvtem.setText(tvdata[2]+"%");
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 1:
                        model=1;
                        if(!tvdata[2].equals(null))tvtem.setText(tvdata[1]+"°");
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 2:
                        model=2;
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                    case 3:
                        Intent newintent = new Intent(DeviceActivity.this, BaiduMap.class);
                        startActivity(newintent);
                        mDrawerLayout.closeDrawer(Gravity.START);
                        break;
                }
            }
        });       
               
        gps=new GPS_Server(this);
        setup_ui();
        if (getIntent().getExtras() == null) {
            Log.e(TAG_, "No bluetooth device selected or found.");
            finish();
        }
        ble_dev_ = getIntent().getExtras().getParcelable(BT_DEV_OBJ);
        context_ = this;
        Log.d(TAG_, "Associate to bluetooth device: " + ble_dev_);
        ble_gatt_ = ble_dev_.connectGatt(this, true, gatt_callback_);
        if (ble_gatt_ == null) {
            Log.e(TAG_, "Fail to connect bluetooth service!");
            finish();
        }
        geo=GeoCoder.newInstance();
        geo.setOnGetGeoCodeResultListener(this);
        if(gps.get_longitude()!=0) {
            geo.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(gps.get_latitude(), gps.get_longitude())));
        }
        dlg_progress_ = ProgressDialog.show(context_,
                "Connect to device",
                "Connecting bluetooth");
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

	public void GetData(String user_Id, String deviceId, String token, int i) {
		String body = "";
		// To get history data
		postURL = DEFAULT_SERVER_ADDRESS + user_Id + "/devices/" + deviceId
				+ "/data/" + "?" + "limit=" + String.valueOf(i);
		getDataThread = new GetDataThread();
		getDataThread.start();
	}
        public BluetoothGatt get_gatt() {
            return ble_gatt_;
        }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG_, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG_, "onPause");

    }

    @Override
    protected void onDestroy() {
        super.onStop();
        Log.d(TAG_, "onDestroy");
        if (ble_gatt_ != null) {
            ble_gatt_.disconnect();
            ble_gatt_.close();
            ble_gatt_ = null;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        if(drawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case android.R.id.home:
                mDrawerLayout.openDrawer(Gravity.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }
    private void setup_ui() {
        huan=(TextView)pageFragment.getView().findViewById(R.id.main_environ);
        DNtext=(TextView)pageFragment.getView().findViewById(R.id.textview_name);
        place=(TextView)pageFragment.getView().findViewById(R.id.main_place);
        indoor=(TextView)pageFragment.getView().findViewById(R.id.main_indoor);
        tvtem=(TextView)pageFragment.getView().findViewById(R.id.main_tvtemp);
        tvtime=(TextView)pageFragment.getView().findViewById(R.id.main_tvtime);
        DNtext.setText(getIntent().getExtras().getString("name"));
        //DNtext.setText("shit");
        getWeatherThread=new GetWeatherThread();
        sensor_list_adapter_ = new SensorListAdapter(this);
        authordb=new AuthorInforRepo(this);
        author=new AuthorInfor();
        sensordata=new SensorDataRepo(this);
        author=authordb.getAuthor(1);


    }

    private Handler ui_event_handler_ = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == UI_EVENT_UPDATE_DEVICE) {

                BluetoothGatt gatt = (BluetoothGatt)msg.obj;
                gatt.discoverServices();

                if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                    dlg_progress_ = null;
                }
                dlg_progress_ = ProgressDialog.show(context_,
                            "Connect to device",
                            "Retrieve bluetooth services");
            } else if (msg.what == UI_EVENT_UPDATE_SERVICE) {
                update_ui_sensors();

                if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();

                }
                GetData(author.UserId, mac, author.apikey, 20);
            }else if (msg.what == UI_EVENT_UPDATE_SENSOR_VALUE) {
                sensor_list_adapter_.notifyDataSetChanged();

                TiSensor<?> sensor = (TiSensor<?>)msg.obj;
                if (sensor instanceof TiHumiditySensor) {
        			Float data = (Float) sensor.get_value();
        			temp[2]=Float.toString(data);
        			i++;
        		}else if (sensor instanceof TiTemperatureSensor) {
                    float[] data = (float[])sensor.get_value();
                    temp[0]=Float.toString(data[0]);
                    temp[1]=Float.toString(data[1]);
                    i++;
                }else if (sensor instanceof TiBarometerSensor) {
                	Double value=((TiBarometerSensor)sensor).get_value();
                	temp[3]=Double.toString(value);
                	i++;
                }
                if(i==30){

        			ToReport toReport = new ToReport();
        			toReport.set_eTemp(temp[0]);
        			toReport.set_humidity(temp[2]);
                    if(gps.get_longitude()!=0) {
                        toReport.set_latitude(String.valueOf(gps.get_latitude()));
                        toReport.set_longitude(String.valueOf(gps.get_longitude()));
                    }
        			toReport.set_pressure(temp[3]);
        			toReport.set_sTemp(temp[1]);
        			toReport.set_time(getStringDate());
        			if(Double.valueOf(temp[0])>8)toReport.set_indoor("1");
        			else toReport.set_indoor("0");
        			sensordata.insert(toReport);
        			DataHandler dataHandler = new DataHandler();
        			dataHandler.upsertParam.add_data(toReport);

        			Log.e("id", author.UserId);
        			dataHandler.PostData(dataHandler.upsertParam,
        					author.UserId,
        					mac,author.apikey);
                	i=0;
                }
                //hum.setText(temp[2]);
                if(model==1){
                    Log.e("temp",temp[0]);
                    huan.setText((temp[0].length()>5? temp[0].substring(0,5):temp[0])+"°");
                    if(Double.parseDouble(temp[0])-8>3) indoor.setText("室内温度");
                    else indoor.setText("室外温度");
                }
                else if(model==0){
                    huan.setText((temp[2].length()>5? temp[2].substring(0,5):temp[2])+"%");
                    if(Double.parseDouble(temp[0])-8>3) indoor.setText("室内湿度");
                    else indoor.setText("室外湿度");
                }
                //else if(model==2)huan.setText(temp[3]+"Pa");


            }else if(msg.what==GETING){
            	dlg_progress_ = ProgressDialog.show(context_,
                        "Getting data",
                        "Getting data");
            }else if(msg.what==GET_FINISH){
            	if (dlg_progress_ != null) {
                    //Paser((String)msg.obj);
                    getDataThread.interrupt();
                    getWeatherThread.start();

                }
            }else if(msg.what==GET_NO){
            	if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                    getDataThread.interrupt();
                }
            }else if(msg.what==GET_NOCON){
            	if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                    getDataThread.interrupt();
                }
            }
            else if(msg.what==WEATHERING){

            }else if(msg.what==WEATHER_FINISH){
                if (dlg_progress_ != null) {
                    dlg_progress_.dismiss();
                    tvdata=Paser2((String)msg.obj);
                    getWeatherThread.interrupt();
                    tvtime.setText("官方数据时间:"+tvdata[0]);
                    if(!tvdata[1].equals(null))tvtem.setText(tvdata[1]+"°");

                }
            }else if(msg.what==WEATHER_NO){
                if (dlg_progress_ != null) {
                    getWeatherThread.interrupt();
                }
            }else if(msg.what==WEATHER_NOCON){
                if (dlg_progress_ != null) {
                    getWeatherThread.interrupt();
                }
            }
        }
    };




    private void update_ui_sensors() {
        if (ble_gatt_ == null) {
            return;
        }
        for (BluetoothGattService service:ble_gatt_.getServices()) {
            BleSensor<?> s =
                    SensorDb.get(service.getUuid().toString().toLowerCase());
            if (s != null) {
                sensor_list_adapter_.add_sensor(s);
                try {
    				Thread.sleep(200);
    			} catch (Exception e) {
    				// TODO: handle exception
    			}
                s.enable(ble_gatt_, true);
            }
        }
    }

    private BluetoothGattCallback gatt_callback_ =
            new BluetoothGattCallback() {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                int newState) {
            Log.i(TAG_, "onConnectionStateChange: " + status + " => " +
                    newState);

            if(newState == BluetoothProfile.STATE_CONNECTED){
                status_ = STATUS_CONNECTED;

                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_DEVICE;
                msg.obj = gatt;
                ui_event_handler_.sendMessage(msg);

            } else if(newState == BluetoothProfile.STATE_DISCONNECTED) {
                status_ = STATUS_DISCONNECTED;
                finish();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG_, "onServicesDiscovered, status: " + status);

            if(status == BluetoothGatt.GATT_SUCCESS) {
                //
                // Update UI
                //
                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_SERVICE;
                ui_event_handler_.sendMessage(msg);
            } else {
            }
        }

        @Override
        public void onCharacteristicRead(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic,
                int status) {
            Log.i(TAG_, "onCharacteristicRead, status: " + status);

            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();

            BleSensor<?> sensor = sensor_list_adapter_.get_sensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicRead(characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(
                BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic) {
            Log.i(TAG_, "onCharacteristicChanged");

            String service_id = characteristic.getService().getUuid().toString();
            String char_id = characteristic.getUuid().toString();

            BleSensor<?> sensor = sensor_list_adapter_.get_sensor(service_id);
            if (sensor != null) {
                sensor.onCharacteristicChanged(characteristic);
                String text = sensor.get_value_string();
                Log.i(TAG_, "value : " + text);

                Message msg = new Message();
                msg.what = UI_EVENT_UPDATE_SENSOR_VALUE;
                msg.obj = sensor;
                ui_event_handler_.sendMessage(msg);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG_, "onCharacteristicWrite: " + status);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG_, "onDescriptorRead: " + status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                int status) {
            Log.i(TAG_, "onDescriptorWrite: " + status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.i(TAG_, "onReliableWriteCompleted: " + status);
        }
    };

    public String getStringDate() {
    	  Date currentTime = new Date();
    	  SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    	  String dateString = formatter.format(currentTime);
    	  return dateString;
    	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(DeviceActivity.this, "no,shit", Toast.LENGTH_LONG)
					.show();
			return;
		}
		place.setText(result.getAddress().substring(0, result.getAddress().indexOf("区")+1));
	}
	public class GetDataThread extends Thread {
    	String body = new String();
        public void run() {

                Log.i(TAG_, "start post...");
                HttpParams params = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(params, 3000);
                HttpConnectionParams.setSoTimeout(params, 3000);
                HttpClient httpclient = new DefaultHttpClient(params);
                ui_event_handler_.sendEmptyMessage(GETING);
                try {
	            	HttpGet getRequest = new HttpGet(postURL);
	        	    getRequest.setHeader("X-auth-token", author.apikey);
	        	    getRequest.addHeader("X-user-id", author.UserId);
	            	HttpResponse response = httpclient.execute(getRequest);
                    int statusCode = response.getStatusLine().getStatusCode();

                    if(statusCode == 200){
                        BufferedReader br = new BufferedReader(new InputStreamReader(
                                (response.getEntity().getContent())));
                        String line = "";
                        while ((line = br.readLine()) != null) {
                            body += line;
                        }
                        Log.i(TAG_, "####response: " + body);
                        Message msg = ui_event_handler_.obtainMessage();
                        msg.what = GET_FINISH;
                        msg.obj = body;

                        ui_event_handler_.sendMessage(msg);

                    }else{
                    	Message msg = ui_event_handler_.obtainMessage();
                        msg.what = GET_NO;
                        msg.obj = EntityUtils.toString(response.getEntity());

                        ui_event_handler_.sendMessage(msg);
                    	//user name has exited
                    	Log.i(TAG_, EntityUtils.toString(response.getEntity()));
                    }

                } catch (ClientProtocolException cpe) {
                    Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
                    ui_event_handler_.sendEmptyMessage(GET_NOCON);
                    cpe.printStackTrace();
                } catch (HttpHostConnectException hhce) {
                    Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
                    ui_event_handler_.sendEmptyMessage(GET_NOCON);
                    hhce.printStackTrace();
                } catch (IOException e) {
                    Log.e(TAG_, "HttpHostConnectException:" + e.toString());
                    ui_event_handler_.sendEmptyMessage(GET_NOCON);
                } catch (Exception e) {
                	ui_event_handler_.sendEmptyMessage(GET_NOCON);
                } finally {
                    httpclient.getConnectionManager().shutdown();
                }

                Log.i(TAG_, "End post...");
        }
    }
    public class GetWeatherThread extends Thread {
        String body = new String();
        public void run() {

            Log.i(TAG_, "start post...");
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 3000);
            HttpClient httpclient = new DefaultHttpClient(params);
            ui_event_handler_.sendEmptyMessage(WEATHERING);
            try {
                HttpGet getRequest = new HttpGet(DEFAULT_SERVER_ADDRESS + author.UserId + "/weather/?limit=1&code=101020200");
                getRequest.setHeader("X-auth-token", author.apikey);
                getRequest.addHeader("X-user-id", author.UserId);
                HttpResponse response = httpclient.execute(getRequest);
                int statusCode = response.getStatusLine().getStatusCode();

                if(statusCode == 200){
                    BufferedReader br = new BufferedReader(new InputStreamReader(
                            (response.getEntity().getContent())));
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        body += line;
                    }
                    Log.i(TAG_, "####response: " + body);
                    Message msg = ui_event_handler_.obtainMessage();
                    msg.what = WEATHER_FINISH;
                    msg.obj = body;

                    ui_event_handler_.sendMessage(msg);

                }else{
                    Message msg = ui_event_handler_.obtainMessage();
                    msg.what = WEATHER_NO;
                    msg.obj = EntityUtils.toString(response.getEntity());

                    ui_event_handler_.sendMessage(msg);
                    Log.i(TAG_, EntityUtils.toString(response.getEntity()));
                }

            } catch (ClientProtocolException cpe) {
                Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
                ui_event_handler_.sendEmptyMessage(WEATHER_NOCON);
                cpe.printStackTrace();
            } catch (HttpHostConnectException hhce) {
                Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
                ui_event_handler_.sendEmptyMessage(WEATHER_NOCON);
                hhce.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG_, "HttpHostConnectException:" + e.toString());
                ui_event_handler_.sendEmptyMessage(WEATHER_NOCON);
            } catch (Exception e) {
                ui_event_handler_.sendEmptyMessage(WEATHER_NOCON);
            } finally {
                httpclient.getConnectionManager().shutdown();
            }

            Log.i(TAG_, "End post...");
        }
    }
	public void Paser(String data) {
		ToReport GetDataStore;
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

				DateStoreList.add(GetDataStore);
			}

			for (ToReport temp : DateStoreList) {
				Log.d("time:", temp.get_time());
				Log.d("humidity:", temp.get_humidity());
			}

		}

	}
    public String[] Paser2(String data) {
        String[] tvdata = new String[3];
        if (data != "") {
            JsonParser parser = new JsonParser();
            JsonElement jsonEl = parser.parse(data);
            JsonArray jaArray = jsonEl.getAsJsonArray();

            for (int i = 0; i < jaArray.size(); i++) {
                JsonObject jobject = jaArray.get(i).getAsJsonObject();
                tvdata[0] = (jobject.get("time").toString().substring(1, jobject.get("time").toString().length() - 1));
                tvdata[1] = (jobject.get("temperature").toString().substring(1, jobject.get("temperature").toString().length() - 1));
                tvdata[2] = (jobject.get("humidity").toString().substring(1, jobject.get("humidity").toString().length() - 1));
            }

        }
        return tvdata;
    }

}
