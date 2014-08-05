package org.altbeacon.beaconreference;

import android.os.Bundle;
import android.os.RemoteException;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

/**
 * 
 * @author dyoung
 * @author Matt Tyler
 */
public class MonitoringActivity extends Activity implements BeaconConsumer {
	protected static final String TAG = "MonitoringActivity";
    private BeaconManager beaconManager;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_monitoring);
		verifyBluetooth();

        beaconManager = org.altbeacon.beacon.BeaconManager.getInstanceForApplication(this);

        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb
        //
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

	    beaconManager.bind(this);
	    
		//initializing simulated beacons
		//BeaconManager.setBeaconSimulator(new TimedBeaconSimulator() );
		//((TimedBeaconSimulator) BeaconManager.getBeaconSimulator()).createTimedSimulatedBeacons();
	}
	
	public void onRangingClicked(View view) {
		Intent myIntent = new Intent(this, RangingActivity.class);
		this.startActivity(myIntent);
	}

	private void verifyBluetooth() {

		try {
			if (!BeaconManager.getInstanceForApplication(this).checkAvailability()) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Bluetooth not enabled");			
				builder.setMessage("Please enable bluetooth in settings and restart this application.");
				builder.setPositiveButton(android.R.string.ok, null);
				builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						finish();
			            System.exit(0);					
					}					
				});
				builder.show();
			}			
		}
		catch (RuntimeException e) {
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Bluetooth LE not available");			
			builder.setMessage("Sorry, this device does not support Bluetooth LE.");
			builder.setPositiveButton(android.R.string.ok, null);
			builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
		            System.exit(0);					
				}
				
			});
			builder.show();
			
		}
		
	}	

    @Override 
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }

    private void logToDisplay(final String line) {
    	runOnUiThread(new Runnable() {
    	    public void run() {
    	    	EditText editText = (EditText)MonitoringActivity.this
    					.findViewById(R.id.monitoringText);
       	    	editText.append(line+"\n");            	    	    		
    	    }
    	});
    }
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setMonitorNotifier(new MonitorNotifier() {
        @Override
        public void didEnterRegion(Region region) {
          logToDisplay("I just saw a beacon named "+ region.getUniqueId() +" for the first time!" );
        }

        @Override
        public void didExitRegion(Region region) {
        	logToDisplay("I no longer see a beacon named "+ region.getUniqueId());
        }

        @Override
        public void didDetermineStateForRegion(int state, Region region) {
        	logToDisplay("I have just switched from seeing/not seeing beacons: "+state);
        }


        });

        try {
        	beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        	
        } catch (RemoteException e) {   }
    }
	
}
