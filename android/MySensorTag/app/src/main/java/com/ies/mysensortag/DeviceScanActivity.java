package com.ies.mysensortag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;


import com.ies.blelib.BleScaner;
import com.ies.mysensortag.R;
import com.sjtu.userkey.AuthorInfor;
import com.sjtu.userkey.AuthorInforRepo;
import com.sjtu.util.SqhelperRemember;
import com.sjtu.util.Static;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class DeviceScanActivity extends Activity {

	private final static String TAG_ = DeviceScanActivity.class.getSimpleName();
	private ToggleButton button_scan_switch_;
	private BluetoothAdapter ble_adapter_;
	private ExpandableListView listview_scan_;
	private ExpandableListAdapter list_adapter_scan_;
	private BleScaner ble_scaner_;
	private final long DISAPPEAR_CHECK_INTERVAL_ = 1000;
	HashMap<String, List<String>> expandableListDetail;
	private Handler disappear_check_handler_;
	private ProgressDialog dlg_progress_;
	private static final int REGISTERING = 1;
    private static final int REGIST_FINISH = 2;
    private static final int REGIST_NO = 3;
    private static final int REGIST_NOCON = 4;
    private String body;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//
		// Prepare UI layout and UI controls.
		//
		setContentView(R.layout.device_scan_main);

		button_scan_switch_ = (ToggleButton) findViewById(R.id.scan_swtich_button);
		listview_scan_ = (ExpandableListView) findViewById(R.id.device_list);

		list_adapter_scan_ = new ExpandableListAdapter(this);
		listview_scan_.setAdapter(list_adapter_scan_);
		listview_scan_.setOnChildClickListener(list_adapter_scan_
				.get_item_click_listener());
		for (int j = 0; j < 3; j++) {
			listview_scan_.expandGroup(j);
		}

		if (!getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_BLUETOOTH_LE)) {
			Toast.makeText(this, "NO BLE", Toast.LENGTH_SHORT).show();
			finish();
		}
		final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		ble_adapter_ = bluetoothManager.getAdapter();
		if (ble_adapter_ == null) {
			Toast.makeText(this, "NO BLE", Toast.LENGTH_SHORT).show();
			finish();
			return;
		}
		if (ble_adapter_ == null || !ble_adapter_.isEnabled()) {
			Intent enableBtIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivity(enableBtIntent);
		}

		ble_scaner_ = new BleScaner();
		disappear_check_handler_ = new Handler();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onResume() {
		super.onResume();

		Log.d(TAG_, "onResume");
		if (button_scan_switch_.isChecked()) {
			ble_scaner_.startScan(scan_callback_, ble_adapter_);
			disappear_check_handler_.postDelayed(disappear_check_runner_,
					DISAPPEAR_CHECK_INTERVAL_);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		Log.d(TAG_, "onPause");
		if (button_scan_switch_.isChecked()) {
			ble_scaner_.stopLeScan(scan_callback_, ble_adapter_);
			list_adapter_scan_.clear();
		}
	}

	public void onScanToggleClicked(View view) {
		boolean on = button_scan_switch_.isChecked();
		if (on) {
			Log.d(TAG_, "Scan button ON");

			//
			// Ensures Bluetooth is available on the device and it is enabled.
			// If not, displays a dialog requesting user permission to enable
			// Bluetooth.
			//
			if (ble_adapter_ == null || !ble_adapter_.isEnabled()) {
				Intent enableBtIntent = new Intent(
						BluetoothAdapter.ACTION_REQUEST_ENABLE);
				startActivity(enableBtIntent);
			}

			if (ble_adapter_.isEnabled()) {
				ble_scaner_.startScan(scan_callback_, ble_adapter_);
				disappear_check_handler_.postDelayed(disappear_check_runner_,
						DISAPPEAR_CHECK_INTERVAL_);
			} else {
				button_scan_switch_.setChecked(false);
			}
		} else {
			Log.d(TAG_, "Scan button OFF");
			ble_scaner_.stopLeScan(scan_callback_, ble_adapter_);
			list_adapter_scan_.clear();
		}
	}

	// Device scan callback.
	private BluetoothAdapter.LeScanCallback scan_callback_ = new BluetoothAdapter.LeScanCallback() {

		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				final byte[] scanRecord) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {

					list_adapter_scan_.update_device(device, rssi, scanRecord);

				}
			});
		}
	};
	final Runnable disappear_check_runner_ = new Runnable() {
		public void run() {
			if (button_scan_switch_.isChecked()) {
				list_adapter_scan_.refresh_disappeared_device();
				disappear_check_handler_.postDelayed(this,
						DISAPPEAR_CHECK_INTERVAL_);
			}
		}
	};
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			// ��������
			case REGISTERING:
				dlg_progress_ = ProgressDialog.show(DeviceScanActivity.this,
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
	/**
	 * @Override public boolean onCreateOptionsMenu(Menu menu) { // Inflate the
	 *           menu; this adds items to the action bar if it is present.
	 *           getMenuInflater().inflate(R.menu.main, menu); return true; }
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { // Handle
	 *           action bar item clicks here. The action bar will //
	 *           automatically handle clicks on the Home/Up button, so long //
	 *           as you specify a parent activity in AndroidManifest.xml. int id
	 *           = item.getItemId(); if (id == R.id.action_settings) { Intent
	 *           intent = new Intent(); intent.setClass(this,
	 *           SettingPreferenceActivity.class); startActivity(intent); return
	 *           true; } return super.onOptionsItemSelected(item); }
	 **/

}
