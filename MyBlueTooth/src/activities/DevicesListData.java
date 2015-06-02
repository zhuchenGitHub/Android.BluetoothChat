package activities;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class DevicesListData implements Parcelable {
	private ArrayList<String[]> list = new ArrayList<String[]>();

	private DevicesListData(Parcel in) {
		list = in.readArrayList(String.class.getClassLoader());
	}
	
	public DevicesListData(){
		
	}
	
	public void setList(ArrayList<String[]> list) {
		this.list = list;
	}

	public ArrayList<String[]> getList() {
		return list;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(list);
	}

	public static final Parcelable.Creator<DevicesListData> CREATOR = new Parcelable.Creator<DevicesListData>() {

		@Override
		public DevicesListData createFromParcel(Parcel source) {
			return new DevicesListData(source);
		}

		@Override
		public DevicesListData[] newArray(int size) {
			return new DevicesListData[size];
		}

	};

}
