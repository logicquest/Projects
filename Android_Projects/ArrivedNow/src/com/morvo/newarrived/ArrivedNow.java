package com.rajesh.newarrived;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import com.morvo.newarrived.R;


public class ArrivedNow extends TabActivity {

	private TabHost mTabHost;
	private Intent intent;
	final public int ABOUT = 0;
	private void setupTabHost() {
		mTabHost = (TabHost) findViewById(android.R.id.tabhost);
		mTabHost.setup();
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// construct the tabhost
		setContentView(R.layout.main);

		setupTabHost();
		mTabHost.getTabWidget().setDividerDrawable(R.drawable.tab_divider);

		setupTab(new TextView(this), "Set Alarm");
		setupTab(new TextView(this), "Manage");

		mTabHost.setCurrentTab(0);
		
	}

	private void setupTab(final View view, final String tag) {
		View tabview = createTabView(mTabHost.getContext(), tag);
		
		if(tag=="Set Alarm")
			{intent = new Intent().setClass(this, SetAlarmActivity.class);}
		else 
		{ intent = new Intent().setClass(this, ManageActivity.class);}
		//else
		//intent = new Intent().setClass(this, Preferences.class);
	
		TabSpec setContent = mTabHost.newTabSpec(tag).setIndicator(tabview).setContent(intent);
		mTabHost.addTab(setContent);

	}

	private static View createTabView(final Context context, final String text) {
		View view = LayoutInflater.from(context).inflate(R.layout.tabs_bg, null);
		//view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);
		return view;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu,menu);
	    return true;
	}
	//Dynamically delete tabs, then add one again
	//Problem with SDK 1.1 returns null pointer exception
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
	        case R.id.About:
	        	{AboutDialog about = new AboutDialog(this);
	        	about.setTitle("About ");
	        	about.show();
	        	return true;}
		case R.id.Settings:
			Intent intent=new Intent(ArrivedNow.this,Preferences.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
	        	startActivity(intent);
	            return true;
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	
	}

}
