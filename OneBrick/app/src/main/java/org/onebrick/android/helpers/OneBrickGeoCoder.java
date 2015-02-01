package org.onebrick.android.helpers;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.util.Log;

import org.onebrick.android.core.OneBrickApplication;

import java.util.List;

/**
 * Created by AshwinGV on 10/19/14.
 */
public class OneBrickGeoCoder {
    private static Geocoder geocoder;
    private static final String TAG = OneBrickGeoCoder.class.getName().toString();

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

   @Nullable
    public static Address getAddressFromLocationName(String location) {
        Log.d(TAG,"Get lat lng for address"+location);
        List<Address> addressList = null;
        Address geoCodedAddress = null;
        try {
            addressList = geocoder.getFromLocationName(location,1);
            if(addressList == null || addressList.size() == 0) {
                if(location.contains(",")) {
                    String closeLocation[] = location.split(",");
                    addressList = geocoder.getFromLocationName(
                            closeLocation[(closeLocation.length)-1],1);
                } else {
                    return null;
                } 

            }
            if(addressList != null && !addressList.isEmpty()) {
                geoCodedAddress = addressList.get(0);
            }
        } catch (Exception e) {
            Log.e("Geocoder Error","Something wrong");
            e.printStackTrace();
        }
        return geoCodedAddress;
    }

}
