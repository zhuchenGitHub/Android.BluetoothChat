package activities;

import java.util.List;
import java.util.Map;

import com.example.mybluetooth.R;

import android.content.Context;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {
	private Context context;
	private List<ChatMessageEntity> list;
	private LayoutInflater layoutInflater;

	public ChatListAdapter(Context context, List<ChatMessageEntity> list) {
		this.context = context;
		this.list = list;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null && !list.get(position).getIsGet()) {
			convertView = layoutInflater.inflate(R.layout.chatmessage_item,
					null);
		} else if (convertView == null && list.get(position).getIsGet()) {
			convertView = layoutInflater.inflate(R.layout.chatmessage_item_get,
					null);
		}

		if (!list.get(position).getIsGet()) {
			TextView dateView = (TextView) convertView
					.findViewById(R.id.chatDate);
			TextView contentView = (TextView) convertView
					.findViewById(R.id.chatContent);
			TextView nameView = (TextView) convertView
					.findViewById(R.id.username);

			dateView.setText(list.get(position).getDate());
			changeBubble(convertView, list.get(position).getMessage(), true);
			nameView.setText(list.get(position).getName());
		} else {
			TextView dateView = (TextView) convertView
					.findViewById(R.id.chatDateGet);
			TextView nameView = (TextView) convertView
					.findViewById(R.id.usernameGet);
			TextView contentView = (TextView) convertView
					.findViewById(R.id.chatContent_get);

			dateView.setText(list.get(position).getDate());
			nameView.setText(list.get(position).getName());
			changeBubble(convertView, list.get(position).getMessage(), false);
		}

		return convertView;
	}

	public void changeBubble(View view, String text, boolean isRight) {
		int bubbleWidth = 300;
		TextView textView = null;
		ImageView bubble2 = null;
		ImageView bubble4 = null;
		ImageView bubble6 = null;
		ImageView bubble8 = null;
		if (isRight) {

			textView = (TextView) view.findViewById(R.id.rightLinearLayout)
					.findViewById(R.id.rightRelativeLayout)
					.findViewById(R.id.bubble_lay_2)
					.findViewById(R.id.chatContent);
			bubble2 = (ImageView) view.findViewById(R.id.rightLinearLayout)
					.findViewById(R.id.rightRelativeLayout)
					.findViewById(R.id.bubble_lay_2)
					.findViewById(R.id.bubble_2);
			bubble4 = (ImageView) view.findViewById(R.id.rightLinearLayout)
					.findViewById(R.id.rightRelativeLayout)
					.findViewById(R.id.bubble_lay_1)
					.findViewById(R.id.bubble_4);
			bubble6 = (ImageView) view.findViewById(R.id.rightLinearLayout)
					.findViewById(R.id.rightRelativeLayout)
					.findViewById(R.id.bubble_lay_3)
					.findViewById(R.id.bubble_6);
			bubble8 = (ImageView) view.findViewById(R.id.rightLinearLayout)
					.findViewById(R.id.rightRelativeLayout)
					.findViewById(R.id.bubble_lay_2)
					.findViewById(R.id.bubble_8);
		} else {
			textView = (TextView) view.findViewById(R.id.LinearLayoutGet)
					.findViewById(R.id.RelativeLayoutGet)
					.findViewById(R.id.bubble_lay_2_get)
					.findViewById(R.id.chatContent_get);
			bubble2 = (ImageView) view.findViewById(R.id.LinearLayoutGet)
					.findViewById(R.id.RelativeLayoutGet)
					.findViewById(R.id.bubble_lay_2_get)
					.findViewById(R.id.bubble_2_get);
			bubble4 = (ImageView) view.findViewById(R.id.LinearLayoutGet)
					.findViewById(R.id.RelativeLayoutGet)
					.findViewById(R.id.bubble_lay_1_get)
					.findViewById(R.id.bubble_4_get);
			bubble6 = (ImageView) view.findViewById(R.id.LinearLayoutGet)
					.findViewById(R.id.RelativeLayoutGet)
					.findViewById(R.id.bubble_lay_3_get)
					.findViewById(R.id.bubble_6_get);
			bubble8 = (ImageView) view.findViewById(R.id.LinearLayoutGet)
					.findViewById(R.id.RelativeLayoutGet)
					.findViewById(R.id.bubble_lay_2_get)
					.findViewById(R.id.bubble_8_get);
		}

		textView.setText(text);
		int textSize = (int) textView.getTextSize();
		TextPaint paint = textView.getPaint();
		int textWidth = (int) paint.measureText(text);
		int bubbleHeight = textSize * 2;

		if (bubbleWidth < textWidth) { // »»ÐÐ
			bubbleHeight = textSize * 2 * ((int) (textWidth / bubbleWidth) + 1);
			textWidth = bubbleWidth;

		}

		bubble2.getLayoutParams().height = bubbleHeight;
		textView.getLayoutParams().height = bubbleHeight;
		bubble8.getLayoutParams().height = bubbleHeight;
		bubble4.getLayoutParams().width = textWidth;
		textView.getLayoutParams().width = textWidth;
		bubble6.getLayoutParams().width = textWidth;
	}
}
