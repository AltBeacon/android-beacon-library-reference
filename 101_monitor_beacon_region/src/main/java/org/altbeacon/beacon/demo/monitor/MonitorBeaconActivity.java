package org.altbeacon.beacon.demo.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.tonytangandroid.wood.WoodTree;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.Region;

import java.util.List;

import timber.log.Timber;

public class MonitorBeaconActivity extends AppCompatActivity
        implements MonitorNotifier, BeaconConsumer {

    private Button btn_enable_bluetooth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        plantTimberLog();
        btn_enable_bluetooth = findViewById(R.id.btn_enable_bluetooth);
        initBeaconScanSettings();


    }

    private void plantTimberLog() {
        Timber.plant(new WoodTree(this)
                .retainDataFor(WoodTree.Period.FOREVER)
                .maxLength(100000).showNotification(true));
    }

    private void initBeaconScanSettings() {
        BeaconManager.setRegionExitPeriod(5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startToScanBeacon();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScanningBeacon();
    }

    private void stopScanningBeacon() {
        if (bluetoothAvailable() && bluetoothEnabled()) {
            deregisterBeaconToBeMonitored(UuidProvider.beaconToMonitored());
            BeaconManager.getInstanceForApplication(this).removeMonitorNotifier(this);
            BeaconManager.getInstanceForApplication(this).unbind(this);
        }
    }

    private void startToScanBeacon() {
        if (bluetoothAvailable()) {
            boolean bluetoothEnabled = bluetoothEnabled();
            if (bluetoothEnabled) {
                onBluetoothReady();
            } else {
                onBluetoothOff();
            }
        } else {
            Timber.w(getString(R.string.bluetooth_not_available));
            btn_enable_bluetooth.setEnabled(false);
        }

    }

    private void onBluetoothOff() {
        Timber.w(getString(R.string.scan_not_ready));
        btn_enable_bluetooth.setText(R.string.enable_bluetooth);
        btn_enable_bluetooth.setEnabled(true);
    }

    private void onBluetoothReady() {
        btn_enable_bluetooth.setText(R.string.bluetooth_enabled);
        btn_enable_bluetooth.setEnabled(false);
        bindThenScan();
    }

    private boolean bluetoothAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    private boolean bluetoothEnabled() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = manager.getAdapter();
        return bluetoothAdapter.isEnabled();
    }

    private void enableBluetooth() {
        startActivity(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE));
    }


    private void bindThenScan() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.addMonitorNotifier(this);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        Timber.v(getString(R.string.scan_ready));
        scanBeacon();
    }

    private void scanBeacon() {
        registerBeaconToBeMonitored(UuidProvider.beaconToMonitored());
    }

    @Override
    public void didEnterRegion(Region region) {
        Timber.i("enter:%s", region.getUniqueId());
    }

    @Override
    public void didExitRegion(Region region) {
        Timber.i("exit:%s", region.getUniqueId());
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {

    }

    private void registerBeaconToBeMonitored(List<String> beacons) {
        try {
            BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
            for (String beacon : beacons) {
                Timber.v("monitor beacon : " + beacon + "\n");
                beaconManager.startMonitoringBeaconsInRegion(UuidMapper.constructRegion(beacon));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    private void deregisterBeaconToBeMonitored(List<String> beacons) {
        try {
            BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
            for (String beacon : beacons) {
                Timber.v("stop monitoring beacon : " + beacon + "\n");
                beaconManager.stopMonitoringBeaconsInRegion(UuidMapper.constructRegion(beacon));
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public void enableBluetooth(View view) {
        enableBluetooth();
    }

}
