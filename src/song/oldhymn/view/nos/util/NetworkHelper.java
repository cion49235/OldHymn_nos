package song.oldhymn.view.nos.util;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import song.oldhymn.view.nos.HymnApp;

public class NetworkHelper {
	private BroadcastReceiver mConnReceiver	= null;
	private ConnectivityManager mConnectivityManager= null;
	private boolean mIsWifi = false;
	private boolean mIs3G = false;
	private final Application mApp;

	private static NetworkHelper mINSTANCE = new NetworkHelper();

	public static NetworkHelper getInstance(){
		return mINSTANCE;
	}

	private NetworkHelper(){
		mApp = HymnApp.getApplication();
		mConnectivityManager = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
		init();
		loadNetStatus();
	}

	public void init(){
		//receiver�� ����Ѵ�.
		if( mConnReceiver == null ){
			mConnReceiver = new ConnectionReceiver();
			mApp.registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
		}
	}

	/**
	 * WIFI ���ӻ��¸� �����Ѵ�.
	 * @return
	 */
	public boolean isWIFIConneced(){
		return mIsWifi;
	}

	/**
	 * 3G���� ���� ���¸� �����Ѵ�.
	 * @return
	 */
	public boolean is3GConnected(){
		return mIs3G;
	}

	public boolean isAvailable(){
		return isWIFIConneced() || is3GConnected();
	}

	public void close(){
		mApp.unregisterReceiver(mConnReceiver);
		//mINSTANCE = null;
	}

	/**
	 * ��Ʈ��ũ ���¸� �ε��Ѵ�.
	 */
	public void loadNetStatus(){
		//ConnectivityManager�� �Ҵ�Ǳ� ���̸� �Ҵ��Ѵ�.
		try{
			if( null != mApp && mConnectivityManager == null)
				mConnectivityManager = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			NetworkInfo mobile = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			
			if( wifi!= null && wifi.isAvailable() && wifi.isConnected()){
				NetworkHelper.this.mIsWifi = true;
				NetworkHelper.this.mIs3G = false;
			}else if(mobile != null && mobile.isAvailable() && mobile.isConnected()){
				NetworkHelper.this.mIsWifi = false;
				NetworkHelper.this.mIs3G = true;
			}else{
				NetworkHelper.this.mIsWifi = false;
				NetworkHelper.this.mIs3G = false;
			}
		}catch (NullPointerException e) {
		}
	}

	/**
	 * BroadcastReceiver
	 * @author jmlee
	 *
	 */
	public class ConnectionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if( ConnectivityManager.CONNECTIVITY_ACTION.equals(action) )
				loadNetStatus();
		}
	}
}
