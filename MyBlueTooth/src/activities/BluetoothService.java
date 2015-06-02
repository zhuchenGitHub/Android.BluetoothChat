package activities;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class BluetoothService extends Service {
	private BluetoothAdapter myBluetoothAdapter = BluetoothAdapter
			.getDefaultAdapter();
	private ServerThread serverThread = null;
	private ClientThread clientThread = null;
	private CommandReceiver cmdReceiver = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	private BluetoothDevice bDevice = null;
	private boolean isOn = true;
	private boolean isServerConnected = false;
	private boolean isClientConnected = false;
	public static final int CMD_RETURN_DATA = 0x06;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		serverThread = new ServerThread();
		serverThread.start();
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (isServerConnected || isClientConnected) {
						Intent intent = new Intent(BluetoothService.this,
								ChatActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
						break;
					}
				}
			}
		}).start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		cmdReceiver = new CommandReceiver();
		IntentFilter commandFilter = new IntentFilter();
		commandFilter.addAction("android.intent.action.cmd");
		registerReceiver(cmdReceiver, commandFilter);
		IntentFilter dataFilter = new IntentFilter();
		dataFilter.addAction("android.intent.action.data");
		registerReceiver(cmdReceiver, dataFilter);

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					if (null == bDevice) {
						continue;
					} else {

						clientThread = new ClientThread(bDevice);
						clientThread.start();
						break;
					}
				}

			}

		}).start();
		// return super.onStartCommand(intent, flags, startId);
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		isServerConnected = false;
		isClientConnected = false;
		super.onDestroy();
		serverThread.interrupt();
		clientThread.interrupt();
		this.unregisterReceiver(cmdReceiver);
		try {
			outStream.close();
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void stopService() {
		stopSelf();
	}

	private void setDevice(BluetoothDevice bdevice) {
		this.bDevice = bdevice;
	}

	private void sendData(String s) {
		if (!(isServerConnected || isClientConnected)) {
			return;
		}
		byte[] outData = s.getBytes();
		try {
			outStream.write(outData);
			outStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				outStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			stopService();
		}
	}

	private String readData() {
		String inData = null;
		byte[] temp = new byte[1024];
		if (!(isServerConnected || isClientConnected)) {
			return null;
		}

		try {
			int bytes;
			if ((bytes = inStream.read(temp)) > 0) {
				byte[] tem_data = new byte[bytes];

				for (int i = 0; i < bytes; i++) {
					tem_data[i] = temp[i];
				}
				inData = new String(tem_data);
			}

		} catch (IOException e) {
			e.printStackTrace();
			try {
				inStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			stopService();
		}
		return inData;
	}

	private class CommandReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals("android.intent.action.cmd")) {
				int cmd = intent.getIntExtra("cmd", -1);
				if (cmd == DevicesListActivity.CMD_SEND_DATA) {
					BluetoothDevice device = intent
							.getParcelableExtra("device");

					setDevice(device);
				} else if (cmd == DevicesListActivity.CMD_STOP_SERVICE) {
					stopService();
				}
			} else if (intent.getAction().equals("android.intent.action.data")) {
				int cmd = intent.getIntExtra("cmd", -1);
				if (cmd == ChatActivity.CMD_SEND_MESSAGE) {
					String message = intent.getStringExtra("msg");
					sendData(message);
				}
			}

		}

	}

	private class ServerThread extends Thread {
		private final BluetoothServerSocket myServerSocket;

		public ServerThread() {
			BluetoothServerSocket temp = null;
			try {

				temp = myBluetoothAdapter
						.listenUsingRfcommWithServiceRecord(
								"myChannel",
								UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			myServerSocket = temp;
		}

		@Override
		public void run() {
			BluetoothSocket bSocket = null;
			try {
				bSocket = myServerSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();

			}

			if (bSocket != null) {
				isServerConnected = true;
				try {
					outStream = bSocket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					inStream = bSocket.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				new Thread(new Runnable() {
					@Override
					public void run() {
						while (isServerConnected) {
							String inmsg = readData();
							System.out.println("server" + inmsg);
							if (null != inmsg) {
								Intent intent = new Intent();
								intent.setAction("android.intent.action.returnData");
								intent.putExtra("cmd",
										BluetoothService.CMD_RETURN_DATA);
								intent.putExtra("returnMsg", inmsg);
								sendBroadcast(intent);
							}
						}
					}
				}).start();
			}

		}

	}

	private class ClientThread extends Thread {
		private BluetoothDevice device;
		private final BluetoothSocket socket;

		public ClientThread(BluetoothDevice device) {
			BluetoothSocket temp = null;
			this.device = device;

			try {

				temp = device.createRfcommSocketToServiceRecord(UUID
						.fromString("00001101-0000-1000-8000-00805F9B34FB"));

			} catch (IOException e) {
				e.printStackTrace();
			}
			socket = temp;
		}

		@Override
		public void run() {
			try {
				socket.connect();

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (socket.isConnected()) {
				isClientConnected = true;
				try {
					outStream = socket.getOutputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					inStream = socket.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			new Thread(new Runnable() {
				@Override
				public void run() {
					while (isClientConnected) {

						String inmsg = readData();
						System.out.println("inmsg" + inmsg);
						if (null != inmsg) {
							Intent intent = new Intent();
							intent.setAction("android.intent.action.returnData");
							intent.putExtra("cmd",
									BluetoothService.CMD_RETURN_DATA);
							intent.putExtra("returnMsg", inmsg);
							sendBroadcast(intent);
						}
					}
				}
			}).start();

			return;
		}

	}
}
