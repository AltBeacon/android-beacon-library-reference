package org.altbeacon.beaconreference;

import java.util.Collection;

import android.app.Activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;

import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

public class RangingActivity extends Activity {
    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ranging);
    }
    @Override 
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override 
    protected void onPause() {
    	super.onPause();
    	// Tell the Application not to pass off ranging updates to this activity
    	((BeaconReferenceApplication)this.getApplication()).setRangingActivity(null);
    }
    @Override 
    protected void onResume() {
    	super.onResume();
    	// Tell the Application to pass off ranging updates to this activity
    	((BeaconReferenceApplication)this.getApplication()).setRangingActivity(this);
    	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }    

    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        if (beacons.size() > 0) {
        	EditText editText = (EditText)RangingActivity.this
					.findViewById(R.id.rangingText);
            for (Beacon beacon: beacons) {
            	logToDisplay("Beacon "+beacon.toString()+" is about "+beacon.getDistance()+" meters away, with Rssi: "+beacon.getRssi());            	
            }
        }
    }

    private void logToDisplay(final String line) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	    	EditText editText = (EditText)RangingActivity.this
    					.findViewById(R.id.rangingText);
    	    	editText.append(line+"\n");            	
    	    }
    	});
    }
}
