package activities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

import com.example.mybluetooth.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainPageActivity extends Activity {
	private static final int REQUEST_ENABLE_BT = 1;
	private static final int REQUEST_ENABLE_BT1 = 2;
	private ArrayList<String[]> devicesInfo;
	private DevicesListData devicesData = new DevicesListData();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainpage);
//		getMyUUID();
		Button openButton = (Button) findViewById(R.id.openBlueToothButton);
		openButton.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
						.getDefaultAdapter();
				if (null == myBluetoothAdapter) {
					Toast.makeText(getApplicationContext(), "本机不支持蓝牙",
							Toast.LENGTH_SHORT).show();
					return;
				}
				if (!myBluetoothAdapter.isEnabled()) {
					Intent openIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_ENABLE);
					startActivityForResult(openIntent, REQUEST_ENABLE_BT);

				} else {
					Toast.makeText(getApplicationContext(), "蓝牙已开启",
							Toast.LENGTH_SHORT).show();
				}
				if (myBluetoothAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
					Intent discoverableIntent = new Intent(
							BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
					discoverableIntent.putExtra(
							BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
					startActivityForResult(discoverableIntent,
							REQUEST_ENABLE_BT1);
				}
				Set<BluetoothDevice> pairedDevices = myBluetoothAdapter
						.getBondedDevices();
				devicesInfo = new ArrayList<String[]>();
				if (pairedDevices.size() > 0) {
					for (BluetoothDevice bd : pairedDevices) {
						devicesInfo.add(new String[] { bd.getName(),
								bd.getAddress(), "paired" });
					}
				}
				IntentFilter foundFilter = new IntentFilter(
						BluetoothDevice.ACTION_FOUND);
				registerReceiver(myReceiver, foundFilter);
				IntentFilter finishFilter = new IntentFilter(
						BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
				registerReceiver(myReceiver, finishFilter);

				myBluetoothAdapter.startDiscovery();

			}

		});
	}

//	private void getMyUUID() {
//		final TelephonyManager tm = (TelephonyManager) getBaseContext()
//				.getSystemService(Context.TELEPHONY_SERVICE);
//
//		final String tmDevice, tmSerial, tmPhone, androidId;
//
//		tmDevice = "" + tm.getDeviceId();
//
//		tmSerial = "" + tm.getSimSerialNumber();
//
//		androidId = ""
//				+ android.provider.Settings.Secure.getString(
//						getContentResolver(),
//						android.provider.Settings.Secure.ANDROID_ID);
//
//		UUID deviceUuid = new UUID(androidId.hashCode(),
//				((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
//
//		Config.MY_UUID = (UUID) deviceUuid;
//	}

	private BroadcastReceiver myReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				BluetoothDevice newDevice = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				if (newDevice.getBondState() != BluetoothDevice.BOND_BONDED
						&& !contains(
								devicesInfo,
								new String[] { newDevice.getName(),
										newDevice.getAddress(), "new" })) {
					devicesInfo.add(new String[] { newDevice.getName(),
							newDevice.getAddress(), "new" });
				}

			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				Intent showDevicesIntent = new Intent(MainPageActivity.this,
						DevicesListActivity.class);
				devicesData.setList(devicesInfo);
				Bundle bundle = new Bundle();
				bundle.putParcelable("list", devicesData);
				showDevicesIntent.putExtra("devicesBundle", bundle);
				startActivity(showDevicesIntent);
				MainPageActivity.this.finish();
			}

		}

	};

	public boolean contains(ArrayList<String[]> arrlist, String[] arr) {
		for (String[] temp : arrlist) {
			if (Arrays.equals(temp, arr)) {
				return true;
			} else {
				continue;
			}
		}
		return false;
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == RESULT_OK) {
				Toast.makeText(getApplicationContext(), "蓝牙已开启",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), "蓝牙开启失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}

}
