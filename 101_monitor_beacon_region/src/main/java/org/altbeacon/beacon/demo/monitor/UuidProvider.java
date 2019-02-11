package org.altbeacon.beacon.demo.monitor;

import java.util.ArrayList;
import java.util.List;

public class UuidProvider {

    /**
     * Define the beacon uuid that you want to scan.
     * If you do not have any beacon in your hands, you could download the beacon locator app
     * from Radius Network.
     * <p>
     * https://play.google.com/store/apps/details?id=com.radiusnetworks.locate
     * <p>
     * in Google Play, which makes it possible to make your Android device as a beacon.
     *
     * @return the list of beacon that you would like to scan.
     */
    public static List<String> beaconToMonitored() {
        List<String> result = new ArrayList<>();
        result.add("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6:1:2");
        return result;
    }
}