package song.oldhymn.view.nos;


import android.app.Application;
import song.oldhymn.view.nos.util.NetworkHelper;


public class HymnApp extends Application{
	private static HymnApp INSTANCE			= null;
	/* (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
	@Override
	public void onCreate() {
		INSTANCE = this;
	}

	public static HymnApp getApplication(){
		return INSTANCE;
	}


	/* (non-Javadoc)
	 * @see android.app.Application#onTerminate()
	 */
	@Override
	public void onTerminate() {

		NetworkHelper.getInstance().close();

		INSTANCE = null;
		super.onTerminate();
	}
	
	public static HymnApp getInstance() {
		return INSTANCE;
  }
}
