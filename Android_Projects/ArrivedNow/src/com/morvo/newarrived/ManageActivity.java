package com.rajesh.newarrived;



import com.google.android.maps.GeoPoint;
import com.morvo.newarrived.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class ManageActivity extends Activity{
	private TextView dcoords;
	private Location b;
	private TextView distance;
	private float dist=0;
	private SharedPreferences prefs;
	private ToggleButton toggle;
	private ImageButton addnote;
	private LocationManager locationManager;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.managenew);		
		prefs = getSharedPreferences("Alarm",Context.MODE_PRIVATE);		
		distance=(TextView) findViewById(R.id.distancev);
		dcoords=(TextView) findViewById(R.id.distance);
		toggle=(ToggleButton) findViewById(R.id.toggleButton1);
	    addnote=(ImageButton) findViewById(R.id.newnote);
		addnote.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
				if(prefs.getBoolean("alarmactive", false)){
					
				Intent noteintent=new Intent(getApplicationContext(),TodoActivity.class);
				noteintent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			
				SharedPreferences DiSp=getApplicationContext().getSharedPreferences("Display",getApplicationContext().MODE_PRIVATE);
				Editor edit1;
				edit1=DiSp.edit();
				edit1.clear();
				edit1.putInt("viewpat", 1);
				edit1.commit();
				startActivity(new Intent(getApplicationContext(),TodoActivity.class));
				}
				else
					Toast.makeText(getApplicationContext(), "Set Alarm", Toast.LENGTH_SHORT).show();
			
			}catch(Exception e)
			{
				
			}
		}});
		//Toast.makeText(getApplicationContext(),prefs.getBoolean("alarmactive", false)+ "deii " , Toast.LENGTH_LONG).show();
		if(prefs.getBoolean("alarmactive", false)){
			String nw="";
			try{
				
				String eded=prefs.getString("daddr", null);
				String spl[]=eded.split("\n");
				int ct=0;
				for(int z=0;z<spl.length;z++)
				{
					
					if(spl[z].length()>0)
					{			
					
					if(ct==0)	{
					nw=nw+spl[z]+","; ct++;}
					else
					nw=nw+spl[z];
					}
				}
				//Toast.makeText(getApplicationContext(),nw, Toast.LENGTH_LONG).show();
	    dcoords.setText(nw);			 
	    GeoPoint dp=new GeoPoint(prefs.getInt("Dlat", 0),prefs.getInt("Dlng", 0));
		toggle.setChecked(true);	
		
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager)getSystemService(context);	  
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		String provider = locationManager.getBestProvider(criteria, true);
		Location location = locationManager.getLastKnownLocation(provider);
		updateWithNewLocation(location);
		locationManager.requestLocationUpdates(provider, 2000, 10,locationListener);		 
		Location  a = new Location("");
		a.setLatitude(location.getLatitude());
		a.setLongitude(location.getLongitude());
	    b = new Location("");
		b.setLatitude(dp.getLatitudeE6()/ 1E6);
		b.setLongitude(dp.getLongitudeE6()/ 1E6 );		
		dist = a.distanceTo(b);
		distance.setText((String.format("%.02f", dist / 1000)+ "Km"));		
			
			}catch(Exception e)
			{
				
			}
		}
		else
		{
			dcoords.setText("No Alarm Set !!");				
			toggle.setChecked(false);
		}
		
	}
	public void onToggleClicked(View v) {
	    // Perform action on clicks
	    if (((ToggleButton) v).isChecked()) {
	    	try{
	      //  Toast.makeText(ManageActivity.this, "Alarm on", Toast.LENGTH_SHORT).show();
	        startActivity(new Intent(getApplicationContext(),ArrivedNow.class));
	    	}catch(Exception e)
	    	{
	    		
	    	}
	    }	    
	    else
	    { 	
	    	try{
	      //  Toast.makeText(ManageActivity.this, "Alarm off", Toast.LENGTH_SHORT).show();
	        Intent intentAlarmService =new Intent(v.getContext(), AlarmService.class);
			stopService(intentAlarmService);
			Editor edit=prefs.edit();
			edit.clear();
			edit.commit();
			Intent tintent=new Intent(getApplicationContext(),ArrivedNow.class);
			tintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(tintent);
	    	}catch(Exception e)
	    	{
	    		
	    	}
	    }
	}
	private final LocationListener locationListener = new LocationListener() {
		    public void onLocationChanged(Location location) {
		      updateWithNewLocation(location);
		    }
			 
		    public void onProviderDisabled(String provider){
		      updateWithNewLocation(null);
		    }

		    public void onProviderEnabled(String provider){ }
		    public void onStatusChanged(String provider, int status, 
		                                Bundle extras){ }
		  }; 
		  
     private void updateWithNewLocation(Location location) {
    	 try{
    	 if(location!=null)
    	 {	float upddist = location.distanceTo(b);
		    distance.setText((String.format("%.02f", upddist / 1000)
					+ "Km"));}
    	 else distance.setText((String.format("%.02f", dist / 1000)
					+ "Km"));
		  }
    	 catch(Exception e)
    	 	{
    	 
    	 	}
     }
	 public void onBackPressed() {

			new AlertDialog.Builder(this)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Quit?")
					.setMessage("Really Quit?")
					.setPositiveButton("yes",
							new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {

									prefs =getSharedPreferences("Alarm",Context.MODE_PRIVATE);
									//Toast.makeText(getApplicationContext(),prefs.getBoolean("alarmactive", false)+" ", Toast.LENGTH_LONG).show();
									if(!prefs.getBoolean("alarmactive", false)){
										try{
										locationManager.removeUpdates(locationListener);
										}catch(Exception e)
										{
											
										}
									}
									// Stop the activity
									Log.d("backerror", "onBackPressed Called");
									Intent setIntent = new Intent(
											Intent.ACTION_MAIN);
									setIntent.addCategory(Intent.CATEGORY_HOME);
									setIntent
											.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									startActivity(setIntent);
									
									
									ManageActivity.this.finish();
								}

							}).setNegativeButton("no", null).show();

		}

}
