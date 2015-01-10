package com.rajesh.newarrived;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;
import com.morvo.newarrived.R;

public class GetLocation extends MapActivity {

	private MapView map = null;

	private MyLocationOverlay me = null;

	private Button set;
	private static GeoPoint pt = null;
	private boolean exset=false;
	LocationManager locationManager;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
set=(Button) findViewById(R.id.button1);
set.setOnClickListener(new OnClickListener() {
	
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		try{
		if(String.valueOf(pt.getLatitudeE6()).length()>2&&!exset){
			
		Intent i = new Intent();
		Bundle b = new Bundle();
		b.putInt("lat", pt.getLatitudeE6());
		b.putInt("lng", pt.getLongitudeE6());
		i.putExtras(b);
		setResult(RESULT_OK, i);
		finish();
			
		}
		else
		{
			Toast.makeText(getApplicationContext(),"Select Again", Toast.LENGTH_LONG).show();
		}
		}catch(Exception e)
		{
		Toast.makeText(getApplicationContext(),"Network unavailable", Toast.LENGTH_LONG).show();	
		}
		
		
	}
});
try{
		map = (MapView) findViewById(R.id.map);
		double[] gps = getGPS();
		map.getController().setCenter(getPoint(gps[0], gps[1]));
		map.getController().setZoom(17);
		map.setBuiltInZoomControls(true);

		Drawable marker = getResources().getDrawable(R.drawable.marker);

		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight());

		map.getOverlays().add(new SitesOverlay(marker));

		me = new MyLocationOverlay(this, map);
		map.getOverlays().add(me);
	
}catch(Exception e)
{
	
}
	}

	@Override
	public void onResume() {
		super.onResume();
try{
		me.enableCompass();
}catch(Exception e)
{
	
}
	}

	public void seloc(View v)
	{
		
	
		GeoPoint point=null;
		EditText et=(EditText)findViewById(R.id.ente);
		try{
	 	MapController mapController;	         
	    Geocoder geoCoder=new Geocoder(getApplicationContext());
	    List<Address> addresses = geoCoder.getFromLocationName(et.getText().toString(),3);
	    //Toast.makeText(this, et.getText().toString(),Toast.LENGTH_LONG).show();
	 for (Address address : addresses) {	
	 	int lat=(int)(address.getLatitude()*1000000);
	 	int lon=(int)(address.getLongitude()*1000000);
	 	 point=new GeoPoint(lat,lon);
	/// 	Toast.makeText(this ,lat+" lon"+lon, Toast.LENGTH_LONG).show();
	 	 map = (MapView)this.findViewById(R.id.map);
	 	 map.setBuiltInZoomControls(true);    
	 	 mapController = map.getController(); //<4>
	 	 mapController.setZoom(17);
	 	 mapController.animateTo(point);
	 	map.getController().setCenter(point);
	 	
	 	me = new MyLocationOverlay(this, map);
		map.getOverlays().add(me);
	 	exset=false;
	 
	 } 
	}catch(Exception e)
	{
	exset=true;	
		Toast.makeText(getApplicationContext(),"Enter Destination Again",Toast.LENGTH_LONG).show();
	}   
		
	
	
	
	
	}	
	
	@Override
	public void onPause() {
		super.onPause();
try{
	me.disableCompass();
}catch(Exception e)
{
	
}
	
	}
	
	 @Override
	  public boolean onKeyDown(int keyCode, KeyEvent event) {
		 try{
	    if (keyCode == KeyEvent.KEYCODE_S) {
	      map.setSatellite(!map.isSatellite());
	      return(true);
	    }
	    else if (keyCode == KeyEvent.KEYCODE_Z) {
	      map.displayZoomControls(true);
	      return(true);
	    }
		 }catch(Exception e)
		 {
			 
		 }
	    return(super.onKeyDown(keyCode, event));
	  }

	@Override
	protected boolean isRouteDisplayed() {
		return (false);
	}

	private GeoPoint getPoint(double lat, double lon) {
		return (new GeoPoint((int) (lat * 1000000.0), (int) (lon * 1000000.0)));
	}

	private double[] getGPS() {
		double[] gps=null;
		try{
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = lm.getProviders(true);

		/*
		 * Loop over the array backwards, and if you get an accurate location,
		 * then break out the loop
		 */
		Location l = null;

		for (int i = providers.size() - 1; i >= 0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null)
				break;
		}

		 gps = new double[2];
		if (l != null) {
			gps[0] = l.getLatitude();
			gps[1] = l.getLongitude();
		}
		}catch(Exception e)
		{
			
		}
		
		
		return gps;
	}

	private class SitesOverlay extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> items = new ArrayList<OverlayItem>();
		private Drawable marker = null;
		private OverlayItem inDrag = null;
		private ImageView dragImage = null;
		private int xDragImageOffset = 0;
		private int yDragImageOffset = 0;
		private int xDragTouchOffset = 0;
		private int yDragTouchOffset = 0;

		public SitesOverlay(Drawable marker) {
			super(marker);
			this.marker = marker;
			dragImage = (ImageView) findViewById(R.id.drag);
			xDragImageOffset = dragImage.getDrawable().getIntrinsicWidth() / 2;
			yDragImageOffset = dragImage.getDrawable().getIntrinsicHeight();
			double[] loc = getGPS();
			items.add(new OverlayItem(getPoint(loc[0], loc[1]), "Vellore",
					"India"));

			populate();
		}

		private double[] getGPS() {
			double[] gps=null;
			try{
			LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			List<String> providers = lm.getProviders(true);
			Location l = null;
			for (int i = providers.size() - 1; i >= 0; i--) {
				l = lm.getLastKnownLocation(providers.get(i));
				if (l != null)
					break;
			}
			 gps = new double[2];
				if (l != null) {
					gps[0] = l.getLatitude();
					gps[1] = l.getLongitude();
				}
				
			}catch(Exception e)
			{
				
			}
			return gps;
		}

		@Override
		protected OverlayItem createItem(int i) {
			return (items.get(i));
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			super.draw(canvas, mapView, shadow);
			try{
			boundCenterBottom(marker);
			}catch(Exception e)
			{
				
			}
		}

		@Override
		public int size() {
			return (items.size());
		}

		@Override
		public boolean onTouchEvent(MotionEvent event, MapView mapView) {
			
			final int action = event.getAction();
			final int x = (int) event.getX();
			final int y = (int) event.getY();
			if(event.getAction()==1)
			{
				try{
			pt = map.getProjection().fromPixels(x, y);
			boolean result = false;
			items.clear();
			items.add(new OverlayItem(pt," "," "));
			populate();
			
				}catch(Exception e)
				{
					return false;	
				}
				return true;
			}
			else
			{
				return false;
			}		
			
		}



	}

	
	
}
