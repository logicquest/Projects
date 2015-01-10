package com.mar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
public class TexttospeechActivity extends Activity implements OnInitListener,OnUtteranceCompletedListener {
private EditText words = null;
private Button speakBtn = null;
private static final int REQ_TTS_STATUS_CHECK = 0;
private static final String TAG = "TTS Demo";
private TextToSpeech mTts;
/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.tts);
words = (EditText)findViewById(R.id.wordsToSpeak);
speakBtn = (Button)findViewById(R.id.speak);

speakBtn.setOnClickListener(new OnClickListener() {
@Override
public void onClick(View view) {
	
Bundle can=getIntent().getExtras();
if(can!=null)
mTts.speak(can.getString("hmm"), TextToSpeech.QUEUE_ADD, null);
else
{
	mTts.speak("retry", TextToSpeech.QUEUE_ADD, null);	
}
}});
// Check to be sure that TTS exists and is okay to use
Intent checkIntent = new Intent();
checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
}
protected void onActivityResult(int requestCode, int resultCode, Intent data) {
if (requestCode == REQ_TTS_STATUS_CHECK) {
switch (resultCode) {
case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
// TTS is up and running
mTts = new TextToSpeech(this, this);
Log.v(TAG, "Pico is installed okay");

break;
case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
// missing data, install it
Log.v(TAG, "Need language stuff: " + resultCode);
Intent installIntent = new Intent();
installIntent.setAction(
TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
startActivity(installIntent);
break;
case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
default:
Log.e(TAG, "Got a failure. TTS apparently not available");
}
}
else {
	
// 	Got something else
	
}
}
@Override
public void onInit(int status) {
// Now that the TTS engine is ready, we enable the button
if( status == TextToSpeech.SUCCESS) {
speakBtn.setEnabled(true);
Bundle can=getIntent().getExtras();
if(can!=null){
mTts.speak(can.getString("hmm"), TextToSpeech.QUEUE_ADD, null);
try
{
Thread.sleep(1000);
}catch(Exception e)
{
	
}

Intent ff=new Intent(this,MarakannaActivity.class);
startActivity(ff);



}
else
{
	mTts.speak("retry", TextToSpeech.QUEUE_ADD, null);	
}

}

}
@Override
public void onPause()
{
super.onPause();
// if we're losing focus, stop talking
if( mTts != null)
mTts.stop();
}
@Override
public void onDestroy()
{
super.onDestroy();
mTts.shutdown();
}
@Override
public void onUtteranceCompleted(String utteranceId) {
	// TODO Auto-generated method stub

	
}
}