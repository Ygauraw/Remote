package se.newbie.remote.tellduslive;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import se.newbie.remote.application.RemoteApplication;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class TelldusLiveRemoteDeviceConnection {
	private final static String TAG = "TelldusLiveRemoteDeviceConnection";
	private OAuthService service;
	private TelldusLiveRemoteDevice device;

	private static final String RESOURCE_DOMAIN = "https://api.telldus.com/json";
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.v(TAG, "Opening login dialog...");
			TelldusLiveAuthenticateDialog dialog = TelldusLiveAuthenticateDialog.newInstance((TelldusLiveRemoteDeviceDetails)device.getRemoteDeviceDetails());
			if (dialog != null) {
				RemoteApplication.getInstance().showDialog(dialog);
			}					

		}
	};		
	
	public TelldusLiveRemoteDeviceConnection(TelldusLiveRemoteDevice device) {
		this.device = device;
        service = new ServiceBuilder()
        .provider(TelldusLiveAPI.class)
        .apiKey("FEHUVEW84RAFR5SP22RABURUPHAFRUNU")
        .apiSecret("ZUXEVEGA9USTAZEWRETHAQUBUR69U6EF")
        .callback("callback://remoteApplication")
        .build();
	}
	
	public void request(final String resource) {
        new Thread() {
        	public void run() {		
				OAuthRequest request = new OAuthRequest(Verb.GET, RESOURCE_DOMAIN + resource);
				TelldusLiveRemoteDeviceDetails details = (TelldusLiveRemoteDeviceDetails)device.getRemoteDeviceDetails();
				service.signRequest(details.getAccessToken(), request);
				try {
				Response response = request.send();
				Log.v(TAG, response.getBody());
				} catch (Exception e) {
					Log.v(TAG, e.getMessage());
					handler.sendEmptyMessage(0);
				}
        	}
        }.start();
	}
	
	public void getRequestToken(final TelldusLiveTokenResponseHandler handler) {
        new Thread() {
        	public void run() {
        		Token requestToken =  service.getRequestToken();
        		handler.onResponse(requestToken);
        	}
        }.start();
		
	}
	
	public void getAccessToken(final Token requestToken, final Verifier verifier, final TelldusLiveTokenResponseHandler handler) {
        new Thread() {
        	public void run() {
        		Token accessToken = service.getAccessToken(requestToken, verifier);
				handler.onResponse(accessToken);
        	}
        }.start();		
	}	
	
	public String getAuthorizationUrl(Token requestToken) {
		return service.getAuthorizationUrl(requestToken);
	}
		
	
	
	public interface TelldusLiveTokenResponseHandler {
		public void onResponse(Token token);
	}
}