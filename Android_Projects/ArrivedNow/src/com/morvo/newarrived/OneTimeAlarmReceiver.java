


package com.rajesh.newarrived;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.morvo.newarrived.R;

public class OneTimeAlarmReceiver extends BroadcastReceiver {

	// this TAG is for debugging
	private static final String TAG = "inOneTimeAlarmReceiver";

	private static final int NOTIFICATION_ID2 = 1002;

 // this variable is for testing whether it successfully goes through
 // the method onCreate and alarm goes off.
	private boolean ifSuccessful;
	
	

	public OneTimeAlarmReceiver(){
		ifSuccessful = false; 
	}
	

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
        ifSuccessful = false;
		NotificationManager manager = 
			(NotificationManager)context.getSystemService(
					Context.NOTIFICATION_SERVICE);

		// this notification appears on top of the screen for a short time
		// when you click on the OK Button.
		Notification notification = new Notification();
		notification.icon = R.drawable.alarm;
        notification.when = System.currentTimeMillis();
        
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_INSISTENT;
        
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        
        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        notification.defaults |=Notification.DEFAULT_SOUND;
       // notification.sound=Uri.parse(preference.getString("ringtone", "DEFAULT_SOUND"));
        Log.d("ringtonebr", preference.getString("ringtone", "DEFAULT_SOUND"));
	    if(preference.getBoolean("vibrate", true))
	    	notification.defaults |= Notification.DEFAULT_VIBRATE;
	    SharedPreferences DiSp=context.getSharedPreferences("Display",context.MODE_PRIVATE);
		Editor edit1;
		edit1=DiSp.edit();
		edit1.clear();
		edit1.putInt("viewpat", 0);
		edit1.commit();

		 Intent intent1 = new Intent(context, TodoActivity.class);
	   	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
	    PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
	            intent1, PendingIntent.FLAG_CANCEL_CURRENT);
    
	   
	  
	       
	        notification.setLatestEventInfo(context, 
	            "Alarm Alert!", "Destination reached.", contentIntent);
	        
	       manager.notify(NOTIFICATION_ID2, notification);
		Log.v(TAG, "Alarm is ringing now! ");
		Toast.makeText(context, "Destination Reached",
				Toast.LENGTH_LONG).show();
	
		SharedPreferences ppre=context.getSharedPreferences("Alarm", context.MODE_PRIVATE);
		Editor edit;
		edit = ppre.edit();
		edit.clear();
		edit.putBoolean("alarmactive", false);
		edit.commit();
		
		context.stopService(new Intent(context, AlarmService.class));
		ifSuccessful = true;
		}catch(Exception e)
		{
			
		}
	}
	
	public boolean getIfSuccessful() {
		return ifSuccessful;
	}
	
	


} // class ends
