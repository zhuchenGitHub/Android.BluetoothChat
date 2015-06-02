package activities;

import java.util.ArrayList;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class DeviceData implements Parcelable{
	private ArrayList<BluetoothDevice> list = new ArrayList<BluetoothDevice>();
	
	public  DeviceData(){
		
	}
	
	public void setDevice(ArrayList<BluetoothDevice> list){
		this.list=list;
	}
	
	public BluetoothDevice getDevice(){
		return list.get(0);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(list);
		
	}
	
	public static final Parcelable.Creator<DeviceData> CREATOR = new Parcelable.Creator<DeviceData>() {

		@Override
		public DeviceData createFromParcel(Parcel source) {
			return new DeviceData();
		}

		@Override
		public DeviceData[] newArray(int size) {
			return new DeviceData[size];
		}

	};
	
}
