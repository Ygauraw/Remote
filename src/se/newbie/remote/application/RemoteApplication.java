package se.newbie.remote.application;

import se.newbie.remote.command.RemoteCommandFactory;
import se.newbie.remote.device.RemoteDeviceFactory;
import se.newbie.remote.display.RemoteDisplayFactory;
import se.newbie.remote.gui.standard.StandardRemoteGUIFactory;
import se.newbie.remote.main.RemoteController;
import se.newbie.remote.main.RemoteModel;
import se.newbie.remote.main.RemoteView;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.provider.Settings.Secure;
import android.util.Log;

public class RemoteApplication {
	private static final String TAG = "RemoteApplication";
	
	// This variable is only true while developing.
	private static final boolean emulator = true;
	
	private static RemoteApplication remoteApplication;
	
	private RemoteActivity activity;
	private RemoteModel remoteModel;
	private RemoteView remoteView;
	private RemoteController remoteController;
	private RemoteDeviceFactory remoteDeviceFactory;
	private RemoteCommandFactory remoteCommandFactory;
	private RemoteDisplayFactory remoteDisplayFactory;
	private RemotePlayerView remotePlayerView;
	
	protected RemoteApplication() {
	}
	
	public static RemoteApplication getInstance() {
		if (remoteApplication == null) {
			remoteApplication = new RemoteApplication();
		}
		return remoteApplication;
	}

	public void init(RemoteActivity activity) {
		Log.v(TAG, "Initializing...");
		
		this.activity = activity;
		
		
		createMVCModel(activity.getApplicationContext()); 
		createRemoteCommandFactory();
		createRemoteDisplayFactory();
		createRemoteDeviceFactory();		
		createGUIFactory();
	}

	public void pause() {
		remoteDeviceFactory.pause();
		if (remotePlayerView != null) {
			remotePlayerView.pause();
		}
	}

	public void resume() {
		remoteDeviceFactory.resume();
		if (remotePlayerView != null) {
			remotePlayerView.resume();
		}
	}	

	/**
	 * Shows the dialog with the main UI thread.
	 */
	public void showDialog(final DialogFragment dialog) {
		activity.runOnUiThread(new Runnable(){
			public void run() {
				activity.showDialog(dialog);
			}
		});
	}
	
	private void createMVCModel(Context context) {
        Log.v(TAG, "Create MVC instances");
        setRemoteModel(new RemoteModelImpl(context));
        setRemoteView(new RemoteViewImpl());
        setRemoteController(new RemoteControllerImpl(getRemoteModel(), getRemoteView()));
	}
	
	private void createGUIFactory() {
        Log.v(TAG, "Create Remote GUI Factory");
        StandardRemoteGUIFactory remoteGUIFactory = new StandardRemoteGUIFactory();
        this.remoteModel.setRemoteGUIFactory(remoteGUIFactory);	
	}
	
	private void createRemoteDeviceFactory() {
        Log.v(TAG, "Create Remote Device Factory");
        this.remoteDeviceFactory = new RemoteDeviceFactory();
        this.remoteDeviceFactory.addListener((RemoteModelImpl)this.remoteModel);
	}
	
	
	private void createRemoteDisplayFactory() {
		Log.v(TAG, "Create Remote Display Factory");
		this.remoteDisplayFactory = new RemoteDisplayFactory();
	}	
	
	private void createRemoteCommandFactory() {
		Log.v(TAG, "Create Remote Command Factory");
		this.remoteCommandFactory = new RemoteCommandFactory();
	}
	
	public Context getContext() {
		return this.activity.getApplicationContext();
	}
	
	public Activity getActivity() {
		return activity;
	}

	public RemoteModel getRemoteModel() {
		return remoteModel;
	}

	public void setRemoteModel(RemoteModel remoteModel) {
		this.remoteModel = remoteModel;
	}

	public RemoteView getRemoteView() {
		return remoteView;
	}

	public void setRemoteView(RemoteView remoteView) {
		this.remoteView = remoteView;
	}

	public RemoteController getRemoteController() {
		return remoteController;
	}

	public void setRemoteController(RemoteController remoteController) {
		this.remoteController = remoteController;
	}

	public RemoteDeviceFactory getRemoteDeviceFactory() {
		return remoteDeviceFactory;
	}

	public void setRemoteDeviceFactory(RemoteDeviceFactory remoteDeviceFactory) {
		this.remoteDeviceFactory = remoteDeviceFactory;
	}

	public RemoteCommandFactory getRemoteCommandFactory() {
		return remoteCommandFactory;
	}

	public void setRemoteCommandFactory(RemoteCommandFactory remoteCommandFactory) {
		this.remoteCommandFactory = remoteCommandFactory;
	}

	public boolean isEmulator() {
		return emulator; 
	}

	public RemoteDisplayFactory getRemoteDisplayFactory() {
		return remoteDisplayFactory;
	}

	public void setRemoteDisplayFactory(RemoteDisplayFactory remoteDisplayFactory) {
		this.remoteDisplayFactory = remoteDisplayFactory;
	}

	public RemotePlayerView getRemotePlayerView() {
		return remotePlayerView;
	}

	public void setRemotePlayerView(RemotePlayerView remotePlayerView) {
		this.remotePlayerView = remotePlayerView;
	}
	
	/**
	 * Returns a unique Id for the running device, this value is always the same for each individual device.
	 * @return
	 */
	public String getDeviceId() {
		return Secure.getString(getContext().getContentResolver(), Secure.ANDROID_ID); 
	}
	
	/**
	 * Returns the application ID.
	 * @return
	 */
	public String getApplicationId() {
		return "FiRemote";
	}
}
