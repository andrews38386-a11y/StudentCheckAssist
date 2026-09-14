package com.studentcheckassist;

import android.app.*;import android.os.*;import android.content.*;import android.content.pm.PackageManager;import android.graphics.Bitmap;import android.net.Uri;import android.provider.Settings;import android.webkit.*;import java.util.*;

public class MainActivity extends Activity {
 WebView web; final int PICK=1001;
 @Override public void onCreate(Bundle b){super.onCreate(b); web=new WebView(this); setContentView(web); setup(); if(Build.VERSION.SDK_INT>=33) requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},9); web.loadUrl("file:///android_asset/index.html");}
 void setup(){ WebSettings s=web.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); s.setAllowContentAccess(true); web.setWebViewClient(new WebViewClient()); web.setWebChromeClient(new WebChromeClient(){ public boolean onShowFileChooser(WebView v,ValueCallback<Uri[]> cb,FileChooserParams p){ Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("image/*");i.addCategory(Intent.CATEGORY_OPENABLE); startActivityForResult(i,PICK); return true; }}); web.addJavascriptInterface(new Bridge(this),"Android"); }
 @Override public void onBackPressed(){if(web.canGoBack())web.goBack();else super.onBackPressed();}
 public static class Bridge { Context c; Bridge(Context c){this.c=c;} @JavascriptInterface public void schedule(String title,long at){ Intent i=new Intent(c,AlarmReceiver.class).putExtra("title",title); PendingIntent p=PendingIntent.getBroadcast(c,(int)(at%Integer.MAX_VALUE),i,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE); AlarmManager am=(AlarmManager)c.getSystemService(ALARM_SERVICE); if(Build.VERSION.SDK_INT>=31&&!am.canScheduleExactAlarms()){try{c.startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));}catch(Exception e){} return;} am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p); }}
}
