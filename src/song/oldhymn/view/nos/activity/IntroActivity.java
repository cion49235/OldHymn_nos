package song.oldhymn.view.nos.activity;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import song.oldhymn.view.nos.MainFragmentActivity;
import song.oldhymn.view.nos.R;
import song.oldhymn.view.nos.util.PreferenceUtil;
import song.oldhymn.view.nos.util.StringUtil;
import song.oldhymn.view.nos.widget.DialogServicePopup;



public class IntroActivity extends Activity{
	public Handler handler;
	public Context context;
	public boolean retry_alert = false;
	public static Activity activity;
	public static LinearLayout bg_intro;
    public static int background_type = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_intro);
        context = this;
        activity = this;
        retry_alert = true;
        alert_view = true;
        billing_process();//인앱정기결제체크
        
        adstatus_async = new Adstatus_Async();
        adstatus_async.execute();
        
    }
    
    
    private Adstatus_Async adstatus_async = null;
    public class Adstatus_Async extends AsyncTask<String, Integer, String> {
    	String version;
        String service_status;
        String recommend_status;
        String tv_service;
        String tv_recommend;
        String pk_recommend_name;
        HttpURLConnection localHttpURLConnection;
        public Adstatus_Async(){
        }
        @Override
        protected String doInBackground(String... params) {
            String sTag;
            try{
                String str = "http://cion49235.cafe24.com/cion49235/oldhymn_nos/ad_status2.php";
                localHttpURLConnection = (HttpURLConnection)new URL(str).openConnection();
                localHttpURLConnection.setFollowRedirects(true);
                localHttpURLConnection.setConnectTimeout(15000);
                localHttpURLConnection.setReadTimeout(15000);
                localHttpURLConnection.setRequestMethod("GET");
                localHttpURLConnection.connect();
                InputStream inputStream = new URL(str).openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput(inputStream, "EUC-KR");
                int eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_DOCUMENT) {
                    }else if (eventType == XmlPullParser.END_DOCUMENT) {
                    }else if (eventType == XmlPullParser.START_TAG){
                        sTag = xpp.getName();
                        if(sTag.equals("version")){
                            version = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_VERSION, version);
                            Log.i("dsu", "version : " + version);
                        }else if(sTag.equals("service_status")){
                            service_status = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_SERVICE_STATUS, service_status);
                            Log.i("dsu", "service_status : " + service_status);
                        }else if(sTag.equals("recommend_status")){
                            recommend_status = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_RECOMMEND_STATUS, recommend_status);
                            Log.i("dsu", "recommend_status : " + recommend_status);
                        }else if(sTag.equals("tv_service")){
                            tv_service = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_TV_SERVICE, tv_service);
                            Log.i("dsu", "tv_service : " + tv_service);
                        }else if(sTag.equals("tv_recommend")){
                            tv_recommend = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_TV_RECOMMEND, tv_recommend);
                            Log.i("dsu", "tv_recommend : " + tv_recommend);
                        }else if(sTag.equals("pk_recommend_name")){
                            pk_recommend_name = xpp.nextText()+"";
                            PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_PK_RECOMMEND_NAME, pk_recommend_name);
                            Log.i("dsu", "pk_recommend_name : " + pk_recommend_name);
                        }
                    } else if (eventType == XmlPullParser.END_TAG){
                        sTag = xpp.getName();
                        if(sTag.equals("Finish")){
                        }
                    } else if (eventType == XmlPullParser.TEXT) {
                    }
                    eventType = xpp.next();
                }
            }
            catch (SocketTimeoutException localSocketTimeoutException)
            {
            }
            catch (ClientProtocolException localClientProtocolException)
            {
            }
            catch (IOException localIOException)
            {
            }
            catch (Resources.NotFoundException localNotFoundException)
            {
            }
            catch (java.lang.NullPointerException NullPointerException)
            {
            }
            catch (Exception e)
            {
            }
            return service_status;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            version_check();
        }
        @Override
        protected void onPostExecute(String service_status) {
            super.onPostExecute(service_status);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    
    int versionCode;
    @SuppressWarnings("deprecation")
	private void version_check(){
        PackageInfo pi=null;
        try {
            pi = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        } catch (NullPointerException e){
        } catch (Exception e){
        }
        if ( (versionCode < Integer.parseInt(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_VERSION, "1"))) && (versionCode > 0) ) {
           android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//            builder.setIcon(R.drawable.icon128);
            builder.setTitle(context.getString(R.string.alert_update_01));
            builder.setMessage(context.getString(R.string.alert_update_02));
            builder.setCancelable(false);
            builder.setPositiveButton(Html.fromHtml("<font color='#f57c00'>'"+context.getString(R.string.alert_update_03)+"'</font>"), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int whichButton){
                    String packageName = "";
                    try {
                        @SuppressWarnings("unused")
						PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        packageName = getPackageName();
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    } catch (PackageManager.NameNotFoundException e) {
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
                    }
                }
            });
            builder.setNegativeButton(Html.fromHtml("<font color='#f57c00'>'"+context.getString(R.string.alert_update_05)+"'</font>"), new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int whichButton){
                    finish();
                }
            });
            android.app.AlertDialog myAlertDialog = builder.create();
            myAlertDialog.show();
        }
        else {
            handler = new Handler();
            handler.postDelayed(runnable, 2000);
        }
    }
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	alert_view = false;
    	retry_alert = false;
    	if(handler != null){
    		handler.removeCallbacks(runnable);
    	}
    	if (bp != null)
            bp.release();
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    }
    @Override
    protected void onPause() {
    	super.onPause();
    }
    
    @Override
    protected void onStart() {
    	super.onStart();
    }
    
    public String MillToDate(long mills) {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String date = (String) formatter.format(new Timestamp(mills));
        return date;
    }
    
    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton("OK", null);
        Log.d("dsu", "Showing alert dialog: " + message);
        bld.create().show();
    }
    
    public void go_main(){
    	Intent intent = new Intent(context, MainFragmentActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
		//fade_animation
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
    
    private BillingProcessor bp;
    private static final String SUBSCRIPTION_ID = "song.oldhymn.inapp.month";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoNHiFif6BUrIB33Qn2qXcrGPnZvkBZPj+qn5TJU1AV2M8INxVj0C9M19wl7RT+FA4xfR1+4qVUGwbgxsiy0+lzwxLB9ItSanXC3fwB/sE+/w5dSP6k2rOHuZspKrGdeYHtCmHhz/fBzsO7ONPgP94bHFpin2zfOvAnmG1yItj/YWATKXoQasisPQsFV1orhBuNWMVoqTXfXFl+q3zZsTj0qEoYi+wwJdU60BmsbwVY22HMW4LvRc41vO/Qw1KjdRMKImLaKMGjJLUL5j2IIcqf/RncK4bg1XQ/7x1j+cvrVUGo7ora72d9P2JSvasNtFgdT1i04eKmx3Qy/Ee65BYwIDAQAB";
    private void billing_process(){
        if(!BillingProcessor.isIabServiceAvailable(this)) {
        }
        bp = new BillingProcessor(this, LICENSE_KEY, new BillingProcessor.IBillingHandler() {
            @Override
            public void onBillingInitialized() {
                try{
                    bp.loadOwnedPurchasesFromGoogle();
                    Log.i("dsu", "isSubscriptionUpdateSupported : " + bp.isSubscriptionUpdateSupported());
                    Log.i("dsu", "getSubscriptionTransactionDetails : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID));
                    Log.i("dsu", "isSubscribed : " + bp.isSubscribed(SUBSCRIPTION_ID));
                    Log.i("dsu", "autoRenewing : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.autoRenewing);
                    Log.i("dsu", "purchaseTime : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.purchaseTime);
                    Log.i("dsu", "purchaseState : " + bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.purchaseState);
                    PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, Boolean.toString(bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID).purchaseInfo.purchaseData.autoRenewing));
                }catch (NullPointerException e){
                }
            }
            
            @Override
            public void onPurchaseHistoryRestored() {
//            	showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts()){
                    Log.i("dsu", "Owned Managed Product: " + sku);
//                    showToast("Owned Managed Product: " + sku);
                }
                for(String sku : bp.listOwnedSubscriptions()){
                    Log.i("dsu", "Owned Subscription: " + sku);
//                    showToast("Owned Subscription : " + sku);
                }
            }

			@Override
			public void onProductPurchased(String arg0, TransactionDetails arg1) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void onBillingError(int arg0, Throwable arg1) {

			}
        });
    }
    
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("dsu", "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            Log.i("dsu", "인앱결제 requestCode : " + requestCode);
            if (requestCode == 32459) {
                int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
                String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
                String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");//this is the signature which you want
                Log.i("dsu", "purchaseData : " +  purchaseData);
                if(TextUtils.isEmpty(purchaseData)) {
                	show_inapp_alert();
                	return;
                }
                if (resultCode == RESULT_OK) {
                    try {
                        JSONObject jo = new JSONObject(purchaseData);//this is the JSONObject which you have included in Your Question right now
                        String orderId = jo.getString("orderId");
                        String packageName = jo.getString("packageName");
                        String productId = jo.getString("productId");
                        String purchaseTime = jo.getString("purchaseTime");
                        String purchaseState = jo.getString("purchaseState");
                        String purchaseToken = jo.getString("purchaseToken");
                        String autoRenewing = jo.getString("autoRenewing");
                        String format_purchaseTime = MillToDate(Long.parseLong(purchaseTime));
                        Log.i("dsu", "구글주문아이디 " +  orderId + "\n어플리케이션 패키지이름 : " + packageName + "\n아이템 상품 식별자 : " + productId + "\n상품 구매가 이루어진 시간 : " + format_purchaseTime + "\n주문의 구매 상태 : " + purchaseState + "\n구매를 고유하게 식별하는 토큰값 : " + purchaseToken + "\n자동갱신여부 : " + autoRenewing);
                        if(!StringUtil.isEmpty(purchaseState) && autoRenewing.equals("true")) {
                        	PreferenceUtil.setStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, autoRenewing);	
                        	Intent intent = new Intent(context, MainFragmentActivity.class);
            				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            				startActivity(intent);
            				finish();
                        }
                    }
                    catch (JSONException e) {
                        alert("Failed to parse purchase data.");
                        e.printStackTrace();
                    }
                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    private void show_inapp_alert() {
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setTitle(context.getString(R.string.txt_inapp_alert_title));
		builder.setMessage(context.getString(R.string.txt_inapp_alert_ment));
		builder.setInverseBackgroundForced(true);
		builder.setNeutralButton(context.getString(R.string.txt_inapp_alert_yes), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				bp.subscribe(IntroActivity.this,SUBSCRIPTION_ID);
			}
		});
		builder.setNegativeButton(context.getString(R.string.txt_inapp_alert_no), new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface dialog, int whichButton){
				Intent intent = new Intent(context, MainFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
             	dialog.dismiss();
			}
		});
		AlertDialog myAlertDialog = builder.create();
		if(retry_alert) myAlertDialog.show();
    }
    
    Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_SERVICE_STATUS, "Y").equals("Y")){
				/*if(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_ISSUBSCRIBED, Const.isSubscribed).equals("true")) {
				Intent intent = new Intent(context, MainFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			}else {
				show_inapp_alert();	
			}*/
				Intent intent = new Intent(context, MainFragmentActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
				//fade_animation
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);	
			}else {
				service_popup();
			}
		}
	};
	
	private boolean alert_view = false;
	private void service_popup(){
        DialogServicePopup dialog =  new DialogServicePopup(context, activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        if(alert_view) dialog.show();
    }
	
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if(handler != null) handler.removeCallbacks(runnable);
		finish();
	}
}
