package com.sjtu.user;

import java.io.IOException;

import org.apache.http.HttpEntity;
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

import com.ies.mysensortag.R;
import com.sjtu.util.UpdateManager;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

public class Welcome extends Activity {
	private static final int CHECKING = 1;
	private static final int CHECKING_OK = 2;
	private static final int CHECKING_NO = 3;
	int sversion = 0;
	ImageButton welcome;
	boolean ready = false;
	int i;
	UpdateManager up = new UpdateManager(this);
	private final static String TAG_ = Welcome.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcom);
		i = 0;
		welcome = (ImageButton) findViewById(R.id.Welcom);
		new Thread() {
			@Override
			public void run() {
				HttpParams params = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(params, 2000);
				HttpConnectionParams.setSoTimeout(params, 2000);
				HttpClient httpclient = new DefaultHttpClient(params);
				try {
					HttpGet httpgets = new HttpGet(
							"http://wxtestshit-pic.stor.sinaapp.com/index2.php");
					HttpResponse res = null;
					res = httpclient.execute(httpgets);
					HttpEntity entity = res.getEntity();
					String string = null;
					string = EntityUtils.toString(entity);
					sversion = Integer.parseInt(string);
				} catch (ClientProtocolException cpe) {
					Log.e(TAG_, "ClientProtocolException:" + cpe.toString());
                    mHandler.sendEmptyMessage(CHECKING_OK);
					cpe.printStackTrace();
				} catch (HttpHostConnectException hhce) {
					Log.e(TAG_, "HttpHostConnectException:" + hhce.toString());
                    mHandler.sendEmptyMessage(CHECKING_OK);
					hhce.printStackTrace();
				} catch (IOException e) {
					Log.e(TAG_, "HttpHostConnectException:" + e.toString());
                    mHandler.sendEmptyMessage(CHECKING_OK);
					e.printStackTrace();
				} catch (Exception e) {
                    mHandler.sendEmptyMessage(CHECKING_OK);
					e.printStackTrace();
				} finally {
					httpclient.getConnectionManager().shutdown();
				}
				if (!up.isUpdate(sversion)) {
					mHandler.sendEmptyMessage(CHECKING_OK);
				} else
					mHandler.sendEmptyMessage(CHECKING_NO);

			}
		}.start();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CHECKING:
				break;
			case CHECKING_OK:
				new Handler().postDelayed(new Runnable() {

					public void run() {
						Intent intent = new Intent();
						intent.setClass(Welcome.this, MainActivity.class);
						startActivity(intent);
					}

				}, 1000);
				break;
			case CHECKING_NO:
				up.checkUpdate(sversion);
				welcome.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.setClass(Welcome.this, MainActivity.class);
						startActivity(intent);
					}
				});
				break;
			default:
				break;
			}
		};
	};

}
