package org.altbeacon.beaconreference;

import android.app.Activity;
import android.os.Bundle;

public class BackgroundActivity extends Activity {
	// This activity does not do anything. Its sole purpose is to obscure the
	// previous Monitoring activity so its onPause() event gets fired and the
	// beaconManager is put in background mode. If you look at LogCat, you will
	// see that Bluetooth scans take place only every 5 minutes. This
	// demonstrates how scans are slowed down to save battery life.
	protected static final String TAG = "BackgroundActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
	}

}