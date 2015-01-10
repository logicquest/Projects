package com.rajesh.newarrived;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.util.AttributeSet;
import com.morvo.newarrived.R;
 
public class Preferences extends PreferenceActivity implements OnSharedPreferenceChangeListener{
        private EditTextPreference trigger;
		private EditTextPreference pingd;
		private EditTextPreference pingt;
		//private RingtonePreference ring;


		@Override
        protected void onCreate(Bundle savedInstanceState) {
			try{
                super.onCreate(savedInstanceState);
                addPreferencesFromResource(R.xml.preferences);
                PreferenceManager.setDefaultValues(Preferences.this, R.xml.preferences, false);

               
                // Get the custom preference;
                 trigger=(EditTextPreference) findPreference("trigdist");
                 pingd=(EditTextPreference) findPreference("pdist");
                 pingt=(EditTextPreference) findPreference("ptime");
                // ring=(RingtonePreference) findPreference("ringtone");
                 /*ring.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						// TODO Auto-generated method stub
					//	ring.setSummary("Current Ringtone:"+newValue);
						return true;
					}
				});*/
                 trigger.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						// TODO Auto-generated method stub
						trigger.setSummary("Trigger Distance:"+newValue+ " mts");
						return true;
					}
				});
    pingd.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						// TODO Auto-generated method stub
						pingd.setSummary("ping Distance:"+newValue+ " mts");
						return true;
					}
				});
    pingt.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			// TODO Auto-generated method stub
			pingt.setSummary("ping time:"+newValue+" ms");
			return true;
		}
		
		
		
		
	});
			}catch(Exception e)
			{
				
			}
        }

        @Override 
        protected void onResume(){
            super.onResume();
            trigger.setSummary("Trigger Distance:"+trigger.getText().toString()+ " mts");
            pingd.setSummary("Ping Distance:"+pingd.getText().toString()+ " mts");
            pingt.setSummary("Ping Time:"+pingt.getText().toString()+" ms");
         // ring.setSummary("Current Ringtone:"+ring.getTitle());
            // Set up a listener whenever a key changes             
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
        }

        @Override 
        protected void onPause() { 
            super.onPause();
            // Unregister the listener whenever a key changes             
            PreferenceManager.getDefaultSharedPreferences(this) .unregisterOnSharedPreferenceChangeListener(this);     
        } 

       
        public class IntEditTextPreference extends EditTextPreference {

            public IntEditTextPreference(Context context) {
                super(context);
            }

            public IntEditTextPreference(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public IntEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
                super(context, attrs, defStyle);
            }

            @Override
            protected String getPersistedString(String defaultReturnValue) {
                return String.valueOf(getPersistedInt(-1));
            }

            @Override
            protected boolean persistString(String value) {
                return persistInt(Integer.valueOf(value));
            }
        }


		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			// TODO Auto-generated method stub
			 Preference pref = findPreference(key);
			    if (pref instanceof EditTextPreference) {
			        EditTextPreference etp = (EditTextPreference) pref;
			        pref.setSummary(etp.getText());
			    }
		}
}