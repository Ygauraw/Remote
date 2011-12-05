package se.newbie.remote.tellduslive.gui;

import java.util.ArrayList;
import java.util.List;

import se.newbie.remote.R;
import se.newbie.remote.application.RemoteApplication;
import se.newbie.remote.device.RemoteDeviceFactory;
import se.newbie.remote.tellduslive.TelldusLiveDevice;
import se.newbie.remote.tellduslive.TelldusLiveRemoteCommand;
import se.newbie.remote.tellduslive.TelldusLiveRemoteDevice;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class TelldusLiveDeviceListView extends ListView {
	private final static String TAG = "DeviceListView";
	private String device;

	private List<TelldusLiveDevice> devices;
	private TelldusLiveDeviceAdapter adapter;

	public TelldusLiveDeviceListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		Log.v(TAG, "Create TelldusLive device list");
		initAttributes(attrs);
		adapter = new TelldusLiveDeviceAdapter(context,
				R.layout.telldus_live_device_list_adapter_item, device);
		this.setAdapter(adapter);
	}

	private final void initAttributes(AttributeSet attrs) {
		TypedArray a = getContext().obtainStyledAttributes(attrs,
				R.styleable.remote);
		device = a.getString(R.styleable.remote_device);
	}

	private void updateView() {
		this.post(new Runnable() {
			public void run() {
				Log.v(TAG, "Update TelldusLive device list");
				adapter.clear();
				if (devices != null) {
					adapter.addDevices(devices);
				}
				adapter.notifyDataSetChanged();
			}
		});
	}

	public void update() {
		RemoteDeviceFactory factory = RemoteApplication.getInstance()
				.getRemoteDeviceFactory();
		TelldusLiveRemoteDevice telldusLiveRemoteDevice = (TelldusLiveRemoteDevice) factory
				.getRemoteDevice(device);
		devices = telldusLiveRemoteDevice.getTelldusLiveDevices();
		updateView();
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	class TelldusLiveDeviceAdapter extends BaseAdapter {

		private List<TelldusLiveDevice> devices = new ArrayList<TelldusLiveDevice>();
		private int resource;
		private Context context;
		private String remoteDeviceIdentifier;

		public TelldusLiveDeviceAdapter(Context context, int resource,
				String remoteDeviceIdentifier) {
			this.resource = resource;
			this.context = context;
			this.remoteDeviceIdentifier = remoteDeviceIdentifier;
		}

		public int getCount() {
			return devices.size();
		}

		public Object getItem(int position) {
			return devices.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public void clear() {
			devices.clear();
		}

		public void addDevice(TelldusLiveDevice device) {
			devices.add(device);
		}

		public void addDevices(List<TelldusLiveDevice> devices) {
			this.devices.addAll(devices);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			View view = inflater.inflate(resource, null);
			TelldusLiveDevice device = devices.get(position);
			TextView textView = (TextView) view
					.findViewById(R.id.telldus_live_device_list_item_headline);
			textView.setText(device.getName());

			TelldusLiveActionButton onButton = (TelldusLiveActionButton) view
					.findViewById(R.id.telldus_live_device_list_item_on);
			onButton.setDevice(remoteDeviceIdentifier);
			onButton.setCommand(TelldusLiveRemoteCommand.Command.turnOn.name()
					+ "-" + device.getId());

			TelldusLiveActionButton offButton = (TelldusLiveActionButton) view
					.findViewById(R.id.telldus_live_device_list_item_off);
			offButton.setDevice(remoteDeviceIdentifier);
			offButton.setCommand(TelldusLiveRemoteCommand.Command.turnOff
					.name() + "-" + device.getId());

			ImageView stateImage = (ImageView) view
					.findViewById(R.id.telldus_live_device_list_item_state);
			if ((device.getState() & TelldusLiveDevice.METHOD_ON) == TelldusLiveDevice.METHOD_ON) {
				stateImage.setImageResource(R.drawable.ic_light_on);
			} else {
				stateImage.setImageResource(R.drawable.ic_light_off);
			}

			return view;
		}
	}

}
