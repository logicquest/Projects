package com.mar;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class MarakannaActivity extends MapActivity implements LocationListener, OnInitListener { //<1>
	GeoPoint point;
	int stepno=1;
	  int btnset=0;
  private static final String TAG = "LocationActivity";
  List<Address> sampaddr,voiceaddr;
  String giga,giga1,giga2;
  String one,two,three,four;
  Location now;
  String s=new String();
  EditText et,tt;
  LocationManager locationManager; //<2>
  Geocoder geocoder; //<3>
  TextView locationText;
  MapView map;  
  MapController mapController; //<4>
  ListView wordslist;
  int m=1,j=1;
  NodeList nodes,nodes1,nodes2;
 int srclat,srclng,deslat,deslng,nxtsrclat,nxtsrclng,nxtdeslat,nxtdeslng;
 double maindestlat,maindestlng;
  int nn,val,val1;
  char cmp,cmp1,firstvar,secondvar,thirdvar;
  int nowlat,nowlon,turnlat,turn1lat,turnlon,turn1lon,lislen;
  int dirsrclat,dirsrclng,dirnxtsrclat,dirsrcnxtlng,dirdeslat,dirdeslng,dirnxtdeslat,dirnxtdeslng;
  List<GeoPoint> gee;
  int gigagigaset=0;
  String spokenword;
  boolean alar;
  int ctspeech;
  TextToSpeech tts; 
  class MapOverlay extends com.google.android.maps.Overlay
  {
 	  @Override
      public boolean draw(Canvas canvas, MapView mapView, 
      boolean shadow, long when) {
 		  super.draw(canvas, mapView, shadow);                   
 		  Point screenPts = new Point(); 	
          mapView.getProjection().toPixels(point, screenPts);         
          Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.drawingpin1_blue);            
          canvas.drawBitmap(bmp, screenPts.x-bmp.getWidth()/2, screenPts.y-bmp.getHeight()/2, null);
          
          return true;		  
 	  }
  }  
  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
	  try{
		 
		  if(keyCode==KeyEvent.KEYCODE_DPAD_DOWN)
		  {
			  Intent i=new Intent(this,SekarActivity.class);
	    	  	startActivity(i);   
			//  tts.speak("Speak after beep",TextToSpeech.QUEUE_ADD, null);
			  //Intent i=new Intent(this,SekarActivity.class);
	    	  	//startActivity(i);
		  }
		  if(keyCode==KeyEvent.KEYCODE_DPAD_UP)
		  {
			 try{
			    if(et.getText().toString().length()>0){
			  tts.speak("path confirmed",TextToSpeech.QUEUE_ADD, null);
			  java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL("http://maps.googleapis.com/maps/api/directions/xml?origin="+ Double.toString(now.getLatitude())+","+Double.toString(now.getLongitude())+"&destination="+maindestlat+","+maindestlng+"&mode=walking,OK&sensor=true").openStream());
				 FileOutputStream fos = openFileOutput("maps1.xml", Context.MODE_WORLD_READABLE);
				//java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
				 int ch;
				 fos.flush();
				 while((ch=in.read())>=0)
				 {
				      fos.write(ch);
				 }
					Toast.makeText(this,"Path Downloaded", Toast.LENGTH_SHORT).show();
					fos.close();
					in.close();
					turnget();
				    btnset=1;	
			    }
			 }catch(Exception e)
			 {
				 Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
			 }
			    return true;
		  }
		  if(keyCode==KeyEvent.KEYCODE_DPAD_LEFT)
		  {
			  btnset=0;			 		  
			  return true;
		  }		  
		  if(keyCode==KeyEvent.KEYCODE_DPAD_RIGHT)
		  {
			  gigagigaset=0;			  
			  return true;	  
			  
		  }
      if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) 
      {
    	  
    	   gigagigaset=1;
    	  
          return true;
      }
      
      if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) 
      {
    	  
    	  int r=1,ct=0;	   
		   for(Address address:sampaddr)
		   {
			   if(r==1){
			   giga=address.getAddressLine(0);
			    tts.speak(giga,TextToSpeech.QUEUE_ADD,null);
			   if(ct==2)
			   r=2;
			   ct++;
			   }
		   }		   
	
		  return true;
		   
      }   
	  }catch(Exception e)
	  {
		  Toast.makeText(this,e.toString()+"on Text to speech",Toast.LENGTH_LONG).show();
	  }
	  		return super.onKeyLongPress(keyCode, event);
 }   
  
  
  public void hey(View v)
  {
	    btnset=0;	    
  }
  
  public void why(View v)
  {
	  et=(EditText)findViewById(R.id.et);
	 try{
		 if(et.getText().length()==0)
		 {
			 	tts.speak("speak again", TextToSpeech.QUEUE_ADD,null); 
		 }
		 else
		 {
			 String dei=new String();
			 dei=et.getText().toString();
			 StringBuffer dei1=new StringBuffer(dei);				
			 for(int h=0;h<et.length();h++)
			 {
				 if((int)dei1.charAt(h)==32)
				 {
						dei1.setCharAt(h,'+');
					
				 }
			 }
		 Toast.makeText(this,dei1, Toast.LENGTH_LONG).show();			 
		 java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL("http://maps.googleapis.com/maps/api/directions/xml?origin="+ Double.toString(now.getLatitude())+","+Double.toString(now.getLongitude())+"&destination="+dei1+"&mode=walking,OK&sensor=true").openStream());
		 FileOutputStream fos = openFileOutput("maps1.xml", Context.MODE_WORLD_READABLE);		
		 int ch;
		 fos.flush();
		 while((ch=in.read())>=0)
		 {
		      fos.write(ch);
		 }
			Toast.makeText(this,"path Downloaded", Toast.LENGTH_SHORT).show();
			fos.close();
			in.close();		
			turnget();
			 btnset=1;	
		 }
	 }catch(Exception e)
	 {
		 Toast.makeText(this,"machi"+e.toString(), Toast.LENGTH_SHORT).show();
	 }	  
  }   
  
  
  void turnget(){
	  try	  {
	  	  alar=true;
	  	  XPathFactory factory = XPathFactory.newInstance();
	  	  XPath xpath = factory.newXPath();
	  	  FileInputStream xmlfile =openFileInput("maps1.xml");
	  	  InputSource inputXml = new InputSource(xmlfile);	  	
	  	  try{
	  		  nodes = (NodeList) xpath.evaluate("//leg/step["+stepno+"]", inputXml, XPathConstants.NODESET);
	  	  }catch(Exception e)
	  	  {
	  		  Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
	  	  }
	  String seds=new String();
	  	  try{
	  	  seds= nodes.item(0).getChildNodes().item(7).getTextContent();
	  	  seds=seds.replace(" ",  "");
	  	  seds=seds.replace("\n","");
	  	 // Toast.makeText(this,"poly line"+seds,Toast.LENGTH_LONG).show();
	  	  }catch(Exception e)
	  	  {
	  		  Toast.makeText(this,"m"+e.toString(), Toast.LENGTH_SHORT).show();
	  	  }
	  	
	  	  xmlfile.close(); 
	  	//  String sds=new String(seds);
	  	  
	  /*	  String seds=new String();
	  	  seds="kycnAkdcbNETCL?J@~A@rABbB?H?fB?lA@`DNxCDrBDlAPnD@rA?h@?`@Mj@WfAs@`DYpAAFq@pC[zBCXKnAC|B?^@nBBl@FxAFbCDhA@j@DrBD`BDnADXFTBFLVp@|@zAnBjAxAVZnBzBhBvBd@t@N\\?@";*/
	  	  try{	  	  
	  	  gee=decodePoly(seds);	  	 
	  	  GeoPoint turn=gee.get(gee.size()-1);
	  	  int turnlat=turn.getLatitudeE6();
	  	  int turnlon=turn.getLongitudeE6();
	  	 // Toast.makeText(this," 11"+turnlat, Toast.LENGTH_SHORT).show();
	  	  //Toast.makeText(this,seds.length()+"len", Toast.LENGTH_SHORT).show();
	  	  }catch(Exception e)
	  	  {
	  		  Toast.makeText(this,seds.length()+e.toString(), Toast.LENGTH_SHORT).show();
	  	  }
	  }catch(Exception e){
	  }


	  }
  int getno()
  {
      NodeList n;
      try
      {
      XPathFactory fa = XPathFactory.newInstance();
      XPath xpath=fa.newXPath();
      FileInputStream xmlfile =openFileInput("maps1.xml");
      InputSource inputXml = new InputSource(xmlfile);           
     n= (NodeList) xpath.evaluate("//leg/step", inputXml, XPathConstants.NODESET);
               //System.out.println();
       nn=n.getLength();
      
      }catch(Exception e)
      {            
          System.out.println(e.toString());               
      }
               
      return nn;
  }
 
  public List<GeoPoint> decodePoly(String encoded) {
	    List<GeoPoint> poly = new ArrayList<GeoPoint>();
	    FileOutputStream   fos=null;
	  //  Toast.makeText(this,"poly line in",Toast.LENGTH_LONG).show();
	    try{
	      fos = openFileOutput("kjk.txt", Context.MODE_WORLD_WRITEABLE);
	    }catch(Exception e)
	    {
	    	
	    }
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;
	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;
	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E5),
	             (int) (((double) lng / 1E5) * 1E5));
	        try{
	        fos.write(p.toString().getBytes());
	        fos.write("\n".getBytes());
  
	        }catch(Exception e)
	        {
	        	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG);
	        }
	    
	        poly.add(p);
	    }
	    try{
	    	Toast.makeText(this,"values copied",Toast.LENGTH_LONG).show();
	    fos.close();}catch(Exception e){}
	    return poly;
}
  
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
  }
  
  @Override
  public void onCreate(Bundle savedInstanceState) {	  
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    tts=new TextToSpeech(getBaseContext(),this);     
    ttsinitialise();    
    findcurrentlocation();		   
    }
  public void ttsinitialise()
  {
	  //Toast.makeText(this,"TTS initialised",Toast.LENGTH_LONG).show();
	  Locale loc = new Locale("eng", "","");
	    if(tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE){
	       tts.setLanguage(loc);        
	        }
	    tts.speak("started",TextToSpeech.QUEUE_ADD, null);
	
  } 
  public String geocodedgcode(List<Address> sewd)
  {	   int r=1;	   
	   for(Address address:sewd)
	   {
		   
		   if(r==1){
		   maindestlat=address.getLatitude();
		   maindestlng=address.getLongitude();
		   giga1=address.getAddressLine(0);
		   one=address.getAddressLine(1);
		   two=address.getAddressLine(2);
		   r=2;
		   }
	   }
	   //Toast.makeText(this,giga1+"giga1",Toast.LENGTH_LONG ).show();
	  // Toast.makeText(this,one+"one",Toast.LENGTH_LONG ).show();
	  // Toast.makeText(this,two+"two",Toast.LENGTH_LONG ).show();
	   if(giga1.length()!=0&&one.length()!=0&&two.length()!=0){
		//   Toast.makeText(this,giga1+"I was clubbed",Toast.LENGTH_LONG ).show();		 	
		 	try{
		 tts.speak("you said",TextToSpeech.QUEUE_ADD, null);
		 tts.speak(giga1,TextToSpeech.QUEUE_ADD, null);
		 tts.speak(one,TextToSpeech.QUEUE_ADD, null);
		 tts.speak(two,TextToSpeech.QUEUE_ADD, null);
		  giga1=giga1.concat(" "+one);	 
		  giga1=giga1.concat(" "+two);
		//  Toast.makeText(this,giga1+"I was clubbed",Toast.LENGTH_LONG ).show();		
		 	}catch(Exception e)
		 	{
		 		Toast.makeText(this,"voice mistake",Toast.LENGTH_LONG).show();
		 	}		  
		}
	   else
		   if(giga1.length()!=0&&one.length()!=0){		   
			//    Toast.makeText(this,giga1+"I was clubbed",Toast.LENGTH_LONG ).show();
			   giga1=giga1.concat(" "+one);;
			   	tts.speak("you said",TextToSpeech.QUEUE_ADD, null);
			   	tts.speak(giga1,TextToSpeech.QUEUE_ADD, null);
			   	tts.speak(one,TextToSpeech.QUEUE_ADD, null);		   
		   										 }
	   else	   
	   if(giga1.length()!=0){	   	   
		  // Toast.makeText(this,giga1+"I was clubbed",Toast.LENGTH_LONG ).show();
	   tts.speak("you said",TextToSpeech.QUEUE_ADD, null);
	   tts.speak(giga1,TextToSpeech.QUEUE_ADD, null);
	   }
	   return giga1;
  }
  
  @SuppressWarnings("null")
public void voiceuse()
  {
	  try{
	  Bundle speech=getIntent().getExtras();
	  if(speech!=null)
	  {
		  String pass=speech.getString("pass");
		  if(pass.equals("passcheck"))
		  {
			  		s=speech.getString("myte");
			  		Toast.makeText(this,s, Toast.LENGTH_LONG).show();
			  		try{
			  			et=(EditText)findViewById(R.id.et);
			  			Geocoder gcods=new Geocoder(this);
			  			List<Address> addresses;
			  			try{
			  			addresses=gcods.getFromLocationName(s,10);
			  			four=geocodedgcode(addresses);			  					  			
			  			}catch(Exception e)
			  			{
			  				Toast.makeText(this,"geocode exception"+e.toString(), Toast.LENGTH_LONG).show();
			  			}			  			
  					  	if(four!=null&&!four.equals("nullnullnull"))
  					  		{
  						 	et.setText(four);
   						 	ctspeech=1;
  					  		} 	 					  
			  			}catch(Exception e)
			  			{
			  				Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();			  				
			  			}	  				  
			  	}
		  else
		  {
			  
		  }		  
		
		//Toast.makeText(this,speech.toString()+"speech", Toast.LENGTH_LONG).show();		  
	  }
	  }catch(Exception e)
	  {
	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();	  
	  }
 } 
  @Override
  protected void onPause() {
    super.onPause();
    locationManager.removeUpdates(this); //<8>
  }  
  @Override
  public void onResume()
  {
	  try{
	  super.onResume();	 
	  locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);	 
  }catch(Exception e)
  			{	  
  			}
  }
@Override
public void onLocationChanged(Location location) {
	// TODO Auto-generated method stub
	now=location;
	Toast.makeText(this,"came on"+turn1lat, Toast.LENGTH_LONG).show();
	Log.d(TAG, "onLocationChanged with location " + location.toString());
    gcode(location);    
    nowlat = (int)(location.getLatitude() * 100000);
    nowlon = (int)(location.getLongitude() * 100000);
	if(btnset==1)
	{
		alarmcal();	
	}
	
	if(gigagigaset==1)
	{
		 try
         {
       HttpClient sdf=new DefaultHttpClient();
       HttpPost pos=new HttpPost("http://10.0.2.2/sssss.php");
       List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
       nameValuePairs.add(new BasicNameValuePair("uname",Double.toString(location.getLatitude())));
       nameValuePairs.add(new BasicNameValuePair("uname1",Double.toString(location.getLongitude())));
       pos.setEntity(new UrlEncodedFormEntity(nameValuePairs));
        HttpResponse res=sdf.execute(pos);
       HttpEntity etr=res.getEntity();        
    //   Toast.makeText(this, EntityUtils.toString(etr),Toast.LENGTH_LONG).show();        
   	//Toast.makeText(this,"done",Toast.LENGTH_LONG).show();    	
         }catch(Exception e)
   {
   	Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
   }    
	}
    //Toast.makeText(this," "+ now.getLatitude(),Toast.LENGTH_LONG).show();
}


void getturnval()

{
	  XPathFactory factory = XPathFactory.newInstance();
	  XPath xpath = factory.newXPath();
	  XPathFactory factory1 = XPathFactory.newInstance();
	  XPath xpath1=factory1.newXPath();
	  try{
	  FileInputStream xmlfile =openFileInput("maps1.xml");
	  InputSource inputXml = new InputSource(xmlfile);
   nodes = (NodeList) xpath.evaluate("//leg/step["+stepno+"]", inputXml, XPathConstants.NODESET);
    for (int i = 0, n = nodes.getLength(); i < n; i++) {        
        srclat=(int)(Double.valueOf(nodes.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent()).doubleValue()*100000);            
        deslat=(int) (Double.valueOf(nodes.item(0).getChildNodes().item(5).getChildNodes().item(1).getTextContent()).doubleValue()*100000);
        srclng=(int)(Double.valueOf(nodes.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent()).doubleValue()*100000);
        deslng=(int)(Double.valueOf(nodes.item(0).getChildNodes().item(5).getChildNodes().item(3).getTextContent()).doubleValue()*100000);
    //   Toast.makeText(this,"src lat is"+srclat,Toast.LENGTH_LONG).show();
    //   Toast.makeText(this,"des lat is"+deslat,Toast.LENGTH_LONG).show();
       //Toast.makeText(this,"src lng is"+srclng,Toast.LENGTH_LONG).show();
     //  Toast.makeText(this,"des lng is"+deslng,Toast.LENGTH_LONG).show();
        }          
    xmlfile.close();		
  	FileInputStream xx=openFileInput("maps1.xml");
  	 InputSource inputXl = new InputSource(xx);
    	try{
    	      nodes1 =  (NodeList) xpath1.evaluate("//leg/step["+(stepno+1)+"]", inputXl, XPathConstants.NODESET);
    	}catch(Exception e)
    	{
    		Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
    	}
    	  //  Toast.makeText(this,"machi"+nodes1.getLength(),Toast.LENGTH_LONG).show();
        for ( int i = 0, n = nodes1.getLength(); i < n; i++) {        
      //	  Toast.makeText(this,"stepno"+stepno,Toast.LENGTH_LONG).show();
            nxtsrclat=(int)(Double.valueOf(nodes1.item(0).getChildNodes().item(3).getChildNodes().item(1).getTextContent()).doubleValue()*100000);
            //Toast.makeText(this,"machi"+nodes1.getLength(),Toast.LENGTH_LONG).show();
            nxtdeslat= (int)(Double.valueOf(nodes1.item(0).getChildNodes().item(5).getChildNodes().item(1).getTextContent()).doubleValue()*100000);
            nxtsrclng=(int)(Double.valueOf(nodes1.item(0).getChildNodes().item(3).getChildNodes().item(3).getTextContent()).doubleValue()*100000);
            nxtdeslng=(int)(Double.valueOf(nodes1.item(0).getChildNodes().item(5).getChildNodes().item(3).getTextContent()).doubleValue()*100000);
        //     Toast.makeText(this,"nxtsrc lat is"+nxtsrclat,Toast.LENGTH_LONG).show();
           //Toast.makeText(this,"nxtdes lat is"+Double.valueOf(nxtdeslat).toString(),Toast.LENGTH_LONG).show();
          //Toast.makeText(this,"nxtsrc lng is"+Double.valueOf(nxtsrclng).toString(),Toast.LENGTH_LONG).show();
           // Toast.makeText(this,"next des lng is"+Double.valueOf(nxtdeslng).toString(),Toast.LENGTH_LONG).show();
      xx.close();
        }
    
	  }catch(Exception  e)
	  {		 
		  Toast.makeText(this, e.toString(),Toast.LENGTH_LONG);		  
	  }
	  
	 // turnvalcal();  
}

public void getturnval4()
{
	
	  String sd=nodes.item(0).getChildNodes().item(11).getTextContent();
	
    sd=sd.replace("\n","");
	  char arr[]=sd.toCharArray();
	  String dd=" ";int qw=0;int qw1=0;
	  for(int i=0;i<arr.length-1;i++)
    {
		  if(arr[i]==' '&&qw==0){
		   sd=sd.replace(" ",  "");
		   qw=1;
		  }
		  if(arr[i]=='\n')
		  {
			  sd=sd.replace("\n","");
			  
		  }
        while(arr[i]=='<'){
      	  
      	  
        if(arr[i]=='<')
        {
           i++;
           while(arr[i]!='>') 
           {
               i++;
           }
           if(i==arr.length-1)
                break;
            i++;
            
            
        }
        }
        if(i<arr.length-1)
       dd+=arr[i];
        
       //dd+=arr[i];
       //dd+="\\0";
       //  System.out.println(dd);
    } 
	  Toast.makeText(this, dd,Toast.LENGTH_LONG).show();	  
//tts.speak(dd,TextToSpeech.QUEUE_ADD,null);
}

public void alarmcal()
{
	  try{
	  int noofsteps=getno();
	  getturnval();
	  if(stepno<=noofsteps)
	  {
		  
		  	turnget();
		  	if((nowlat==gee.get(gee.size()-1).getLatitudeE6()||nowlat==gee.get(gee.size()-1).getLatitudeE6()+1||nowlat==gee.get(gee.size()-1).getLatitudeE6()+2||nowlat==gee.get(gee.size()-1).getLatitudeE6()+3||nowlat==gee.get(gee.size()-1).getLatitudeE6()-1||nowlat==gee.get(gee.size()-1).getLatitudeE6()-2||nowlat==gee.get(gee.size()-1).getLatitudeE6()-3)
		  	&&(nowlon==gee.get(gee.size()-1).getLongitudeE6()||nowlon==gee.get(gee.size()-1).getLongitudeE6()+1||nowlon==gee.get(gee.size()-1).getLongitudeE6()+2||nowlon==gee.get(gee.size()-1).getLongitudeE6()+3||nowlon==gee.get(gee.size()-1).getLongitudeE6()-1||nowlon==gee.get(gee.size()-1).getLongitudeE6()-2||nowlon==gee.get(gee.size()-1).getLongitudeE6()-3))
		  					{
		  						if(stepno!=noofsteps)
		  						{
		  						stepno=stepno+1;
		  						getturnval();
		  						getturnval4();
		  						}
		  						else{
		  						tts.speak("destination reached", TextToSpeech.QUEUE_ADD,null);
		  						btnset=0;}
		  					}		  
		  	else
		  	{	  		
		  		for(int q=0;q<gee.size()-2;q++)
		  		{
		  			turnlat=gee.get(q).getLatitudeE6();
		  			turn1lat=gee.get(q+1).getLatitudeE6();
		  			turnlon=gee.get(q).getLongitudeE6();
		  			turn1lon=gee.get(q+1).getLongitudeE6();
		  			val=turnlat-turn1lat;
		  			val1=turnlon-turn1lon;
		  				if(val>0)
		  				{
		  					cmp='A';
		  				}
		  				if(val<0)
		  				{
		  					cmp='B';
		  					
		  				}
		  				if(val==0)
		  				{
		  					cmp='C';
		  				}
		  				if(val1>0)
		  				{
		  					cmp1='A';
		  					
		  				}
		  				if(val1<0)
		  				{
		  					cmp1='B';
		  				}
		  				if(val1==0)
		  				{
		  					cmp1='C';
		  				}
		  				if(alar==false)
			  			{
			  				Toast.makeText(this,"correct route",Toast.LENGTH_LONG).show();
			  				break;
			  			}		  		
		  				if(cmp=='A'&&cmp1=='A')
		  				{
		  				if(equi())
		  				{
		  					alar=false;
		  				}
		  				if((nowlat<turnlat&&nowlat>turn1lat)&&(nowlon<turnlon&&nowlon>turn1lon))
		  				{
		  					alar=false;
		  				}		
		  				
		  				}		  				
		  			if(cmp=='B'&&cmp1=='A')
		  			{		  				
		  				if(equi())
		  				{
		  					alar=false;
		  				}	  				
		  				if((nowlat>turnlat&&nowlat<turn1lat)&&(nowlon<turnlon&&nowlon>turn1lon))
		  				{		  					
		  				alar=false;		  					
		  				}		  				
		  			}
		  			if(cmp=='C'&&cmp1=='A')
		  			{		  				
		  				if(equi())
		  				{
		  					alar=false;		  					
		  				}
		  				if((nowlat==turnlat||nowlat==turnlat-1||nowlat==turnlat-2||nowlat==turnlat-3||nowlat==turnlat+1||nowlat==turnlat+2||nowlat==turnlat+3)&&(nowlon<turnlon&&nowlon>turn1lon)){
		  				alar=false;
		  				}	  				
		  			}
		  			if(cmp=='A'&&cmp1=='B')
		  			{
		  				if(equi())
		  				{
		  					  			alar=false;		
		  				}
		  				if((nowlat<turnlat&&nowlat>turn1lat)&&(nowlon>turnlon&&nowlon<turn1lon))
		  				{
		  					alar=false;
		  				}

		  			}
		  			if(cmp=='B'&&cmp1=='B')
		  			{
		  				if(equi())		  					
		  				{
		  					  			alar=false;		
		  				}
		  				if((nowlat>turnlat)&&(nowlat<turn1lat)&&(nowlon>turnlon)&&(nowlon<turn1lon))
		  				{
		  					alar=false;
		  				}		  				
		  			}
		  			if(cmp=='C'&&cmp1=='B')
		  			{
		
		  				if(equi())
		  				{
		  					  			alar=false;		
		  					  		//	Toast.makeText(this ,"done",Toast.LENGTH_LONG).show();
		  				}
		  				if(((nowlat==turnlat||nowlat==turnlat-1||nowlat==turnlat-2||nowlat==turnlat-3||nowlat==turnlat+1||nowlat==turnlat+2||nowlat==turnlat+3))&&(nowlon>turnlon)&&(nowlon<turn1lon))
		  				{
		  					alar=false;
		  				}
		  			
		  				
		  			}
		  			if(cmp=='A'&&cmp1=='C')
		  			{
		  			//	Toast.makeText(this,"in"+(char) cmp+"sd "+(char)cmp1, Toast.LENGTH_LONG).show();
		  				if(equi())
		  				{
		  					alar=false;		
		  				}
		  				if((nowlat<turnlat&&nowlat>turn1lat)&&(nowlon==turnlon||nowlon==turnlon-1||nowlon==turnlon-2||nowlon==turnlon-3||nowlon==turnlon+1||nowlon==turnlon+2||nowlon==turnlon+3))
		  				{
		  					alar=false;
		  				}
		  			}
		  			if(cmp=='B'&&cmp1=='C')
		  			{
		  			//	Toast.makeText(this,"in"+(char) cmp+"sd "+(char)cmp1, Toast.LENGTH_LONG).show();
		  				if(equi())
		  				{
		  					alar=false;		
		  				}
		  				if((nowlat>turnlat&&nowlat<turn1lat)&&(nowlon==turnlon||nowlon==turnlon-1||nowlon==turnlon-2||nowlon==turnlon-3||nowlon==turnlon+1||nowlon==turnlon+2||nowlon==turnlon+3))
		  				{
		  					alar=false;
		  				} 				
		  			}
		  			if(cmp=='C'&&cmp1=='C')
		  			{
		  			//	Toast.makeText(this,"in"+(char) cmp+"sd "+(char)cmp1, Toast.LENGTH_LONG).show();
		  				if(equi())
		  				{
		  					alar=false;		
		  				}
		  			}	  			
		  		}
		  		if(alar)
		  		{		  			
		  			//Toast.makeText(this, "alarm", Toast.LENGTH_LONG).show();
		  			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		  			long milliseconds = 1000;
		  			v.vibrate(milliseconds);
		  		}	  	
		  	}		  	
	  } 
	  
	  
	//  if(stepno==noofsteps)
	  //{
		 // turnget();	 
	  //}	  
	  //Toast.makeText(this,"steps no  "+stepno,Toast.LENGTH_LONG).show();
	  
	    	  
 }catch(Exception e)
{
	  Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
}
}

boolean equi(){
	
	boolean ssa=false;
	if((nowlat==turnlat||nowlat==turnlat-1||nowlat==turnlat-2||nowlat==turnlat-3||nowlat==turnlat+1||nowlat==turnlat+2||nowlat==turnlat+3)&&
			(nowlon==turnlon||nowlon==turnlon-1||nowlon==turnlon-2||nowlon==turnlon-3||nowlon==turnlon+1||nowlon==turnlon+2||nowlon==turnlon+3))
	{
		 ssa=true;
	}
	return ssa;
}




@Override
public void onProviderDisabled(String arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onProviderEnabled(String arg0) {
	// TODO Auto-generated method stub
	
}
@Override
public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
	// TODO Auto-generated method stub	
}

@Override
protected boolean isRouteDisplayed() {
	// TODO Auto-generated method stub
	return false;
}
 
public void findcurrentlocation()
{
	 //finds location
	 //sets maps 	 
	 locationText = (TextView)this.findViewById(R.id.lblLocationInfo);
	 map = (MapView)this.findViewById(R.id.mapview);
	 map.setBuiltInZoomControls(true);    
	 mapController = map.getController(); //<4>
	 mapController.setZoom(20);
	 //retreives position from gps
	 locationManager = (LocationManager)this.getSystemService(LOCATION_SERVICE); //<2>	 
	 geocoder = new Geocoder(this); //<3>
	 Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER); //<5>
	 
	 if (location != null) {
	 Log.d(TAG, location.toString());
	 this.onLocationChanged(location); //<6>	 
	 }	
}
 public void gcode(Location location)
{
String text = String.format("Lat:\t %f\nLong:\t %f\nAlt:\t %f\nBearing:\t %f", location.getLatitude(), 
location.getLongitude(), location.getAltitude(), location.getBearing());
this.locationText.setText(text);
try {
List<Address> addresses = geocoder.getFromLocation(now.getLatitude(), now.getLongitude(), 10); //<10>
sampaddr=addresses;
for (Address address : addresses) {	 
	      this.locationText.append("\n" + address.getAddressLine(0));	     
   }
int latitude = (int)(location.getLatitude() *1000000);
int longitude = (int)(location.getLongitude()* 1000000);
 point = new GeoPoint(latitude,longitude);
 mapController.animateTo(point);
 //<11>
List<Overlay> listOfOverlays = map.getOverlays();
listOfOverlays.clear();
listOfOverlays.add(new MapOverlay());
} catch (Exception e) {
	
Log.e("LocateMe", "Could not get Geocoder data", e);
}
//Geocoding complete
}


@Override
public void onInit(int arg0) {
	// TODO Auto-generated method stub
	Locale loc = new Locale("eng", "","");
    if(tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE){
       tts.setLanguage(loc);        
        }
    voiceuse();     
}
  							 }	
 