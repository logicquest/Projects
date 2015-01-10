
package com.rajesh.newarrived;

import com.google.android.maps.GeoPoint;
import com.morvo.newarrived.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmService extends Service {

	private static final int NOTIFICATION_ID1 = 1001;
	private static final int PENDING_INTENT_REQUEST_CODE1 = 1000001;
	private LocationManager lm;
	private Location currentLoc;
	private NotificationManager mNtf;
	private Notification ntf;

	
	private float distanceToShow;
	private GeoPoint targetgp;
	private String note;
	private SharedPreferences prefs;
	private static String pdist;
	private float tdist;
	private String ringtone;
	private static String ptime;
	public void onCreate() {
		super.onCreate();
		try{
		getPrefs();
	Log.d("alarmservice", "service called");
	
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, Integer.parseInt(ptime), Integer.parseInt(pdist),locc);
		
		mNtf = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		ntf = new Notification(R.drawable.ic_menu_notifications, "Alarm Set!", 
				System.currentTimeMillis());
		
		 ntf.flags |= Notification.FLAG_AUTO_CANCEL;
	        ntf.flags |= Notification.FLAG_SHOW_LIGHTS;
	        ntf.flags |=Notification.FLAG_ONLY_ALERT_ONCE;
	       ntf.defaults |=Notification.DEFAULT_SOUND;
	        ntf.defaults |= Notification.DEFAULT_VIBRATE;
	        ntf.defaults |= Notification.DEFAULT_LIGHTS;
		}catch(Exception e)
		{
			Toast.makeText(getApplicationContext(),"location unavailable", Toast.LENGTH_LONG).show();
		}
	      //  ntf.sound=  Uri.parse(ringtone);;
	}

	public void onDestroy(){
		super.onDestroy();
		try{
		mNtf.cancel(NOTIFICATION_ID1);

		Intent alarmIntent = new Intent(getApplicationContext(), 
				OneTimeAlarmReceiver.class);

		PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(
				getApplicationContext(), PENDING_INTENT_REQUEST_CODE1, 
				alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		pendingIntentAlarm.cancel();
		lm.removeUpdates(locc);

		Log.d("ALARMSERVICE", "current alarm is destroyed");
		}catch(Exception e)
		{
			
		}

	}


	public void onStart(Intent intent, int startId) {
	
	        getPrefs();
		try{
		prefs=getSharedPreferences("Alarm", MODE_PRIVATE);
		targetgp=new GeoPoint(prefs.getInt("Dlat", 0),prefs.getInt("Dlng", 0));
		new GeoPoint(prefs.getInt("Slat", 0),prefs.getInt("Slng", 0));
		note=prefs.getString("note", null);
		
		  Intent intent1 = new Intent(this, ArrivedNow.class);
		   	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		    PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
		            intent1, PendingIntent.FLAG_CANCEL_CURRENT);
		    
		//PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(), PENDING_INTENT_REQUEST_CODE2,new Intent(getApplicationContext(), AlarmService.class), PendingIntent.FLAG_UPDATE_CURRENT);
		ntf.setLatestEventInfo(getApplicationContext(), 
				"Alarm: " +"",
				"acquiring location...", contentIntent);

		mNtf.notify(NOTIFICATION_ID1, ntf);



		Intent alarmIntent = new Intent(getApplicationContext(), 
				OneTimeAlarmReceiver.class);
		alarmIntent.putExtra("daddr", prefs.getString("daddr", null));
		PendingIntent pendingIntentAlarm = PendingIntent.getBroadcast(
				getApplicationContext(), PENDING_INTENT_REQUEST_CODE1, 
				alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			
		lm.addProximityAlert(targetgp.getLatitudeE6()/1E6, targetgp.getLongitudeE6()/1E6,
				tdist, -1, pendingIntentAlarm);
		}catch(Exception e)
		{
			
		}
		
		
	
	}
	
	 private void getPrefs() {
         // Get the xml/preferences.xml preferences
		 try{
         SharedPreferences prefs = PreferenceManager
                         .getDefaultSharedPreferences(getBaseContext());
              
         tdist = Float.parseFloat(prefs.getString("trigdist","1000"));
         pdist = prefs.getString("pdist","500");
         ptime = prefs.getString("ptime","1000");
         ringtone=prefs.getString("ringtone", "DEFAULT_SOUND");
        Log.d("ringtoneas", ringtone);
        		 //prefs.getString("ringtone","DEFAULT_RINGTONE_URI");
		 }catch(Exception e)
		 {
			 
		 }
       
 }

	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	
	LocationListener locc=new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			
			
			try{
				currentLoc = location;
				Location target = new Location("");
				target.setLatitude(targetgp.getLatitudeE6()/1E6);
				target.setLongitude(targetgp.getLongitudeE6()/1E6);
				distanceToShow = currentLoc.distanceTo(target);  // in meters
											
				PendingIntent pi = PendingIntent.getBroadcast(
						getApplicationContext(), 0, 
						new Intent(getApplicationContext(), AlarmService.class), 
						PendingIntent.FLAG_UPDATE_CURRENT);
				note=prefs.getString("daddr", null);
				if(!note.equals(null)){
				ntf.setLatestEventInfo(getApplicationContext(), 
						"Destination: " +note , String.format("%.02f", distanceToShow / 1000)
						+ "Km"+ 
						"" + " away", pi);
				ntf.when = System.currentTimeMillis();
				mNtf.notify(NOTIFICATION_ID1, ntf);
			
				}
				Log.d("ALARMSERVICE", "location updated");
				}catch(Exception e)
				{
					
				}
			
		}
	};
	


}
