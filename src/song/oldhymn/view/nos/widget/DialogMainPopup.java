package song.oldhymn.view.nos.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import song.oldhymn.view.nos.R;
import song.oldhymn.view.nos.util.PreferenceUtil;

public class DialogMainPopup extends Dialog implements View.OnClickListener {
    Context context;
    Activity activity;
    Handler handler = new Handler();
    Button bt_close, bt_market;
    CheckBox cb_today_close;
    TextView txt_mainpopup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_mainpopup);
        setLayoutDialog();
        init_ui();
    }


    private void setLayoutDialog() {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        params.dimAmount = 0.6f;
        params.width = (int) (getScreenWidth(context) * 0.8);
        params.height = (int) (getScreenHeight(context) * 0.5);
        getWindow().setAttributes(params);
    }


    private static int m_screenWidthPixcels = -1;
    private static int m_screenHeightPixcels = -1;
    public int getScreenWidth(Context ctx)
    {
        if(  m_screenWidthPixcels == -1 ) {
            DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
            m_screenWidthPixcels = metrics.widthPixels;
        }

        return m_screenWidthPixcels;
    }

    public int getScreenHeight(Context ctx)
    {
        if(  m_screenHeightPixcels == -1 ) {
            DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
            m_screenHeightPixcels = metrics.heightPixels;
        }

        return m_screenHeightPixcels;
    }


    private void init_ui(){
        txt_mainpopup = (TextView)findViewById(R.id.txt_mainpopup);
        txt_mainpopup.setText(PreferenceUtil.getStringSharedData(context, PreferenceUtil.PREF_TV_RECOMMEND, ""));
        bt_close = (Button)findViewById(R.id.bt_close);
        bt_market = (Button)findViewById(R.id.bt_market);
        bt_close.setOnClickListener(this);
        bt_market.setOnClickListener(this);
        cb_today_close = (CheckBox)findViewById(R.id.cb_today_close);
    }

    public DialogMainPopup(Context context, Activity activity) {
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onClick(View view) {
       if(view == bt_close){
           handler.postDelayed(new Runnable() {
               @Override
               public void run() {
                   if(cb_today_close.isChecked()){
                       PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_RECOMMEND_POPUP, true);
                   }else{
                       PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_RECOMMEND_POPUP, false);
                   }
                   dismiss();
               }
           },500);
       }else if(view == bt_market){
           String packageName = "";
           try {
               PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
               packageName = PreferenceUtil.getStringSharedData(context,PreferenceUtil.PREF_PK_RECOMMEND_NAME, "");
               context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
           } catch (PackageManager.NameNotFoundException e) {
           } catch (ActivityNotFoundException e) {
               context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packageName)));
           }
           if(cb_today_close.isChecked()){
               PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_RECOMMEND_POPUP, true);
           }else{
               PreferenceUtil.setBooleanSharedData(context, PreferenceUtil.PREF_RECOMMEND_POPUP, false);
           }
           dismiss();
       }
    }
}