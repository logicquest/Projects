package com.rajesh.newarrived;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.morvo.newarrived.R;


public class splash extends Activity{
private long splashdelay=4000;
@Override
public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
 
    setContentView(R.layout.splash);
    final ImageView imageView = (ImageView) findViewById(R.id.imageView1);
   // imageView.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.alpha_translate));   
    TimerTask timerTask;
    Timer timer;

    timerTask = new TimerTask() {

        @Override
        public void run() {
        	 finish();
             Intent mainIntent=new Intent().setClass(splash.this, ArrivedNow.class);
			 startActivity( mainIntent );
           
        }
    };

    timer = new Timer();
    timer.schedule(timerTask, splashdelay); 
    

}

}

