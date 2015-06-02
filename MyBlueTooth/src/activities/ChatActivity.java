package activities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.mybluetooth.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChatActivity extends Activity {
	private ListView chatView;
	private Button sendButton;
	private EditText sendText;
	private String date;
	private String name;
	private String textToSend;
	private ChatMessageEntity msgC;
	private List<ChatMessageEntity> list;
	private String returnMessage;
	private returnMessageReceiver receiver;
	private Handler handler;
	private String newMsg;
	public static final int CMD_SEND_MESSAGE = 0x05;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat_mainpage);

		chatView = (ListView) findViewById(R.id.chatList);
		sendButton = (Button) findViewById(R.id.sendButton);
		sendText = (EditText) findViewById(R.id.chatText);
		list = new ArrayList<ChatMessageEntity>();

		receiver = new returnMessageReceiver();
		IntentFilter returnFilter = new IntentFilter();
		returnFilter.addAction("android.intent.action.returnData");
		registerReceiver(receiver, returnFilter);

		handler = new Handler() {
			boolean isNew = false;

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (isNew && msg.what == 0) {
					newMsg = (String) msg.obj;
					msgC = new ChatMessageEntity(getCurrentDate(), newMsg,
							"傻逼二号", true);
					list.add(msgC);
					chatView.setAdapter(new ChatListAdapter(ChatActivity.this,
							list));
					isNew = false;
				} else if (msg.what == 1) {
					newMsg = (String) msg.obj;
					date = getCurrentDate();
					name = "傻逼一号";
					msgC = new ChatMessageEntity(date, newMsg, name, false);
					list.add(msgC);
					chatView.setAdapter(new ChatListAdapter(ChatActivity.this,
							list));
					isNew=false;
				} else if (msg.what == 2) {
					isNew = (Boolean) msg.obj;
				}
			}
		};

		new Thread(new Runnable() {
			Message msg = null;

			@Override
			public void run() {
				while (true) {
					msg = new Message();
					if (null != returnMessage) {
						msg.obj = returnMessage;
						msg.what = 0;
						handler.sendMessage(msg);
					}
				}
			}
		}).start();

		sendButton.setOnClickListener(new OnClickListener() {
			Message msg = null;

			@Override
			public void onClick(View v) {
				msg = new Message();
				textToSend = sendText.getText().toString();
				if (textToSend.equals("")) {
					Toast.makeText(ChatActivity.this, "请输入内容",
							Toast.LENGTH_SHORT);
					return;
				}
				sendData(textToSend);
				msg.obj = textToSend;
				msg.what = 1;
				handler.sendMessage(msg);
				sendText.setText("");
			}

		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

	private class returnMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			Message message = new Message();
			if (action.equals("android.intent.action.returnData")) {
				int cmd = intent.getIntExtra("cmd", -1);
				if (cmd == BluetoothService.CMD_RETURN_DATA) {
					String msg = intent.getStringExtra("returnMsg");
					message.obj = true;
					message.what = 2;
					handler.sendMessage(message);
					setReturnMsg(msg);

				}
			}

		}

	}

	private void setReturnMsg(String msg) {
		this.returnMessage = msg;
	}

	private String getCurrentDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("MM月dd日   HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());
		return formatter.format(curDate);
	}

	private void sendData(String data) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.data");
		intent.putExtra("cmd", ChatActivity.CMD_SEND_MESSAGE);
		intent.putExtra("msg", data);
		sendBroadcast(intent);
	}
}
