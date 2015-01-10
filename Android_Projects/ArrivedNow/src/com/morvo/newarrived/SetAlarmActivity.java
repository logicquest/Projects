package com.rajesh.newarrived;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import android.provider.Settings;
import com.google.android.maps.GeoPoint;
import com.morvo.newarrived.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SetAlarmActivity extends Activity {

public static final int requestCode = 1;


private Geocoder geocoder;
private List<Address> addresses;
View layout=null;
private EditText destination;
private EditText source;
TextView text=null;
private ImageButton getdest;
private Button setalarm;
private GeoPoint gp;
private ImageButton refresh;
private Boolean isset=false;
private SharedPreferences prefs;
private Button clear;
private Location location;
LocationManager locationManager;
ImageView image=null;
public void onref()

{
	try{
		
	    String context = Context.LOCATION_SERVICE;
	    locationManager = (LocationManager)getSystemService(context);
	   if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
	   {
		   locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 10,locationListener);
	    
	    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	    updateWithNewLocation(location);
	    
	    
	   }
	   else
	   {
		   
		   Toast.makeText(this, "SWITCH ON GPS", Toast.LENGTH_LONG).show();
	   }
	   
	}catch(Exception e)
	{
		Toast.makeText(this, "location unavailable",Toast.LENGTH_LONG ).show();
	}	
}


@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	setContentView(R.layout.setlocation);
	
	
	
	try{
		
		clear=(Button) findViewById(R.id.clear);
		setalarm=(Button) findViewById(R.id.button3);
		
		
		
		final ImageView imageView = (ImageView) findViewById(R.id.mk);
	    imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
		
		//Toast.makeText(getApplicationContext(),clear.getWidth()+setalarm.getWidth()+" ", Toast.LENGTH_LONG).show();
		final ImageView imageView1 = (ImageView) findViewById(R.id.dk);
	    imageView1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
		
		
		
		
		onref();
		
	}catch(Exception e)
	{
		
		
	}
	
	
	
	
try{
	destination=(EditText) findViewById(R.id.destLoc);
	source=(EditText) findViewById(R.id.currLoc);
	geocoder = new Geocoder(this, Locale.ENGLISH);	
	getdest=(ImageButton) findViewById(R.id.setfrommap);
	refresh=(ImageButton) findViewById(R.id.refreshcurr);
	


	
	
}catch(Exception e)
{
	
}
	refresh.setOnClickListener(new OnClickListener() {
		


		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			try{
				final ImageView imageView = (ImageView) findViewById(R.id.mk);
			    imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
			    
		    onref();
			}catch(Exception e){
			Toast.makeText(getApplicationContext(),"Location Unavailable", Toast.LENGTH_LONG).show();	
			}
		}
	});
	
	clear.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			isset=false;
			destination.setText("");
			clear.setVisibility(4);
		
			setalarm.setVisibility(4);
		
		
			
			}
	});
	getdest.setOnClickListener(new OnClickListener() {
		
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			startActivityForResult(new Intent(getApplicationContext(),GetLocation.class),requestCode );
		}
	});
	//fillsource();
	
	setalarm.setOnClickListener(new OnClickListener() {
		
		
		private Editor edit;

		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
			try{
						
				prefs = getSharedPreferences("Alarm",Context.MODE_PRIVATE);
			//	Toast.makeText(getApplicationContext(),prefs.getBoolean("alarmactive", false)+" ", Toast.LENGTH_LONG).show();
				if(!prefs.getBoolean("alarmactive", false)){				
				execontrue(arg0);
				SharedPreferences ppre1=getApplicationContext().getSharedPreferences("Alarm1", getApplicationContext().MODE_PRIVATE);
				Editor edit2;
				edit2 = ppre1.edit();
				edit2.clear();
				edit2.putString("todos",null);
				edit2.commit();				
				
				}else
				{
					
				 Intent intentAlarmService =new Intent(getApplicationContext(), AlarmService.class);
				 stopService(intentAlarmService);				 
				 execontrue(arg0);
					SharedPreferences ppre1=getApplicationContext().getSharedPreferences("Alarm1", getApplicationContext().MODE_PRIVATE);
					Editor edit2;
					edit2 = ppre1.edit();
					edit2.clear();
					edit2.putString("todos",null);
					edit2.commit();
				 
				 
				}
			
			}catch(Exception e)
			{
				
			}
		}
	});
}



public void execontrue(View arg0)
{try{
	Editor edit;
	if(isset)
	{
		if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			prefs=getSharedPreferences("Alarm",MODE_PRIVATE);
			edit = prefs.edit();
			edit.clear();
			edit.putInt("Dlat", pt.getLatitudeE6() );
			edit.putInt("Dlng", pt.getLongitudeE6() );
			edit.putString("daddr",destination.getText().toString() );		
			edit.putBoolean("alarmactive", true);
			edit.commit();	
			Toast.makeText(getApplicationContext(), "Alarm details saved!",Toast.LENGTH_SHORT).show();
			Intent intentAlarmService = 
				new Intent(getApplicationContext(), AlarmService.class);
			startService(intentAlarmService);
			Intent i=new Intent(getApplicationContext(),ArrivedNow.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			
		}
		else{
			
			  
		}
		}
	else
    Toast.makeText(getApplicationContext(), "select destination", Toast.LENGTH_SHORT).show();
}
	catch(Exception e)
	{
Toast.makeText(getApplicationContext(),e.toString(), Toast.LENGTH_LONG).show();
	}				

}

@Override
public void onPause()
{
	super.onPause();
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
    String saddress;
    if (location != null) {
      double lat = location.getLatitude();
      double lng = location.getLongitude();
      GeoPoint newgp =getPoint(lat, lng);
     saddress = getAddress(newgp);
    } else {
      saddress = "No address found"; 
      
    }
   source.setText(saddress);
	  }catch(Exception e)
	  {
		  
	  }
  }

/*private void fillsource() {
	// TODO Auto-generated method stub
	double[] gps=getGPS();
	gp = getPoint(gps[0], gps[1]);
	source.setText(getAddress(gp));

}*/


private GeoPoint getPoint(double lat, double lon) {
	return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));}

private GeoPoint pt;
 

/*double[] getGPS() {
	LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	List<String> providers = lm.getProviders(true);

	
	Location l = null;

	for (int i = providers.size() - 1; i >= 0; i--) {
		l = lm.getLastKnownLocation(providers.get(i));
		if (l != null)
			break;
	}

	double[] gps = new double[2];
	if (l != null) {
		gps[0] = l.getLatitude();
		gps[1] = l.getLongitude();
	}
	return gps;
}*/
String getAddress(GeoPoint gp){
	
	
	try {
		if(haveInternet())
		addresses = geocoder.getFromLocation(gp.getLatitudeE6() / 1E6,
				gp.getLongitudeE6() / 1E6, 1);

		if (addresses != null) {
			Address returnedAddress = addresses.get(0);
			StringBuilder strReturnedAddress = new StringBuilder("\n");
			for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
				strReturnedAddress
						.append(returnedAddress.getAddressLine(i)).append(
								"\n");
			}
			isset=true;
			return strReturnedAddress.toString();
		} else {
			String address="No Address Found!";
			isset=false;
			return address;
		}
	} catch (IOException e) {
		// TODO Auto-generated catch block
		isset=false;
		e.printStackTrace();
		String address="Working...!";
		Toast.makeText(getApplicationContext(), "Network Unavailable", Toast.LENGTH_SHORT).show();
		return address;
	}
	
	}
private boolean haveInternet(){
	try{
	NetworkInfo info = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).
		    getActiveNetworkInfo();
    if (info==null || !info.isConnected()) {
            return false;
    }
    if (info.isRoaming()) {
            // here is the roaming option you can change it if you want to disable internet while roaming, just return false
            return true;
    }
   
	}catch(Exception e)
	{
		
	}
	 return true;
}
@Override
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    // If the request went well (OK) and the request was PICK_CONTACT_REQUEST
    if (resultCode == Activity.RESULT_OK && requestCode == 1) {
        // Perform a query to the contact's content provider for the contact's name
    	try{
    	Bundle b = new Bundle();
    	b = data.getExtras();
    	pt = new GeoPoint(b.getInt("lat"), b.getInt("lng"));
    	destination.setText(getAddress(pt)); 
    	//Toast.makeText(getApplicationContext(),pt.getLatitudeE6()+" ", Toast.LENGTH_LONG).show();
    	if(isset){
			
			clear.setVisibility(0);
			clear.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha));
			setalarm.setVisibility(0);
			setalarm.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha));
			final ImageView imageView1 = (ImageView) findViewById(R.id.dk);
			  imageView1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
			
			
		}
    	
    	
    	}catch(Exception e){
    		isset=false;
    		Toast.makeText(getApplicationContext(), "Set Location Details Again", Toast.LENGTH_LONG).show();
    	}
    	

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

							// Stop the activity
							Log.d("backerror", "onBackPressed Called");
							Intent setIntent = new Intent(
									Intent.ACTION_MAIN);
							setIntent.addCategory(Intent.CATEGORY_HOME);
							setIntent
									.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(setIntent);
							SetAlarmActivity.this.finish();
							
							prefs =getSharedPreferences("Alarm",Context.MODE_PRIVATE);
							//Toast.makeText(getApplicationContext(),prefs.getBoolean("alarmactive", false)+" ", Toast.LENGTH_LONG).show();
							if(!prefs.getBoolean("alarmactive", false)){
								try{
								
								locationManager.removeUpdates(locationListener);
								}catch(Exception e)
								{
									
								}
								
							}

							
							
						}

					}).setNegativeButton("no", null).show();

}

}






