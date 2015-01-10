package com.rajesh.newarrived;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;


import com.morvo.newarrived.R;

public class TodoActivity extends Activity{
	

	private SharedPreferences prefs;
	private Button save;
	private ArrayList<String> todoItems;
	private String[] todosfromsp=null;
	private Button cancel;
	private ListView myListView;
	private ArrayAdapter<String> aa;
	Boolean expire=false;
	public void gett()
	{
		
		try{
		
		setContentView(R.layout.addtodo);
		
		save=(Button) findViewById(R.id.save);
		cancel=(Button) findViewById(R.id.cancel);
		todoItems = new ArrayList<String>();
		prefs=getSharedPreferences("Alarm1", MODE_PRIVATE);
		String sa=prefs.getString("todos", null);
		
		if(sa!=null){
		todosfromsp=getStringArray(sa);
		for(String s:todosfromsp)
		{
			todoItems.add(s);
		}
		}
	    myListView = (ListView)findViewById(R.id.listView1);
	    final EditText myEditText = (EditText)findViewById(R.id.editText1);
	    aa = new ArrayAdapter<String>(this,R.layout.listtextview,todoItems);
	      myListView.setAdapter(aa);
	    ImageButton add=(ImageButton) findViewById(R.id.imageButton1);
	  add.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
			 todoItems.add(0, myEditText.getText().toString());
             aa.notifyDataSetChanged();
             myEditText.setText("");
         	
             
             
		}
	});
	  save.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Editor edit=prefs.edit();
				 String[] temp=todoItems.toArray(new String[todoItems.size()]);
				edit.putString("todos", setStringArray(temp));
				edit.commit();
				startActivity(new Intent(getApplicationContext(),ArrivedNow.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
			}
		})  ;  
	  
	  
	  
	 
	  cancel.setOnClickListener(new OnClickListener() {
		
		public void onClick(View v) {
			// TODO Auto-generated method stub
		
			startActivity(new Intent(getApplicationContext(),ArrivedNow.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	});
	  registerForContextMenu(myListView);
		}catch(Exception e)
		{
			
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
	}

	
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 // Get references to UI widgets
		
		TextView txtvie=(TextView)findViewById(R.id.textView2);
		try{
		 SharedPreferences DiSp=getApplicationContext().getSharedPreferences("Display",getApplicationContext().MODE_PRIVATE);
		 if(DiSp.getInt("viewpat",1)==1){
		gett();
		expire=false;
		 txtvie.setVisibility(4);
		 }
		 else{
			 gett();
			 cancel=(Button) findViewById(R.id.cancel);
			 EditText myEditText = (EditText)findViewById(R.id.editText1);
			  ImageButton add=(ImageButton) findViewById(R.id.imageButton1);
			  save=(Button) findViewById(R.id.save);
			  cancel.setVisibility(4);
			  myEditText.setVisibility(4);
			  add.setVisibility(4);
			  save.setVisibility(4);
			  txtvie.setVisibility(0);
			  expire=true;
				
			  
			  
			  
			  
			  
		 }
		}catch(Exception e)
		{
			
		}

	}
	@Override
	  public void onCreateContextMenu(ContextMenu menu, 
	                                  View v, 
	                                  ContextMenu.ContextMenuInfo menuInfo) {
	    
	    try{
	    	super.onCreateContextMenu(menu, v, menuInfo);

	    menu.setHeaderTitle("Selected To Do Item");
	    menu.add(0, Menu.FIRST, Menu.NONE, "Remove");
	    }catch(Exception e)
	    {
	    	
	    }
	  }
	  
	  @Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    super.onOptionsItemSelected(item);
try{
	    int index = myListView.getSelectedItemPosition();

	    switch (item.getItemId()) {
	      case (Menu.FIRST): {
	      
	          removeItem(index);
	          return true;
	      }
	    
	    }

}catch(Exception e)
{
	
}
	    return false;
	  }
	  
	  @Override
	  public boolean onContextItemSelected(MenuItem item) {  
		  
		 try{ 
	    super.onContextItemSelected(item);
	    switch (item.getItemId()) {
	      case (Menu.FIRST): {
	        AdapterView.AdapterContextMenuInfo menuInfo;
	        menuInfo =(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	        int index = menuInfo.position;

	        removeItem(index);
	        return true;
	      }
	    } 
	    
		 }catch(Exception e)
		 {
			 
		 }
	    return false;
	  }




	  private void removeItem(int _index) {
		  try{
	    todoItems.remove(_index);
	    aa.notifyDataSetChanged();
		  }catch(Exception e)
		  {
			  
		  }
	  
	  }
	public String setStringArray(String[] stringarray) {
		
		StringBuffer packedString = new StringBuffer();
		try{		
		for (int i = 0; i < stringarray.length; i++) {
			if (i > 0) {
				packedString.append(",");
			}
			packedString.append(stringarray[i]);
		}
		}catch(Exception e)
		{
			
		}
		return packedString.toString();
	}

	public String[] getStringArray(String packedString) {
		
		return packedString.split(",");
	}
	 public void onBackPressed() {

		try{	
									
									
			if(expire)
				{
							
			
			  
				
				}
			TodoActivity.this.finish();
									
		}catch(Exception e)
		{
			
		}

		}

}
