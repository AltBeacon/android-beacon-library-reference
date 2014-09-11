package org.altbeacon.beaconreference;

import java.util.Collection;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.powersave.BackgroundPowerSaver;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import android.app.Application;
import android.os.RemoteException;
import android.util.Log;

public class BeaconReferenceApplication extends Application implements BootstrapNotifier, RangeNotifier {
	private static final String TAG = "BeaconReferenceApplication";
	private BeaconManager mBeaconManager;
	private Region mAllBeaconsRegion;
	private MonitoringActivity mMonitoringActivity;
	private RangingActivity mRangingActivity;
	private BackgroundPowerSaver mBackgroundPowerSaver;
	@SuppressWarnings("unused")
	private RegionBootstrap mRegionBootstrap;
	
	@Override 
	public void onCreate() {
		mAllBeaconsRegion = new Region("all beacons", null, null, null);
		
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
		mBackgroundPowerSaver = new BackgroundPowerSaver(this);		
        mRegionBootstrap = new RegionBootstrap(this, mAllBeaconsRegion);
        
	
        // By default the AndroidBeaconLibrary will only find AltBeacons.  If you wish to make it
        // find a different type of beacon, you must specify the byte layout for that beacon's
        // advertisement with a line like below.  The example shows how to find a beacon with the
        // same byte layout as AltBeacon but with a beaconTypeCode of 0xaabb
        //
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=aabb,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //
        // In order to find out the proper BeaconLayout definition for other kinds of beacons, do
        // a Google search for "setBeaconLayout" (including the quotes in your search.)
	}
	
	@Override
	public void didRangeBeaconsInRegion(Collection<Beacon> arg0, Region arg1) {
		if (mRangingActivity != null) {
			mRangingActivity.didRangeBeaconsInRegion(arg0, arg1);
		}
		
	}

	@Override
	public void didDetermineStateForRegion(int arg0, Region arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void didEnterRegion(Region arg0) {
		if (mMonitoringActivity != null) {
			mMonitoringActivity.didEnterRegion(arg0);
		}		
		try {
			Log.d(TAG, "entered region.  starting ranging");
			mBeaconManager.startRangingBeaconsInRegion(mAllBeaconsRegion);
			mBeaconManager.setRangeNotifier(this);
		} catch (RemoteException e) {
			Log.e(TAG, "Cannot start ranging");
		}
	}

	@Override
	public void didExitRegion(Region arg0) {
		if (mMonitoringActivity != null) {
			mMonitoringActivity.didExitRegion(arg0);
		}				
	}
	
	public void setMonitoringActivity(MonitoringActivity activity) {
		mMonitoringActivity = activity;
	}

	public void setRangingActivity(RangingActivity activity) {
		mRangingActivity = activity;
	}
	
}
