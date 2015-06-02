package activities;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.example.mybluetooth.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DevicesListActivity extends Activity {
	public static final int CMD_SEND_DATA = 0x01;
	public static final int CMD_STOP_SERVICE = 0x02;
	public static final int CMD_SHOW_TOAST = 0x03;

	private DevicesListData devicesListData;
	private ArrayList<String[]> devicesList;
	private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private ArrayList<HashMap<String, String>> adapterInfoList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.deviceslist);
		Bundle bundle = this.getIntent().getBundleExtra("devicesBundle");
		devicesListData = bundle.getParcelable("list");
		devicesList = devicesListData.getList();
		adapterInfoList = new ArrayList<HashMap<String, String>>();
		String[] deviceInfoArr;
		HashMap<String, String> map;
		for (int i = 0; i < devicesList.size(); i++) {
			deviceInfoArr = devicesList.get(i);
			map = new HashMap<String, String>();
			for (int j = 0; j < deviceInfoArr.length; j++) {
				map.put("deviceName", deviceInfoArr[0]);
				map.put("deviceAddress", deviceInfoArr[1]);
				map.put("deviceIsNew", deviceInfoArr[2]);
			}
			adapterInfoList.add(map);
		}
		SimpleAdapter adapter = new SimpleAdapter(this, adapterInfoList,
				R.layout.deviceslist_item, new String[] { "deviceName",
						"deviceAddress", "deviceIsNew" }, new int[] {
						R.id.deviceName, R.id.deviceAddress, R.id.deviceIsNew });

		ListView devicesListView = (ListView) findViewById(R.id.devicesList);
		devicesListView.setAdapter(adapter);

		Intent serviceIntent = new Intent(DevicesListActivity.this,
				BluetoothService.class);
		startService(serviceIntent);

		devicesListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BluetoothDevice device = myBluetoothAdapter
						.getRemoteDevice(adapterInfoList.get(position).get(
								"deviceAddress"));
				sendCmd(device);
				
			}

		});

	}

	public void sendCmd(BluetoothDevice device) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.cmd");
		DeviceData da = new DeviceData();
		ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
		list.add(device);
		da.setDevice(list);
		intent.putExtra("cmd", DevicesListActivity.CMD_SEND_DATA);
		intent.putExtra("device", device);
		sendBroadcast(intent);
	}

}
