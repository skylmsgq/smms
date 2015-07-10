package com.sjtu.user;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

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
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ies.mysensortag.R;
import com.sjtu.userkey.AuthorInfor;
import com.sjtu.userkey.AuthorInforRepo;
import com.sjtu.util.SqhelperRemember;
import com.sjtu.util.SqhelperUser;
import com.sjtu.util.Static;
import com.sjtu.util.UserData;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Regist extends Activity implements OnClickListener {
    private static final int REGISTERING = 1;
    private static final int REGIST_FINISH = 2;
    private static final int REGIST_NO = 3;
    private static final int REGIST_NOCON = 4;
	private ProgressDialog dlg_progress_;
	private Button reg;
	private EditText name;
	private EditText pass;
	private EditText mail;
	private SqhelperRemember remember;

	private UserData userData = new UserData();
	private AuthorInfor author=new AuthorInfor();
	private AuthorInforRepo authordb=new AuthorInforRepo(this);
	private String post_data_;
	private String token;
	private String user_id;
	public boolean IsIn;
    private final static String TAG_ = 
            Regist.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		init();
	}

	private void init() {
		reg = (Button) findViewById(R.id.reg_reg);
		name = (EditText) findViewById(R.id.reg_idedit);
		pass = (EditText) findViewById(R.id.reg_passedit);
		mail = (EditText) findViewById(R.id.reg_mailedit);
		reg.setOnClickListener(this);
		remember=SqhelperRemember.GetRemember(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reg_reg:
			if (name.getText().toString().length() == 0)
				Toast.makeText(Regist.this,"用户名不得为空",Toast.LENGTH_SHORT).show();
			else {
//				if (pass.getText().toString().length()<5)
//					Toast.makeText(Regist.this,"��������5λ",Toast.LENGTH_SHORT).show();


						userData.set_mail(mail.getText().toString());
						userData.set_password(pass.getText().toString());
						userData.set_username(name.getText().toString());
						userData.set_action("register");
						Gson gson = new Gson(); 
				        post_data_ = gson.toJson(userData);
				        RegUserThread reguser_thread = new RegUserThread();
				        reguser_thread.start();				        												
			}
			break;
		default:
			break;
		}
	}
	 public void paser(String body){
	       if(body != "" ){
	           JsonParser parser = new JsonParser();  
	           JsonElement jsonEl = parser.parse(body);
	           JsonObject  jobject = jsonEl.getAsJsonObject();
	           token = jobject.get("token").toString().substring(1, jobject.get("token").toString().length()-1);
	           user_id = jobject.get("user_id").toString().substring(1, jobject.get("user_id").toString().length()-1);
	       }else{
	       }
	   }
	    public class RegUserThread extends Thread {
	    	String body = new String();
	        public void run() {

	                Log.i(TAG_, "start post...");               
	                HttpParams params = new BasicHttpParams();
	                HttpConnectionParams.setConnectionTimeout(params, 3000);
	                HttpConnectionParams.setSoTimeout(params, 3000);            
	                HttpClient httpclient = new DefaultHttpClient(params);
	                mHandler.sendEmptyMessage(REGISTERING);
	                try {
	                    HttpPost postRequest = new HttpPost("http://202.121.178.239/api/users/");	                               
	                    postRequest.setEntity(new StringEntity(post_data_));           
	                    HttpResponse response = httpclient.execute(postRequest);
	                    int statusCode = response.getStatusLine().getStatusCode();
	                    
	                    if(statusCode == 200){
	                        BufferedReader br = new BufferedReader(new InputStreamReader(
	                                (response.getEntity().getContent())));
	                        String line = "";
	                        while ((line = br.readLine()) != null) {
	                            body += line;
	                        }
	                        Log.i(TAG_, "####response: " + body);
	                        Message msg = mHandler.obtainMessage();
	                        msg.what = REGIST_FINISH;
	                        msg.obj = body;
	                        
	                        mHandler.sendMessage(msg);

	                    }else{
	                    	Message msg = mHandler.obtainMessage();
	                        msg.what = REGIST_NO;
	                        msg.obj = EntityUtils.toString(response.getEntity());
	                        
	                        mHandler.sendMessage(msg);
	                    	//user name has exited
	                    	Log.i(TAG_, EntityUtils.toString(response.getEntity()));
	                    }
	                    
	                } catch (ClientProtocolException cpe) {
	                    Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
	                    mHandler.sendEmptyMessage(REGIST_NOCON);
	                    cpe.printStackTrace();
	                } catch (HttpHostConnectException hhce) {
	                    Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
	                    mHandler.sendEmptyMessage(REGIST_NOCON);
	                    hhce.printStackTrace();
	                } catch (IOException e) {
	                    Log.e(TAG_, "HttpHostConnectException:" + e.toString());
	                    mHandler.sendEmptyMessage(REGIST_NOCON);
	                } catch (Exception e) { 
	                	mHandler.sendEmptyMessage(REGIST_NOCON);
	                } finally {
	                    httpclient.getConnectionManager().shutdown();
	                }
	                	
	                Log.i(TAG_, "End post...");
	        }
	    }
	    private Handler mHandler = new Handler()
	    {
	        public void handleMessage(Message msg)
	        {
	            switch (msg.what)
	            {
	            // ��������
	            case REGISTERING:
	            	dlg_progress_ = ProgressDialog.show(Regist.this, 
	                        "Registering", 
	                        "Registering");
	                break;
	            case REGIST_FINISH:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	paser((String) msg.obj);
	            	author.set_apike(token);
	            	author.set_UserId(user_id);
	            	authordb.Add(author);
	            	Static.usname=name.getText().toString();
					remember.insertRememberdata(name.getText().toString(), "1");
	            	Intent intent = new Intent();
					intent.setClass(Regist.this, MainActivity.class);
					startActivity(intent);
	                break;
	            case REGIST_NO:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	Toast.makeText(Regist.this,(String) msg.obj,Toast.LENGTH_SHORT).show();
	                break;
	            case REGIST_NOCON:
	            	if (dlg_progress_ != null) {
	                    dlg_progress_.dismiss();
	                }
	            	Toast.makeText(Regist.this,"NETWORK NOT GOOD",Toast.LENGTH_SHORT).show();
	                break;
	            default:
	                break;
	            }
	        };
	    };
}
