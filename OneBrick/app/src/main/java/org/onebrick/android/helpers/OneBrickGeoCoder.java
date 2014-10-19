package org.onebrick.android.helpers;

import android.location.Address;
import android.location.Geocoder;

import org.onebrick.android.OneBrickApplication;

import java.io.IOException;
import java.util.List;

/**
 * Created by AshwinGV on 10/19/14.
 */
public class OneBrickGeoCoder {
    private static Geocoder geocoder;

    private OneBrickGeoCoder() {};

    public static Geocoder getInstance() {
        if (geocoder == null) {
            geocoder = new Geocoder(OneBrickApplication.getContext());
        }
        return geocoder;
    }

    public static boolean isPresent() {
        return geocoder.isPresent();
    }

    public static Address getAddressFromLocationName(String location) {
        List<Address> addressList = null;
        Address geoCodedAddress = null;
        try {
            addressList = geocoder.getFromLocationName(location,1);
            if(addressList == null || addressList.size() == 0) {
                String closeLocation[] = location.split(",");
                addressList = geocoder.getFromLocationName(closeLocation[1],1);
            }
            geoCodedAddress = addressList.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return geoCodedAddress;
    }

}
