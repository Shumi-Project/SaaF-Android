package com.aozoradev.saaf;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Process;
import android.os.Build;
import androidx.appcompat.app.AppCompatDelegate;
import android.app.AlarmManager;

public class SaaFApplication extends Application {
  public void onCreate() {
    super.onCreate();
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException (Thread thread, Throwable e) {
        handleUncaughtException (thread, e);
      }
    });
  }
  
  @SuppressLint("UnspecifiedImmutableFlag")
  private void handleUncaughtException (Thread thread, Throwable e) {
    Intent intent = new Intent(getApplicationContext(), UncaughtExceptionActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("error", Log.getStackTraceString(e));
		
		PendingIntent pendingIntent = null;
		if (Build.VERSION.SDK_INT >= 23) {
		  pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_IMMUTABLE);
		} else {
		  pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
		}
		
		AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
		
		Process.killProcess(Process.myPid());
		System.exit(1);
  }
}