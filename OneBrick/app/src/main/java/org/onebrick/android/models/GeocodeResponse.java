package org.onebrick.android.models;


import java.util.List;

public class GeocodeResponse {
    private String status;
    private List<Result> results;
    private static final double ERROR_LAT_LON = 181.0;

    public boolean isSuccess() {
        return "OK".equals(status);
    }


    public double getLatitude() {
        if (results != null && results.size() > 0) {
            if (results.get(0).geometry != null && results.get(0).geometry.location != null) {
                return results.get(0).geometry.location.lat;
            }
        }
        return ERROR_LAT_LON;
    }

    public double getLongitude() {
        if (results != null && results.size() > 0) {
            if (results.get(0).geometry != null && results.get(0).geometry.location != null) {
                return results.get(0).geometry.location.lng;
            }
        }
        return ERROR_LAT_LON;
    }

    public static class Result {
        private Geometry geometry;


    }

    public static class Geometry {
        private Location location;

    }

    public static class Location {
        private double lat;
        private double lng;

    }

}
