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

    private static final int REQUEST_TURN_ON_BLUETOOTH = 1;
    private Button btn_scan;
    private Button btn_enable_bluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        plantTimberLog();
        btn_scan = findViewById(R.id.btn_scan);
        btn_enable_bluetooth = findViewById(R.id.btn_enable_bluetooth);
        initBeaconScanSettings();
        initUI();

    }

    private void plantTimberLog() {
        Timber.plant(new WoodTree(this)
                .retainDataFor(WoodTree.Period.FOREVER)
                .maxLength(100000).showNotification(true));
    }

    private void initBeaconScanSettings() {
        BeaconManager.setRegionExitPeriod(5000);
    }

    private void initUI() {
        if (bluetoothAvailable()) {
            boolean bluetoothEnabled = bluetoothEnabled();
            initBluetoothStatus(bluetoothEnabled);
            updateUI(bluetoothEnabled);
        } else {
            btn_scan.setEnabled(false);
            btn_enable_bluetooth.setEnabled(false);
        }

    }

    private boolean bluetoothAvailable() {
        return BluetoothAdapter.getDefaultAdapter() != null;
    }

    private void updateUI(boolean bluetoothEnabled) {
        if (bluetoothEnabled) {
            bindBeaconManager();
        } else {
            btn_scan.setEnabled(false);
            Timber.w(getString(R.string.scan_not_ready));
        }
    }

    private void initBluetoothStatus(boolean bluetoothEnabled) {
        if (bluetoothEnabled) {
            btn_enable_bluetooth.setText(R.string.bluetooth_enabled);
            btn_enable_bluetooth.setEnabled(false);
            btn_enable_bluetooth.setVisibility(View.INVISIBLE);
        } else {
            btn_enable_bluetooth.setText(R.string.enable_bluetooth);
            btn_enable_bluetooth.setEnabled(true);
            btn_enable_bluetooth.setVisibility(View.VISIBLE);
        }

    }

    private boolean bluetoothEnabled() {
        BluetoothManager manager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = manager.getAdapter();
        return bluetoothAdapter.isEnabled();
    }

    private void enableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_TURN_ON_BLUETOOTH);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_TURN_ON_BLUETOOTH:
                updateUI(bluetoothEnabled());
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


    private void bindBeaconManager() {
        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.addMonitorNotifier(this);
        beaconManager.bind(this);
    }

    public void toggleScanStatus(View view) {
        if (btn_scan.getText().equals(getString(R.string.scan))) {
            onScanRequested();
        } else {
            onStopRequested();
        }

    }

    private void onStopRequested() {
        stopBeacon();
        Timber.v("Beacon scanning stopped.");
        btn_scan.setText(R.string.scan);
    }

    private void stopBeacon() {
        deregisterBeaconToBeMonitored(UuidProvider.beaconToMonitored());
    }

    private void onScanRequested() {
        Timber.v("Begin to scan beacon.");
        btn_scan.setText(R.string.stop);
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

    @Override
    public void onBeaconServiceConnect() {
        Timber.v(getString(R.string.scan_ready));
        btn_scan.setEnabled(true);

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
