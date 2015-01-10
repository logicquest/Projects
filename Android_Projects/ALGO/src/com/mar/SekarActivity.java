 package com.mar;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * A very simple application to handle Voice Recognition intents
 * and display the results
 */
public class SekarActivity extends Activity
{
	private static final int REQUEST_CODE = 1234;
    private ListView wordsList;
    String a=new String();
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summa);
        Toast.makeText(this, "sekar",Toast.LENGTH_LONG);
        Button b=(Button)findViewById(R.id.button1);
         b.setText("sekar");
         startVoiceRecognitionActivity();
       // Disable button if no recognition service is present
    }

    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
    	
    	startVoiceRecognitionActivity();
    }

    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {  
    	try
    	{
    	
    	  //  Toast.makeText(this, "sekar", Toast.LENGTH_SHORT).show();
    	Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
               RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    	}catch(Exception e)
    	{
    	      Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    	}
    }

    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	try
    	{
    	//voice recognition starts
    	 wordsList = (ListView) findViewById(R.id.list);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
          // Populate the wordsList with the String values the recognition engine thought it heard
               ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            //wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
              //      matches));
            a= matches.get(0);
           // TextView t=(TextView)findViewById(R.id.mytext);
            //t.setText(a);
           Toast.makeText(SekarActivity.this, a, Toast.LENGTH_SHORT).show();           	
        }
        super.onActivityResult(requestCode, resultCode, data);
        Intent j=new Intent(this,MarakannaActivity.class);
        j.putExtra("pass","passcheck");
        j.putExtra("myte", a);
        startActivity(j);
    	}catch(Exception e)
    	{
    	      Toast.makeText(SekarActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
    	}
    }   
        
}