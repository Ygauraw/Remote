package se.newbie.remote.boxee;

import se.newbie.remote.device.RemoteDevice;
import se.newbie.remote.device.RemoteDeviceDetails;
import se.newbie.remote.util.jsonrpc2.JSONRPC2NotificationListener;
import se.newbie.remote.util.jsonrpc2.JSONRPC2Request;

public class BoxeeRemoteDevice implements RemoteDevice {
	private static final String TAG = "BoxeeRemoteDevice";
	
	private BoxeeRemoteDeviceDetails details;
	private BoxeeRemoteDeviceConnection connection;
	private BoxeePlayerState state;
	
	public BoxeeRemoteDevice(BoxeeRemoteDeviceDetails details) {
		this.details = details;
		this.connection = new BoxeeRemoteDeviceConnection(this);
		this.state = new BoxeePlayerState(this);
	}
	
	public String getIdentifier() {
		return details.getIdentifier();
	}
	
	public RemoteDeviceType getRemoteDeviceType() {
		return RemoteDeviceType.Boxee;
	}
	
	public String getRemoteDeviceName() {
		return details.getName();
	}	

	public RemoteDeviceDetails getRemoteDeviceDetails() {
		return details;
	}

	public int sendCommand(final JSONRPC2Request request) {
		this.connection.sendRequest(request, null);
		return 1;	
	}	
	
/*	private void sendHttpRequestTask(String url, HttpRequestTaskHandler handler) {
		HttpRequestTask request = new HttpRequestTask();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(RemoteApplication.getInstance().getContext());
		
		if (preferences.contains(getIdentifier() + ".user")) {
			request.setCredentials(preferences.getString(getIdentifier() + ".user", "")
					, preferences.getString(getIdentifier() + ".password", ""));
		}
		if (handler != null) {
			request.setHttpRequestTaskHandler(handler);
		}
		request.execute(url);
	}*/
	
	/**
	 * Only update the host information if the device was found again.
	 */
	public boolean update(RemoteDeviceDetails details) {
		boolean b = false;
		if (details.getIdentifier().startsWith(BoxeeRemoteDeviceDiscoverer.APPLICATION)) {
			if (!((BoxeeRemoteDeviceDetails)details).getHost().equals(this.details.getHost())) {
				this.details.setHost(((BoxeeRemoteDeviceDetails)details).getHost());
				b = true;
			}
			if (!((BoxeeRemoteDeviceDetails)details).getPort().equals(this.details.getPort())) {
				this.details.setPort(((BoxeeRemoteDeviceDetails)details).getPort());
				b = true;
			}			
		}	
		return b;
	}

	public void pause() {
		getConnection().pause();
	}

	public void resume() {
		getConnection().resume();
		
	}
	
	public void addNotificationListener(JSONRPC2NotificationListener listener) {
		getConnection().addNotificationListener(listener);
	}

	public BoxeeRemoteDeviceConnection getConnection() {
		return connection;
	}

	public BoxeePlayerState getBoxeePlayerState() {
		return this.state;
	}
}